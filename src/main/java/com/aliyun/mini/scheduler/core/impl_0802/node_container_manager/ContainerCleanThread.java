package com.aliyun.mini.scheduler.core.impl_0802.node_container_manager;

import com.aliyun.mini.scheduler.core.impl_0802.global.GlobalInfo;
import com.aliyun.mini.scheduler.core.impl_0802.model.ContainerInfo;
import com.aliyun.mini.scheduler.core.impl_0802.model.RequestInfo;
import com.java.mini.faas.ana.dto.ContainerCleanDTO;
import com.java.mini.faas.ana.log.LogWriter;
import java.util.*;
import java.util.concurrent.Future;

/**
 * 清理container线程
 * 1.在没有创建container的空间时启动，按照多优先级+LRU算法清理container，为了方便，仅对执行时间小于100ms的container定为删除低优先级
 */
public class ContainerCleanThread implements Runnable {

    private LogWriter logWriter = LogWriter.getInstance();

    private RequestInfo requestInfo;

    public ContainerCleanThread build(RequestInfo requestInfo){
        this.requestInfo = requestInfo;
        return this;
    }
    @Override
    public void run() {
        System.out.println("start clean container..."+requestInfo);
        // 记录在一次清理中，每个node将会清理出的内存
        Map<String,MemoryRecord> nodeCleanMemory = new HashMap<>();
        long needMemory = requestInfo.getMemoryInBytes();
        MemoryRecord selected = null;
        nodeCleanMemory.clear();
        synchronized (GlobalInfo.containerLRU){
            for(ContainerInfo containerInfo : GlobalInfo.containerLRU.values()){
                if(containerInfo.getRequestSet().size() > 0 || containerInfo.isDeleted() ){
                    continue;
                }
                if(containerInfo.getAvgDuration() < NodeContainerManagerContants.CONTAINER_CLEAN_AVG_DURATION_LOWER
                        && containerInfo.getSignCleanCnt() < NodeContainerManagerContants.CONTAINER_CLEAN_SIGN_CNT_LOWER ){
                    containerInfo.setSignCleanCnt(containerInfo.getSignCleanCnt() + 1);
                    continue;
                }
                MemoryRecord memoryRecord = nodeCleanMemory.get(containerInfo.getNodeId());
                if(memoryRecord == null){
                    memoryRecord = new MemoryRecord();
                    memoryRecord.nodeId = containerInfo.getNodeId();
                    nodeCleanMemory.put(containerInfo.getNodeId(),memoryRecord);
                    memoryRecord.cleanMemory += GlobalInfo.nodeInfoMap.get(containerInfo.getNodeId()).getAvailableMemInBytes();
                }
                memoryRecord.containerInfos.add(containerInfo);
                memoryRecord.cleanMemory += containerInfo.getMemoryInBytes();
                if(selected == null){
                    selected = memoryRecord;
                }else {
                    selected = selected.cleanMemory > memoryRecord.cleanMemory ? selected : memoryRecord;
                }
                if(selected.cleanMemory  >= needMemory){
                    break;
                }
            }
            if(selected == null){
                System.out.println("[CLEAN_FAIL]" + requestInfo);
                try {
                    GlobalInfo.containerCleanThreads.put(this);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return;
            }
            if(selected.cleanMemory < needMemory){
                System.out.println("[CLEAN_NOT_ENOUGH]"+requestInfo);
            }
            for(ContainerInfo containerInfo : selected.containerInfos){
                synchronized (containerInfo){
                    containerInfo.setDeleted(true);
                }
                GlobalInfo.containerLRU.remove(containerInfo.getContainerId());
            }
        }
        // 正式删除
        List<String> containerIdList = new ArrayList<>();
        List<Future> futures = new ArrayList<>();
        for(ContainerInfo containerInfo : selected.containerInfos){
            containerIdList.add(containerInfo.getContainerId());
            try {
                futures.add(GlobalInfo.threadPool.submit(GlobalInfo.removeContainerThreadQueue.take().build(containerInfo)));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        try {
            for(Future future : futures){
                future.get();
            }
            GlobalInfo.containerCleanThreads.put(this);
            logWriter.cleanContainerInfo(new ContainerCleanDTO(requestInfo.getRequestId(),selected.nodeId,containerIdList));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    class MemoryRecord{
        String nodeId;
        List<ContainerInfo> containerInfos = new ArrayList<>();
        Long cleanMemory = 0L;
    }
}
