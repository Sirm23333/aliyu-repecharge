package com.aliyun.mini.scheduler.server;

import com.aliyun.mini.scheduler.constans.ObjectFactory;
import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class SchedulerServer {

    private static final Logger logger = Logger.getLogger(SchedulerServer.class.getName());

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
        logger.info("Started scheduler server listening on " + port);
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
        SchedulerServer server = new SchedulerServer(10600);
        server.start();
        server.blockUntilShutdown();
    }
}
