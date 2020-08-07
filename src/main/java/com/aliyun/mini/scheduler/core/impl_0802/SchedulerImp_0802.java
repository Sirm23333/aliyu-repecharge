package com.aliyun.mini.scheduler.core.impl_0802;

import com.aliyun.mini.scheduler.core.impl_0802.global.GlobalInfo;
import com.aliyun.mini.scheduler.core.impl_0802.model.ContainerInfo;
import com.aliyun.mini.scheduler.core.impl_0802.model.FunctionStatistics;
import com.aliyun.mini.scheduler.core.impl_0802.model.RequestInfo;
import com.aliyun.mini.scheduler.core.impl_0802.node_container_manager.CreateContainerThread;
import com.aliyun.mini.scheduler.proto.SchedulerGrpc.SchedulerImplBase;
import com.java.mini.faas.ana.dto.ContainerRunDTO;
import com.java.mini.faas.ana.dto.NewRequestDTO;
import com.java.mini.faas.ana.dto.SelectedContainerDTO;
import com.java.mini.faas.ana.log.LogWriter;
import io.grpc.netty.shaded.io.netty.util.internal.ConcurrentSet;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import schedulerproto.SchedulerOuterClass;
import schedulerproto.SchedulerOuterClass.AcquireContainerReply;
import schedulerproto.SchedulerOuterClass.AcquireContainerRequest;
import schedulerproto.SchedulerOuterClass.ReturnContainerReply;
import schedulerproto.SchedulerOuterClass.ReturnContainerRequest;
import java.util.Calendar;
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


        Object lock = GlobalInfo.functionLockMap.get(request.getFunctionName());
        if(lock == null){
            synchronized (GlobalInfo.functionLockMap){
                lock = GlobalInfo.functionLockMap.get(request.getFunctionName());
                if(lock == null){
                    lock = new Object();
                    GlobalInfo.containerIdMap.put(request.getFunctionName(),new ConcurrentSet<>());
                    GlobalInfo.functionLockMap.put(request.getFunctionName(),lock);
                    GlobalInfo.functionStatisticsMap.put(request.getFunctionName(),new FunctionStatistics(request.getFunctionName()));
                }
            }
        }
        ContainerInfo selectedContainer ;
        // 标志是否为这个request正在创建container，避免重复创建
        boolean createContainerFlag =false;
        // 有能用的container马上返回，没有能用的马上创建container，但同时间创建container的数量有限
        while(true){
            selectedContainer = getBestContainer(requestInfo);
            if(selectedContainer != null){
                break;
            } else if(!createContainerFlag){
                // 没有可用的container 马上创建一个container
                CreateContainerThread createContainerThread = null;
                try {
                    // 需要先拿到创建线程，如果暂时拿不到就阻塞在这
                    createContainerThread = GlobalInfo.createContainerThreadQueue.take();
                    // 在检查一般现在有没有可以用的container，如果没有则创建
                    selectedContainer = getBestContainer(requestInfo);
                    if(selectedContainer != null){
                        GlobalInfo.createContainerThreadQueue.put(createContainerThread);
                        break;
                    }
                    GlobalInfo.threadPool.execute(createContainerThread.build(requestInfo));
                    createContainerFlag = true;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            synchronized (lock){
                selectedContainer = getBestContainer(requestInfo);
                if(selectedContainer == null){
                    try {
                        // 被唤醒：1.创建好了新的container 2.有container return 3.提高了container的并行度
                        lock.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }else {
                    break;
                }
            }
        }
        synchronized (GlobalInfo.containerLRU){
            GlobalInfo.containerLRU.get(selectedContainer.getContainerId());
        }
        responseObserver.onNext(SchedulerOuterClass.AcquireContainerReply.newBuilder()
                .setNodeId(selectedContainer.getNodeId())
                .setNodeAddress(selectedContainer.getAddress())
                .setNodeServicePort(selectedContainer.getPort())
                .setContainerId(selectedContainer.getContainerId())
                .build());
        requestInfo.getResponseObserver().onCompleted();
        logWriter.selectedContainerInfo(new SelectedContainerDTO(requestInfo.getRequestId(),selectedContainer.getContainerId()));
    }

    @Override
    public void returnContainer(ReturnContainerRequest request,
                                StreamObserver<ReturnContainerReply> responseObserver) {
        ContainerInfo containerInfo = GlobalInfo.containerInfoMap.get(request.getContainerId());
        try{
            logWriter.containerRunInfo(new ContainerRunDTO(request.getRequestId(),request.getContainerId(),request.getDurationInNanos(),request.getMaxMemoryUsageInBytes(),request.getErrorCode(),request.getErrorMessage()));
        }catch (Exception e){
            e.printStackTrace();
        }
        // 运行异常，删除container
        if(containerRunException(request) && containerInfo != null){
            synchronized (containerInfo){
                containerInfo = GlobalInfo.containerInfoMap.get(request.getContainerId());
                if(containerInfo != null){
                    containerInfo.setDeleted(true);
                    try {
                        GlobalInfo.threadPool.execute(GlobalInfo.removeContainerThreadQueue.take().build(containerInfo));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }else {
            synchronized (containerInfo){
                containerInfo.getRequestSet().remove(request.getRequestId());
                double avg = containerInfo.getAvgDuration();
                containerInfo.setAvgDuration( avg + ((double) request.getDurationInNanos() / 1000000 - avg) / containerInfo.getUseCnt() );
            }
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

    private ContainerInfo  getBestContainer(RequestInfo requestInfo){
        ConcurrentSet<String> containerIds = GlobalInfo.containerIdMap.get(requestInfo.getFunctionName());
        ContainerInfo selectContainer = null,tmpContainer;
        double minCpu = Double.MAX_VALUE;
        synchronized (containerIds){
            for(String containerId : containerIds){
                tmpContainer = GlobalInfo.containerInfoMap.get(containerId);
                // 选择node cpu使用最小的container
                if(tmpContainer.getRequestSet().size() < tmpContainer.getConcurrencyUpperLimit() && !tmpContainer.isDeleted()){
                    synchronized (tmpContainer){
                        if(tmpContainer.getRequestSet().size() < tmpContainer.getConcurrencyUpperLimit() && !tmpContainer.isDeleted()){
                            double cpu = GlobalInfo.nodeStatusMap.get(tmpContainer.getNodeId()).getCpuUsagePctHistory().getLast();
                            if(cpu < minCpu){
                                minCpu = cpu;
                                selectContainer = tmpContainer;
                            }
                        }
                    }
                }
            }
            if(selectContainer != null){
                selectContainer.getRequestSet().add(requestInfo.getRequestId());
                selectContainer.setUseCnt(selectContainer.getUseCnt() + 1);
                selectContainer.setSignCleanCnt(0);
            }
        }
        return selectContainer;
    }
}