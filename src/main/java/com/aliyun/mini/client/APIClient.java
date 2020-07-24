package com.aliyun.mini.client;
import apiserverproto.APIServerGrpc;
import apiserverproto.Apiserver;
import io.grpc.Channel;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import static apiserverproto.APIServerGrpc.newBlockingStub;

/**
 */
public class APIClient {
    private static final Logger logger = Logger.getLogger(APIClient.class.getName());


    private final APIServerGrpc.APIServerBlockingStub blockingStub;

    public APIClient(Channel channel) {
        blockingStub = newBlockingStub(channel);
    }

    public Apiserver.ListFunctionsReply listFunctions(Apiserver.ListFunctionsRequest req) {
        return blockingStub.listFunctions(req);
    }
    public Apiserver.InvokeFunctionReply invokeFunction(Apiserver.InvokeFunctionRequest req) {
        return blockingStub.invokeFunction(req);
    }
    public static APIClient New(String endPoint) {
        String apiEndpoint = endPoint;
        ManagedChannel channel = ManagedChannelBuilder.forTarget(apiEndpoint).usePlaintext().build();
        APIClient client = new APIClient(channel);
        logger.info("Connected to api server at " + apiEndpoint);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                channel.shutdownNow().awaitTermination(5, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace(System.err);
            }
            System.err.println("client shut down.");
        }));

        return client;
    }

}
