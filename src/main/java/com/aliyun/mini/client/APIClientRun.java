package com.aliyun.mini.client;

import apiserverproto.Apiserver.*;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.mini.scheduler.util.NumberUtil;
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
        // function1 0.5s 20线程
        for(int k = 0; k < 20;k++){
            Thread t = new Thread(()->{
                for(int i = 0; i < 200; i++){
                    FunctionConfig functionConfig = listFunctionsReply.getFunctionsList().get(0);
                    String e = sampleEvents.get(functionConfig.getFunctionName());
                    JSONObject event = new JSONObject();
                    event.put("functionName",functionConfig.getFunctionName());
                    event.put("param",e);
                    log.info("Invoking function "+functionConfig.getFunctionName()+" with event" +sampleEvents.get(functionConfig.getFunctionName()));
                    InvokeFunctionReply invokeFunctionReply = null;
                    try{
                        invokeFunctionReply = client.invokeFunction(InvokeFunctionRequest.newBuilder()
                                .setEvent(ByteString.copyFromUtf8(event.toJSONString()))
                                .setFunctionName(functionConfig.getFunctionName()).build());
                    }catch (Exception ee){
                        ee.printStackTrace();
                    }

                    log.info("Invoke function reply "+invokeFunctionReply);
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }
            });
            t.start();
        }
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // function2 1s 10线程
        for(int k = 0; k < 10; k++){
            Thread t = new Thread(()->{
                for(int i = 0; i < 60; i++){
                    FunctionConfig functionConfig = listFunctionsReply.getFunctionsList().get(1);
                    String e = sampleEvents.get(functionConfig.getFunctionName());
                    JSONObject event = new JSONObject();
                    event.put("functionName",functionConfig.getFunctionName());
                    event.put("param",e);
                    log.info("Invoking function "+functionConfig.getFunctionName()+" with event" +sampleEvents.get(functionConfig.getFunctionName()));
                    InvokeFunctionReply invokeFunctionReply = null;
                    try{
                        invokeFunctionReply = client.invokeFunction(InvokeFunctionRequest.newBuilder()
                                .setEvent(ByteString.copyFromUtf8(event.toJSONString()))
                                .setFunctionName(functionConfig.getFunctionName()).build());
                    }catch (Exception ee){
                        ee.printStackTrace();
                    }
                    log.info("Invoke function reply "+invokeFunctionReply);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }
            });
            t.start();
        }
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // function3 0.1s 10线程
        for(int k = 0; k < 10; k++){
            Thread t = new Thread(()->{
                for(int i = 0; i < 200; i++){
                    if(NumberUtil.getRandomDouble(0,1) < 0.5){
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException ex) {
                            ex.printStackTrace();
                        }
                        continue;
                    }
                    FunctionConfig functionConfig = listFunctionsReply.getFunctionsList().get(2);
                    String e = sampleEvents.get(functionConfig.getFunctionName());
                    JSONObject event = new JSONObject();
                    event.put("functionName",functionConfig.getFunctionName());
                    event.put("param",e);
                    log.info("Invoking function "+functionConfig.getFunctionName()+" with event" +sampleEvents.get(functionConfig.getFunctionName()));
                    InvokeFunctionReply invokeFunctionReply = null;
                    try{
                        invokeFunctionReply = client.invokeFunction(InvokeFunctionRequest.newBuilder()
                                .setEvent(ByteString.copyFromUtf8(event.toJSONString()))
                                .setFunctionName(functionConfig.getFunctionName()).build());
                    }catch (Exception ee){
                        ee.printStackTrace();
                    }
                    log.info("Invoke function reply "+invokeFunctionReply);
                    try {
                        Thread.sleep(600);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }
            });
            t.start();
        }
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // function4 随机
        for(int k = 0; k < 10; k++){
            Thread t = new Thread(()->{
                for(int i = 0; i < 200; i++){
                    FunctionConfig functionConfig = listFunctionsReply.getFunctionsList().get(3);
                    String e = sampleEvents.get(functionConfig.getFunctionName());
                    JSONObject event = new JSONObject();
                    event.put("functionName",functionConfig.getFunctionName());
                    event.put("param",e);
                    log.info("Invoking function "+functionConfig.getFunctionName()+" with event" +sampleEvents.get(functionConfig.getFunctionName()));
                    InvokeFunctionReply invokeFunctionReply = null;
                    try{
                        invokeFunctionReply = client.invokeFunction(InvokeFunctionRequest.newBuilder()
                                .setEvent(ByteString.copyFromUtf8(event.toJSONString()))
                                .setFunctionName(functionConfig.getFunctionName()).build());
                    }catch (Exception ee){
                        ee.printStackTrace();
                    }
                    log.info("Invoke function reply "+invokeFunctionReply);
                    try {
                        Thread.sleep(NumberUtil.getRandomNum(0,500));
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }
            });
            t.start();
        }
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // function5 调用几次
        for(int k = 0; k < 50; k++){
            Thread t = new Thread(()->{
                for(int i = 0; i < 5; i++){
                    FunctionConfig functionConfig = listFunctionsReply.getFunctionsList().get(4);
                    String e = sampleEvents.get(functionConfig.getFunctionName());
                    JSONObject event = new JSONObject();
                    event.put("functionName",functionConfig.getFunctionName());
                    event.put("param",e);
                    log.info("Invoking function "+functionConfig.getFunctionName()+" with event" +sampleEvents.get(functionConfig.getFunctionName()));
                    InvokeFunctionReply invokeFunctionReply = null;
                    try{
                        invokeFunctionReply = client.invokeFunction(InvokeFunctionRequest.newBuilder()
                                .setEvent(ByteString.copyFromUtf8(event.toJSONString()))
                                .setFunctionName(functionConfig.getFunctionName()).build());
                    }catch (Exception ee){
                        ee.printStackTrace();
                    }
                    log.info("Invoke function reply "+invokeFunctionReply);
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }
            });
            t.start();
        }
        try {
            Thread.sleep(NumberUtil.getRandomNum(0,500));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
    public static void main(String[] args) {
        APIClientRun.start();
    }
}
