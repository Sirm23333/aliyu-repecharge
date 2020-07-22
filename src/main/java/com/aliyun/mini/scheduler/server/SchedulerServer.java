package com.aliyun.mini.scheduler.server;

import com.aliyun.mini.nodeservice.client.NodeServiceClient;
import com.aliyun.mini.resourcemanager.client.ResourceManagerClient;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import schedulerproto.SchedulerGrpc;
import schedulerproto.SchedulerOuterClass.AcquireContainerReply;
import schedulerproto.SchedulerOuterClass.AcquireContainerRequest;
import schedulerproto.SchedulerOuterClass.ReturnContainerReply;
import schedulerproto.SchedulerOuterClass.ReturnContainerRequest;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;


public class SchedulerServer {

    private static final Logger logger = Logger.getLogger(SchedulerServer.class.getName());

    private final int port;
    private final Server server;
    private final ResourceManagerClient rmClient;

    public SchedulerServer(int port) throws IOException {
        this.port = port;
        this.server = ServerBuilder.forPort(port)
                                   .addService(new SchedulerService())
                                   .build();
        this.rmClient = ResourceManagerClient.New();
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
        //启动一个schedulerServer
        var server = new SchedulerServer(10600);
        server.start();
        server.blockUntilShutdown();

        //启动一个resourceManagerClient  以下两个客户端不是要放在这里，可以放在returnContainer和AcquireContainer里面
        ResourceManagerClient resourceManagerClient = ResourceManagerClient.New();

        //启动一个nodeServiceClient
        NodeServiceClient nodeServiceClient = NodeServiceClient.New();


    }

    private static class SchedulerService extends SchedulerGrpc.SchedulerImplBase {

        @Override
        public void acquireContainer(AcquireContainerRequest request,
                                     StreamObserver<AcquireContainerReply> responseObserver) {
//            AcquireContainerReply.Builder acquireContainerReply = AcquireContainerReply.newBuilder();
            responseObserver.onNext(null);
            responseObserver.onCompleted();
        }

        @Override
        public void returnContainer(ReturnContainerRequest request,
                                    StreamObserver<ReturnContainerReply> responseObserver) {
            responseObserver.onNext(null);
            responseObserver.onCompleted();
        }
    }
}
