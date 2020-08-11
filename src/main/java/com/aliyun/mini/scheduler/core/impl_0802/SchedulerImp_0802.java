package com.aliyun.mini.scheduler.core.impl_0802;

import com.aliyun.mini.scheduler.core.impl_0802.global.GlobalInfo;
import com.aliyun.mini.scheduler.core.impl_0802.model.ContainerInfo;
import com.aliyun.mini.scheduler.core.impl_0802.model.FunctionStatistics;
import com.aliyun.mini.scheduler.core.impl_0802.model.NodeStatus;
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
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class SchedulerImp_0802 extends SchedulerImplBase {

    LogWriter logWriter = LogWriter.getInstance();

    @Override
    public void acquireContainer(AcquireContainerRequest request,
                                 StreamObserver<AcquireContainerReply> responseObserver) {
        long start  = System.nanoTime();
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
                    GlobalInfo.containerIdMap.put(request.getFunctionName(),new ConcurrentSet<>());
                    GlobalInfo.functionStatisticsMap.put(request.getFunctionName(),new FunctionStatistics(request.getFunctionName(),requestInfo.getMemoryInBytes()));
                    GlobalInfo.creatingContainerNumMap.put(request.getFunctionName(),new AtomicInteger(0));
                    GlobalInfo.waitingCreateContainerNumMap.put(request.getFunctionName(),new AtomicInteger(0));
                    lock = new Object();
                    GlobalInfo.functionLockMap.put(request.getFunctionName(),lock);
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
            } else if(!createContainerFlag && !GlobalInfo.functionStatisticsMap.get(requestInfo.getFunctionName()).isCpuIntensive()){
                createContainerFlag = true;
                AtomicInteger wait = GlobalInfo.waitingCreateContainerNumMap.get(requestInfo.getFunctionName());
                AtomicInteger create = GlobalInfo.creatingContainerNumMap.get(requestInfo.getFunctionName());
                int waitNum = wait.getAndIncrement(); // 等待创建container的请求中，自己的排位
                if(waitNum % GlobalInfo.functionStatisticsMap.get(requestInfo.getFunctionName()).getParallelism() == 0){
                    create.getAndIncrement();
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
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
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
        GlobalInfo.functionStatisticsMap.get(request.getFunctionName()).appendDelaySamp(System.nanoTime() - start);
        GlobalInfo.useStartMap_Tmp.put(selectedContainer.getContainerId(),System.nanoTime());
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
            GlobalInfo.functionStatisticsMap.get(GlobalInfo.containerInfoMap.get(request.getContainerId()).getFunctionName()).appendUseTime(System.nanoTime() - GlobalInfo.useStartMap_Tmp.get(request.getContainerId()));
            logWriter.containerRunInfo(new ContainerRunDTO(request.getRequestId(),request.getContainerId(),request.getDurationInNanos(),request.getMaxMemoryUsageInBytes(),request.getErrorCode(),request.getErrorMessage()));
        }catch (Exception e){
            e.printStackTrace();
        }
        // 运行异常或者被标记需要删除，删除container
        if(containerRunException(request) && containerInfo != null || containerInfo.isDeleted()){
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
                containerInfo.getChoiceTmpCnt().set(containerInfo.getRequestSet().size());
                NodeStatus nodeStatus = GlobalInfo.nodeStatusMap.get(containerInfo.getNodeId());
                nodeStatus.setEstimateCPU(nodeStatus.getCpuUsagePctHistory().getLast());
                nodeStatus.setEstimateMem(nodeStatus.getMemoryUsageInBytesHistory().getLast());
                double avg = containerInfo.getAvgDuration();
                containerInfo.setAvgDuration( avg + ((double) request.getDurationInNanos() / 1000000 - avg) / containerInfo.getUseCnt() );
                if(GlobalInfo.functionStatisticsMap.get(containerInfo.getFunctionName()).isCpuIntensive()){
                    GlobalInfo.nodeInfoMap.get(containerInfo.getNodeId()).getCpuIntensiveNum().set(0);
                }
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
        double minScore = Double.MAX_VALUE;
        FunctionStatistics functionStatistics = GlobalInfo.functionStatisticsMap.get(requestInfo.getFunctionName());
        if(functionStatistics.isCpuIntensive()){
            System.out.println("cpu intensive get container"+requestInfo);
            // 如果是cpu密集型，每个node仅可以同时执行1个此类函数
            synchronized (containerIds){
                for(String containerId : containerIds){
                    tmpContainer = GlobalInfo.containerInfoMap.get(containerId);
                    // 选择node cpu使用最小的container
                    if(tmpContainer.getRequestSet().size() < tmpContainer.getConcurrencyUpperLimit() && !tmpContainer.isDeleted()
                            && GlobalInfo.nodeInfoMap.get(tmpContainer.getNodeId()).getCpuIntensiveNum().get() < 1){
                        synchronized (tmpContainer){
                            if(tmpContainer.getRequestSet().size() < tmpContainer.getConcurrencyUpperLimit() && !tmpContainer.isDeleted()){
                                double score = 0.7 * GlobalInfo.nodeStatusMap.get(tmpContainer.getNodeId()).getEstimateCPU() / 200
                                        + 0.3 * GlobalInfo.nodeStatusMap.get(tmpContainer.getNodeId()).getEstimateMem() / (1024 * 1024 * 1024 * 3);
                                if(score < minScore){
                                    minScore = score;
                                    selectContainer = tmpContainer;
                                }
                            }
                        }
                    }
                }
                if(selectContainer != null){
                    GlobalInfo.nodeStatusMap.get(selectContainer.getNodeId())
                            .setEstimateCPU(GlobalInfo.nodeStatusMap.get(selectContainer.getNodeId()).getEstimateCPU()
                                    + GlobalInfo.functionStatisticsMap.get(selectContainer.getFunctionName()).getAvgCpu());
                    GlobalInfo.nodeStatusMap.get(selectContainer.getNodeId())
                            .setEstimateMem(GlobalInfo.nodeStatusMap.get(selectContainer.getNodeId()).getEstimateMem()
                                    + (long)GlobalInfo.functionStatisticsMap.get(selectContainer.getFunctionName()).getAvgMem());
                    selectContainer.getRequestSet().add(requestInfo.getRequestId());
                    selectContainer.setUseCnt(selectContainer.getUseCnt() + 1);
                    selectContainer.setSignCleanCnt(0);
                    GlobalInfo.nodeInfoMap.get(selectContainer.getNodeId()).getCpuIntensiveNum().set(1);
                }
            }
        }else if(functionStatistics.getChoiceType() == 1 ){
            // 小函数 直接优先匹配原则分配
            for(String containerId : containerIds){
                tmpContainer = GlobalInfo.containerInfoMap.get(containerId);
                if(tmpContainer.getChoiceTmpCnt().getAndIncrement() < tmpContainer.getConcurrencyUpperLimit() &&  !tmpContainer.isDeleted()){
                    selectContainer = tmpContainer;
                    selectContainer.setLastUseTimeStamp(System.currentTimeMillis());
                    selectContainer.getRequestSet().add(requestInfo.getRequestId());
                    selectContainer.setUseCnt(selectContainer.getUseCnt() + 1);
                    selectContainer.setSignCleanCnt(0);
                    break;
                }else {
                    tmpContainer.getChoiceTmpCnt().getAndDecrement();
                }
            }
        }else {
            synchronized (containerIds){
                for(String containerId : containerIds){
                    tmpContainer = GlobalInfo.containerInfoMap.get(containerId);
                    // 选择node cpu使用最小的container
                    if(tmpContainer.getRequestSet().size() < tmpContainer.getConcurrencyUpperLimit() && !tmpContainer.isDeleted()){
                        synchronized (tmpContainer){
                            if(tmpContainer.getRequestSet().size() < tmpContainer.getConcurrencyUpperLimit() && !tmpContainer.isDeleted()){
                                double score = 0.7 * GlobalInfo.nodeStatusMap.get(tmpContainer.getNodeId()).getEstimateCPU() / 200
                                        + 0.3 * GlobalInfo.nodeStatusMap.get(tmpContainer.getNodeId()).getEstimateMem() / (1024 * 1024 * 1024 * 3);
                                if(score < minScore){
                                    minScore = score;
                                    selectContainer = tmpContainer;
                                }
                            }
                        }
                    }
                }
                if(selectContainer != null){
                    GlobalInfo.nodeStatusMap.get(selectContainer.getNodeId())
                            .setEstimateCPU(GlobalInfo.nodeStatusMap.get(selectContainer.getNodeId()).getEstimateCPU()
                                    + GlobalInfo.functionStatisticsMap.get(selectContainer.getFunctionName()).getAvgCpu());
                    GlobalInfo.nodeStatusMap.get(selectContainer.getNodeId())
                            .setEstimateMem(GlobalInfo.nodeStatusMap.get(selectContainer.getNodeId()).getEstimateMem()
                                    + (long)GlobalInfo.functionStatisticsMap.get(selectContainer.getFunctionName()).getAvgMem());
                    selectContainer.getRequestSet().add(requestInfo.getRequestId());
                    selectContainer.setUseCnt(selectContainer.getUseCnt() + 1);
                    selectContainer.setSignCleanCnt(0);
                }
            }
        }
        return selectContainer;
    }
}