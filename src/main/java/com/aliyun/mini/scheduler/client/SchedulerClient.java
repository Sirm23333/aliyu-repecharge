package com.aliyun.mini.scheduler.client;
import com.aliyun.mini.nodeservice.client.NodeServiceClient;
import io.grpc.Channel;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import nodeservoceproto.NodeServiceOuterClass;
import schedulerproto.SchedulerGrpc;
import schedulerproto.SchedulerOuterClass.*;

import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import static schedulerproto.SchedulerGrpc.newBlockingStub;

/**
 * 测试SchedulerServer
 */
public class SchedulerClient {
    private static final Logger logger = Logger.getLogger(SchedulerClient.class.getName());


    private final SchedulerGrpc.SchedulerBlockingStub blockingStub;

    public SchedulerClient(Channel channel) {
        blockingStub = newBlockingStub(channel);
    }

    public AcquireContainerReply acquireContainer(AcquireContainerRequest req) {
        return blockingStub.acquireContainer(req);
    }
    public ReturnContainerReply returnContainer(ReturnContainerRequest req) {
        return blockingStub.returnContainer(req);
    }
    public static SchedulerClient New(String endPoint) {
        String rmEndpoint = endPoint;
        ManagedChannel channel = ManagedChannelBuilder.forTarget(rmEndpoint).usePlaintext().build();
        SchedulerClient client = new SchedulerClient(channel);
        logger.info("Connected to Scheduler server at " + rmEndpoint);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                channel.shutdownNow().awaitTermination(5, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace(System.err);
            }
            System.err.println("SchedulerClient client shut down.");
        }));

        return client;
    }

    public static void main(String[] args) {
        SchedulerClient client = New("127.0.0.1:10600");
        client.acquireContainer(AcquireContainerRequest.newBuilder().setFunctionName("dev_function_1").setAccountId("dafdafea")
                .setRequestId("3rasdfe")
                .setFunctionConfig(FunctionConfig.newBuilder().setMemoryInBytes(500000000).setTimeoutInMs(60000).setHandler("handler_function_1").build()).build());
    }
}
