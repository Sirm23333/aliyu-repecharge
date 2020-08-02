package com.aliyun.mini.scheduler.core.impl_0802;

import com.aliyun.mini.scheduler.core.impl_0802.global.GlobalInfo;
import com.aliyun.mini.scheduler.core.impl_0802.model.RequestInfo;
import com.aliyun.mini.scheduler.core.impl_0802.strategic.StrategicThread;
import com.aliyun.mini.scheduler.proto.SchedulerGrpc.SchedulerImplBase;
import com.java.mini.faas.ana.dto.NewRequestDTO;
import com.java.mini.faas.ana.log.LogWriter;
import io.grpc.netty.shaded.io.netty.util.internal.ConcurrentSet;
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
import java.util.concurrent.LinkedBlockingQueue;

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
                Calendar.getInstance().getTimeInMillis(),
                false);

        LinkedBlockingQueue<RequestInfo> requestInfoQueue = GlobalInfo.requestQueueMap.get(request.getFunctionName());
        if(requestInfoQueue == null){
            synchronized (GlobalInfo.requestQueueMap){
                requestInfoQueue = GlobalInfo.requestQueueMap.get(request.getFunctionName());
                if(requestInfoQueue == null){
                    requestInfoQueue = new LinkedBlockingQueue<>();
                    GlobalInfo.requestQueueMap.put(request.getFunctionName(),requestInfoQueue);
                    GlobalInfo.containerIdMap.put(request.getFunctionName(),new ConcurrentSet<>());
                    GlobalInfo.lockMap.put(request.getFunctionName(),new Object());
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
        if(containerRunException(request) && GlobalInfo.containerInfoMap.containsKey(request.getContainerId())){
            // 运行异常，删除container


        }
        responseObserver.onNext(null);
        responseObserver.onCompleted();
    }


    private boolean containerRunException(ReturnContainerRequest request){
        return request.getErrorCode() != null && !request.getErrorCode().equals("");
    }
}