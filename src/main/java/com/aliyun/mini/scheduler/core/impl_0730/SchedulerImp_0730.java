package com.aliyun.mini.scheduler.core.impl_0730;

import com.aliyun.mini.nodeservice.client.NodeServiceClient;
import com.aliyun.mini.resourcemanager.client.ResourceManagerClient;
import com.aliyun.mini.scheduler.core.impl_0722.model.ContainerInfo;
import com.aliyun.mini.scheduler.core.impl_0722.model.NodeInfo;
import com.aliyun.mini.scheduler.core.impl_0730.global.GlobalInfo;
import com.aliyun.mini.scheduler.core.impl_0730.model.RequestInfo;
import com.aliyun.mini.scheduler.core.impl_0730.nodemanager.NodeContainerManagerThread;
import com.aliyun.mini.scheduler.core.impl_0730.strategic.StrategicThread;
import com.aliyun.mini.scheduler.proto.SchedulerGrpc.SchedulerImplBase;
import com.java.mini.faas.ana.dto.*;
import com.java.mini.faas.ana.log.LogWriter;
import io.grpc.netty.shaded.io.netty.util.internal.ConcurrentSet;
import io.grpc.stub.StreamObserver;
import jdk.nashorn.internal.objects.Global;
import lombok.extern.slf4j.Slf4j;
import nodeservoceproto.NodeServiceOuterClass.*;
import resourcemanagerproto.ResourceManagerOuterClass.ReserveNodeReply;
import resourcemanagerproto.ResourceManagerOuterClass.ReserveNodeRequest;
import schedulerproto.SchedulerOuterClass.AcquireContainerReply;
import schedulerproto.SchedulerOuterClass.AcquireContainerRequest;
import schedulerproto.SchedulerOuterClass.ReturnContainerReply;
import schedulerproto.SchedulerOuterClass.ReturnContainerRequest;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

@Slf4j
public class SchedulerImp_0730 extends SchedulerImplBase {

    LogWriter logWriter = LogWriter.getInstance();



    @Override
    public void acquireContainer(AcquireContainerRequest request,
                                 StreamObserver<AcquireContainerReply> responseObserver) {
        try{
            logWriter.newRequestInfo(new NewRequestDTO(request.getRequestId(),request.getFunctionName(),request.getFunctionConfig().getMemoryInBytes(),request.getFunctionConfig().getTimeoutInMs()));
        }catch (Exception e){
            e.printStackTrace();
        }

        RequestInfo requestInfo = new RequestInfo(
                request.getAccountId(),
                request.getRequestId(),
                request.getFunctionName(),
                request.getFunctionConfig().getHandler(),
                request.getFunctionConfig().getMemoryInBytes(),
                request.getFunctionConfig().getTimeoutInMs(),
                responseObserver,
                Calendar.getInstance().getTimeInMillis());

        Queue<RequestInfo> requestInfoQueue = GlobalInfo.requestQueueMap.get(request.getFunctionName());
        if(requestInfoQueue == null){
            synchronized (GlobalInfo.requestQueueMap){
                requestInfoQueue = GlobalInfo.requestQueueMap.get(request.getFunctionName());
                if(requestInfoQueue == null){
                    requestInfoQueue = new ConcurrentLinkedQueue<>();
                    GlobalInfo.requestQueueMap.put(request.getFunctionName(),requestInfoQueue);
                    GlobalInfo.functionNameMap.put(request.getFunctionName(),new ConcurrentSet<>());
                    StrategicThread.start(request.getFunctionName(),requestInfoQueue);
                    log.info("StrategicThread start..."+request.getFunctionName());
                }
            }
        }
        requestInfoQueue.add(requestInfo);
    }

    @Override
    public void returnContainer(ReturnContainerRequest request,
                                StreamObserver<ReturnContainerReply> responseObserver) {
        responseObserver.onNext(null);
        responseObserver.onCompleted();
    }
}