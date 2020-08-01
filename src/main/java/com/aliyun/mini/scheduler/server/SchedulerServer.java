package com.aliyun.mini.scheduler.server;

import com.aliyun.mini.scheduler.constants.ObjectFactory;
import com.aliyun.mini.scheduler.core.impl_0730.nodemanager.NodeContainerManagerThread;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import lombok.extern.slf4j.Slf4j;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.concurrent.TimeUnit;

@Slf4j
public class SchedulerServer {


    private final int port;
    private final Server server;

    public SchedulerServer(int port) throws IOException {
        this.port = port;
        this.server = ServerBuilder.forPort(port)
                                   .addService(ObjectFactory.SchedulerImp)
                                   .build();
    }

    public void start() throws IOException {
        server.start();
        log.info("Started scheduler server listening on " + port);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                SchedulerServer.this.stop();
            } catch (InterruptedException e) {
                e.printStackTrace(System.err);
            }
            System.err.println("Scheduler server shut down.");
        }));
    }

    public void stop() throws InterruptedException {
        if (server != null) {
            server.shutdown().awaitTermination(30, TimeUnit.SECONDS);
        }
    }

    private void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }

    public static void main(String[] args) throws Exception {
        String path = "/aliyuncnpc/scheduler/log/application.log";
        FileOutputStream puts = null;
        try {
            puts = new FileOutputStream(path,true);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        PrintStream out = new PrintStream(puts);
        System.setOut(out);
        System.setErr(out);
        NodeContainerManagerThread.start();
        log.info("nodeContainerManagerThread start...");
        SchedulerServer server = new SchedulerServer(10600);
        server.start();
        server.blockUntilShutdown();
    }
}
