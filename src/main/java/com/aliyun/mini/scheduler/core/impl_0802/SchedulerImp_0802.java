package com.aliyun.mini.scheduler.core.impl_0802;

import com.aliyun.mini.scheduler.core.impl_0802.global.GlobalInfo;
import com.aliyun.mini.scheduler.core.impl_0802.model.ContainerInfo;
import com.aliyun.mini.scheduler.core.impl_0802.model.RequestInfo;
import com.aliyun.mini.scheduler.core.impl_0802.node_container_manager.RemoveContainerThread;
import com.aliyun.mini.scheduler.core.impl_0802.strategic.StrategicThread;
import com.aliyun.mini.scheduler.proto.SchedulerGrpc.SchedulerImplBase;
import com.java.mini.faas.ana.dto.ContainerRunDTO;
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
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

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
                new AtomicBoolean(false));
//        log.info("[NEW_REQUEST]{}",requestInfo);
        LinkedBlockingQueue<RequestInfo> requestInfoQueue = GlobalInfo.requestQueueMap.get(request.getFunctionName());
        if(requestInfoQueue == null){
            synchronized (GlobalInfo.requestQueueMap){
                requestInfoQueue = GlobalInfo.requestQueueMap.get(request.getFunctionName());
                if(requestInfoQueue == null){
                    requestInfoQueue = new LinkedBlockingQueue<>();
                    GlobalInfo.requestQueueMap.put(request.getFunctionName(),requestInfoQueue);
                    GlobalInfo.containerIdMap.put(request.getFunctionName(),new ConcurrentSet<>());
                    GlobalInfo.functionLockMap.put(request.getFunctionName(),new Object());
                    StrategicThread.start(request.getFunctionName(),requestInfoQueue);
                }
            }
        }
        requestInfoQueue.add(requestInfo);
    }

    @Override
    public void returnContainer(ReturnContainerRequest request,
                                StreamObserver<ReturnContainerReply> responseObserver) {
        ContainerInfo containerInfo = GlobalInfo.containerInfoMap.get(request.getContainerId());

        try{
            logWriter.containerRunInfo(new ContainerRunDTO(request.getRequestId(),request.getDurationInNanos(),request.getMaxMemoryUsageInBytes(),request.getErrorCode(),request.getErrorMessage()));
        }catch (Exception e){
            e.printStackTrace();
        }
        // 运行异常，删除container
        if(containerRunException(request) && containerInfo != null){
            synchronized (containerInfo){
                containerInfo = GlobalInfo.containerInfoMap.get(request.getContainerId());
                if(containerInfo != null){
                    GlobalInfo.threadPool.execute(new RemoveContainerThread(containerInfo));
                }
            }
        }else {
            containerInfo.getRequestSet().remove(request.getRequestId());
            Object lock = GlobalInfo.functionLockMap.get(containerInfo.getFunctionName());
            synchronized (lock){
                lock.notifyAll();
            }
        }
        responseObserver.onNext(null);
        responseObserver.onCompleted();
    }


    private boolean containerRunException(ReturnContainerRequest request){
        return request.getErrorCode() != null && !request.getErrorCode().equals("");
    }

}