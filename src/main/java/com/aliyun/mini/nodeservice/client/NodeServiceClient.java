package com.aliyun.mini.nodeservice.client;

import com.aliyun.mini.nodeservice.NodeServiceGrpc;
import io.grpc.Channel;
import io.grpc.ManagedChannelBuilder;
import nodeservoceproto.NodeServiceOuterClass;

import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;


public class NodeServiceClient {

    private static final Logger logger = Logger.getLogger(NodeServiceClient.class.getName());

    private final NodeServiceGrpc.NodeServiceBlockingStub blockingStub;

    public NodeServiceClient(Channel channel) {
        blockingStub = NodeServiceGrpc.newBlockingStub(channel);
    }

    /**
     * 获取状态
     * @param req
     * @return
     */
    public NodeServiceOuterClass.GetStatsReply getStats(NodeServiceOuterClass.GetStatsRequest req) {
        return blockingStub.getStats(req);
    }

    public static NodeServiceClient New() {
        var rmEndpoint = System.getenv("NODE_SERVICE_ENDPOINT");
        if (null == rmEndpoint) {
            rmEndpoint = "0.0.0.0:10400";
        }
        var channel = ManagedChannelBuilder.forTarget(rmEndpoint).usePlaintext().build();
        var client = new NodeServiceClient(channel);
        logger.info("Connected to NodeService server at " + rmEndpoint);

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
