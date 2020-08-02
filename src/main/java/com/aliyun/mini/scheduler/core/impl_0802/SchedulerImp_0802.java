package com.aliyun.mini.scheduler.core.impl_0802;

import com.aliyun.mini.scheduler.core.impl_0802.global.GlobalInfo;
import com.aliyun.mini.scheduler.core.impl_0802.model.RequestInfo;
import com.aliyun.mini.scheduler.proto.SchedulerGrpc.SchedulerImplBase;
import com.java.mini.faas.ana.dto.NewRequestDTO;
import com.java.mini.faas.ana.log.LogWriter;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import schedulerproto.SchedulerOuterClass.AcquireContainerReply;
import schedulerproto.SchedulerOuterClass.AcquireContainerRequest;
import schedulerproto.SchedulerOuterClass.ReturnContainerReply;
import schedulerproto.SchedulerOuterClass.ReturnContainerRequest;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@Slf4j
public class SchedulerImp_0802 extends SchedulerImplBase {

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
            requestInfoQueue = new ConcurrentLinkedQueue<>();
            GlobalInfo.requestQueueMap.put(request.getRequestId(),requestInfoQueue);
            GlobalInfo.functionNameMap.put(request.getFunctionName(),new HashSet<>());
            StrategicThread.start(request.getFunctionName(),requestInfoQueue);
            log.info("StrategicThread start..."+request.getFunctionName());

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