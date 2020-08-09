package com.aliyun.mini.scheduler.core.impl_0802.node_container_manager;

import com.aliyun.mini.scheduler.core.impl_0802.global.GlobalInfo;
import com.aliyun.mini.scheduler.core.impl_0802.model.*;
import com.java.mini.faas.ana.dto.CreateContainerErrorDTO;
import com.java.mini.faas.ana.dto.NewContainerDTO;
import com.java.mini.faas.ana.log.LogWriter;
import lombok.extern.slf4j.Slf4j;
import nodeservoceproto.NodeServiceOuterClass.*;

import java.util.Calendar;
import java.util.UUID;

/**
 * 创建container
 */
@Slf4j
public class CreateContainerThread implements Runnable {

    LogWriter logWriter = LogWriter.getInstance();

    private RequestInfo requestInfo;

    public CreateContainerThread build(RequestInfo requestInfo){
        this.requestInfo = requestInfo;
        return this;
    }

    @Override
    public void run() {
        // 先找一个可以创建Container的node
        NodeInfo selectedNode  = null;
        long lastCleanTime = 0; // 上一次清除时间
        boolean reserveNodeFlag = false; // 标识是否正在为这个request创建node
        while (true){
            selectedNode = getBestNode();
            if(selectedNode != null){
                break;
            }else{
                try{
                    if(Calendar.getInstance().getTimeInMillis() - lastCleanTime > 2000){
                        lastCleanTime = Calendar.getInstance().getTimeInMillis();
                        ContainerCleanThread containerCleanThread = null;
                        containerCleanThread = GlobalInfo.containerCleanThreads.take();
                        selectedNode = getBestNode();
                        if(selectedNode != null){
                            GlobalInfo.containerCleanThreads.put(containerCleanThread);
                            break;
                        }
                        GlobalInfo.threadPool.execute(containerCleanThread.build(requestInfo));
                    }

                    synchronized (GlobalInfo.nodeLock){
                        // 被唤醒：1.container的清理成功  3.过2s，如果清理失败了过两s后唤醒，重新申请清理空间
                        GlobalInfo.nodeLock.wait(2000);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        }
        // 创建container
        CreateContainerReply container = null;
        try {
            container = selectedNode.getClient().createContainer(
                    CreateContainerRequest.newBuilder()
                            .setRequestId(requestInfo.getRequestId())
                            .setName(requestInfo.getFunctionName()+ UUID.randomUUID())
                            .setFunctionMeta(FunctionMeta.newBuilder()
                                    .setFunctionName(requestInfo.getFunctionName())
                                    .setMemoryInBytes(requestInfo.getMemoryInBytes())
                                    .setTimeoutInMs(requestInfo.getTimeOutInMs())
                                    .setHandler(requestInfo.getFunctionHandler()).build()).build());
        }catch (Exception e){
            e.printStackTrace();
            logWriter.createContainerError(new CreateContainerErrorDTO(requestInfo.getRequestId(),e));
        }
        ContainerInfo containerInfo = new ContainerInfo(
                container.getContainerId(),
                requestInfo.getFunctionName(),
                selectedNode.getNodeId(),
                selectedNode.getAddress(),
                selectedNode.getPort(),
                requestInfo.getMemoryInBytes(),
                requestInfo.getMemoryInBytes() * 0.67 / (1024 * 1024 * 1024),
                GlobalInfo.functionStatisticsMap.get(requestInfo.getFunctionName()).getParallelism());
        GlobalInfo.containerInfoMap.put(containerInfo.getContainerId(),containerInfo);
        ContainerStatus containerStatus = new ContainerStatus(containerInfo.getContainerId(),containerInfo.getFunctionName());
        GlobalInfo.containerStatusMap.put(containerStatus.getContainerId(),containerStatus);
        GlobalInfo.nodeStatusMap.get(selectedNode.getNodeId()).getContainerStatusMap().put(containerStatus.getContainerId(),containerStatus);
        GlobalInfo.nodeInfoMap.get(selectedNode.getNodeId()).getContainerInfoMap().put(containerInfo.getContainerId(),containerInfo);
        GlobalInfo.containerIdMap.get(containerInfo.getFunctionName()).add(containerInfo.getContainerId());
        logWriter.newContainerInfo(new NewContainerDTO(requestInfo.getRequestId(),selectedNode.getNodeId(),containerInfo.getContainerId()));
        synchronized (GlobalInfo.containerLRU){
            GlobalInfo.containerLRU.put(containerInfo.getContainerId(),containerInfo);
        }
        Object lock = GlobalInfo.functionLockMap.get(containerInfo.getFunctionName());
        synchronized (lock){
            lock.notifyAll();
        }
        try {
            GlobalInfo.createContainerThreadQueue.put(this);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private NodeInfo getBestNode(){
        // 找一个相同function少的可用节点
        // function数量相同，选内存最少的
        NodeInfo selectedNode = null;
        int functionNum = Integer.MAX_VALUE;
        long memory = Long.MIN_VALUE;
        String functionName = requestInfo.getFunctionName();
        synchronized (GlobalInfo.nodeInfoMap){
            for(NodeInfo nodeInfo : GlobalInfo.nodeInfoMap.values()){
                if(nodeInfo.getAvailableMemInBytes() > requestInfo.getMemoryInBytes()){
                    if(nodeInfo.getContainerNumMap().getOrDefault(functionName,0) < functionNum){
                        functionNum = nodeInfo.getContainerNumMap().getOrDefault(functionName,0);
                        selectedNode = nodeInfo;
                    }else if(nodeInfo.getContainerNumMap().getOrDefault(functionName,0) == functionNum && nodeInfo.getAvailableMemInBytes() > memory){
                        memory = nodeInfo.getAvailableMemInBytes();
                        selectedNode = nodeInfo;
                    }
                }
            }
            if(selectedNode != null){
                selectedNode.setAvailableMemInBytes(selectedNode.getAvailableMemInBytes() - requestInfo.getMemoryInBytes());
                selectedNode.setAvailableVCPU(selectedNode.getAvailableVCPU() - requestInfo.getMemoryInBytes() * 0.67 / (1024 * 1024 * 1024));
                selectedNode.getContainerNumMap().put(functionName,GlobalInfo.nodeInfoMap.get(selectedNode.getNodeId()).getContainerNumMap().getOrDefault(functionName,0) + 1);
            }
        }

        return selectedNode;
    }
}
