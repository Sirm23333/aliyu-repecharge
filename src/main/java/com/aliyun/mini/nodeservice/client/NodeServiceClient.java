package com.aliyun.mini.nodeservice.client;

import com.aliyun.mini.nodeservice.NodeServiceGrpc.*;
import io.grpc.Channel;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import lombok.extern.slf4j.Slf4j;
import nodeservoceproto.NodeServiceOuterClass.*;

import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import static com.aliyun.mini.nodeservice.NodeServiceGrpc.newBlockingStub;

@Slf4j
public class NodeServiceClient {

    private static final Logger logger = Logger.getLogger(NodeServiceClient.class.getName());

    private final NodeServiceBlockingStub blockingStub;

    public NodeServiceClient(Channel channel) {
        blockingStub = newBlockingStub(channel);
    }

    public GetStatsReply getStats(GetStatsRequest req) {
        return blockingStub.getStats(req);
    }
    public CreateContainerReply createContainer(CreateContainerRequest createContainerRequest){
        return blockingStub.withDeadlineAfter(30,TimeUnit.SECONDS).createContainer(createContainerRequest);
    }
    public RemoveContainerReply removeContainer(RemoveContainerRequest removeContainerRequest){
        return blockingStub.removeContainer(removeContainerRequest);
    }

    public static NodeServiceClient New(String endPoint) {
        String rmEndpoint = endPoint;
        ManagedChannel channel = ManagedChannelBuilder.forTarget(rmEndpoint).usePlaintext().build();
        NodeServiceClient client = new NodeServiceClient(channel);
        log.info("Connected to NodeService server at " + rmEndpoint);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                channel.shutdownNow().awaitTermination(5, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace(System.err);
            }
            System.err.println("NodeService client shut down.");
        }));

        return client;
    }
}
