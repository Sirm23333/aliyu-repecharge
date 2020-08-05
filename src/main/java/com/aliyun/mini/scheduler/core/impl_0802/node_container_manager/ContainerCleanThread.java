package com.aliyun.mini.scheduler.core.impl_0802.node_container_manager;

import com.aliyun.mini.scheduler.core.impl_0802.global.GlobalInfo;
import com.aliyun.mini.scheduler.core.impl_0802.model.ContainerInfo;
import com.aliyun.mini.scheduler.core.impl_0802.model.RequestInfo;
import com.java.mini.faas.ana.dto.ContainerCleanDTO;
import com.java.mini.faas.ana.log.LogWriter;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 系统启动时启动，清理container线程，在没有创建container的空间时按照LRU算法清理container
 */
public class ContainerCleanThread implements Runnable {

    private LogWriter logWriter = LogWriter.getInstance();
    public static LinkedBlockingQueue<RequestInfo> cleanContainerQueue = new LinkedBlockingQueue<>();

    public static void start(){
        new Thread(new ContainerCleanThread()).start();
        System.out.println("ContainerCleanThread start...");
    }
    @Override
    public void run() {
        // 记录在一次清理中，每个node将会清理出的内存
        Map<String,MemoryRecord> nodeCleanMemory = new HashMap<>();

        while(true){
            RequestInfo requestInfo = null;
            try {
                requestInfo = cleanContainerQueue.take();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            long needMemory = requestInfo.getMemoryInBytes();
            MemoryRecord selected = null;
            while (true){
                if(requestInfo.getEnd().get()){
                    synchronized (GlobalInfo.nodeLock){
                        GlobalInfo.nodeLock.notifyAll();
                    }
                    break;
                }
                nodeCleanMemory.clear();
                synchronized (GlobalInfo.containerLRU){
                    for(ContainerInfo containerInfo : GlobalInfo.containerLRU.values()){
                        // 如果遍历到正在被使用的container，说明没有足够的内存空间了
                        if(containerInfo.getRequestSet().size() > 0){
                            break;
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
                        if(memoryRecord.cleanMemory  >= needMemory){
                            selected = memoryRecord;
                            break;
                        }
                    }
                    if(selected != null){
                        for(ContainerInfo containerInfo : selected.containerInfos){
                            containerInfo.setDeleted(true);
                            GlobalInfo.containerLRU.remove(containerInfo.getContainerId());
                        }
                    }
                }
                if(selected == null){
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }else {
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
                    for(Future future : futures){
                        try {
                            future.get();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        }
                    }
                    try{
                        logWriter.cleanContainerInfo(new ContainerCleanDTO(requestInfo.getRequestId(),selected.nodeId,containerIdList));
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    break;
                }
            }
        }

    }
    class MemoryRecord{
        String nodeId;
        List<ContainerInfo> containerInfos = new ArrayList<>();
        Long cleanMemory = 0L;
    }
}
