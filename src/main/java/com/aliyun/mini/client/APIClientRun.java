package com.aliyun.mini.client;

import apiserverproto.Apiserver.*;
import com.alibaba.fastjson.JSONObject;
import com.google.protobuf.ByteString;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 *
 */
@Slf4j
public class APIClientRun {

    private static final String endPoint = "172.17.0.4:10500";

    private static Map<String,String> sampleEvents = new HashMap<>();

    static {
        sampleEvents.put( "dev_function_1","1.2");
        sampleEvents.put( "dev_function_2","");
        sampleEvents.put( "dev_function_3","");
        sampleEvents.put( "dev_function_4","50");
        sampleEvents.put( "dev_function_5","");

    }
    public static void start(){
        APIClient client = APIClient.New(endPoint);
        ListFunctionsReply listFunctionsReply = client.listFunctions(null);
//        for(int i = 0; i < 20; i++){
//            try {
//                Thread.sleep(100);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            for(FunctionConfig functionConfig : listFunctionsReply.getFunctionsList()){
//                String e = sampleEvents.get(functionConfig.getFunctionName());
//                JSONObject event = new JSONObject();
//                event.put("functionName",functionConfig.getFunctionName());
//                event.put("param",e);
//                log.info("Invoking function "+functionConfig.getFunctionName()+" with event" +sampleEvents.get(functionConfig.getFunctionName()));
////            System.out.println("Invoking function "+functionConfig.getFunctionName()+" with event" +sampleEvents.get(functionConfig.getFunctionName()));
//                InvokeFunctionReply invokeFunctionReply = client.invokeFunction(InvokeFunctionRequest.newBuilder()
//                        .setEvent(ByteString.copyFromUtf8(event.toJSONString()))
//                        .setFunctionName(functionConfig.getFunctionName()).build());
//                log.info("Invoke function reply "+invokeFunctionReply);
////            System.out.println("Invoke function reply "+invokeFunctionReply);
//            }
//
//        }
        for(int k = 0; k < 5;k++){
            int finalK = k;
            Thread t = new Thread(()->{
                for(int i = 0; i < 10; i++){
                    FunctionConfig functionConfig = listFunctionsReply.getFunctionsList().get(0);
                    String e = sampleEvents.get(functionConfig.getFunctionName());
                    JSONObject event = new JSONObject();
                    event.put("functionName",functionConfig.getFunctionName());
                    event.put("param",e);
                    log.info("Invoking function "+functionConfig.getFunctionName()+" with event" +sampleEvents.get(functionConfig.getFunctionName()));
//            System.out.println("Invoking function "+functionConfig.getFunctionName()+" with event" +sampleEvents.get(functionConfig.getFunctionName()));
                    InvokeFunctionReply invokeFunctionReply = client.invokeFunction(InvokeFunctionRequest.newBuilder()
                            .setEvent(ByteString.copyFromUtf8(event.toJSONString()))
                            .setFunctionName(functionConfig.getFunctionName()).build());
                    log.info("Invoke function reply "+invokeFunctionReply);
//            System.out.println("Invoke function reply "+invokeFunctionReply);
                }
            });
            t.start();
        }

    }
    public static void main(String[] args) {
        APIClientRun.start();
    }
}
