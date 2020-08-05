package com.aliyun.mini.scheduler.core.impl_0802.node_container_manager;

import com.aliyun.mini.scheduler.core.impl_0802.global.GlobalInfo;
import com.aliyun.mini.scheduler.core.impl_0802.model.*;
import com.java.mini.faas.ana.dto.CreateContainerErrorDTO;
import com.java.mini.faas.ana.dto.NewContainerDTO;
import com.java.mini.faas.ana.log.LogWriter;
import io.grpc.netty.shaded.io.netty.util.internal.ConcurrentSet;
import lombok.extern.slf4j.Slf4j;
import nodeservoceproto.NodeServiceOuterClass.*;
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
        boolean cleanFlag = false; // 标识是否通知了ContainerClean线程去清理container
        while (true){
            // 如果请求已经被消费了，就不用再找node创建container了
            if(requestInfo.getEnd().get()){
                try {
                    GlobalInfo.createContainerThreadQueue.put(this);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return;
            }
            selectedNode = getBestNode();
            if(selectedNode != null){
                break;
            }else{
                // 没有可以用的node，申请一个node?等待?这里先直接申请
                synchronized (GlobalInfo.nodeLock){
                    // double check
                    selectedNode  = getBestNode();
                    if(selectedNode == null){
                        try {
                            // 没有可用的node，不再主动创建node，而是1.等着container的清理 2.等着NodeApplyThread创建新的node 3.等着request被消费
                            // nodeLock只能在123的情况被唤醒
                            if(!cleanFlag){
                                ContainerCleanThread.cleanContainerQueue.put(requestInfo);
                                cleanFlag = true;
                            }
                            GlobalInfo.nodeLock.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        // 如果request已经被消费了，那么不再创建container，返还预拿的node的memory
        if(requestInfo.getEnd().get()){
            synchronized (selectedNode){
                selectedNode.setAvailableMemInBytes(selectedNode.getAvailableMemInBytes() + requestInfo.getMemoryInBytes());
                selectedNode.setAvailableVCPU(selectedNode.getAvailableVCPU() + requestInfo.getMemoryInBytes() * 0.67 / (1024 * 1024 * 1024));
            }
            try {
                GlobalInfo.createContainerThreadQueue.put(this);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return;
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
        ContainerInfo containerInfo = new ContainerInfo(container.getContainerId(),requestInfo.getFunctionName(),selectedNode.getNodeId(),selectedNode.getAddress(),
                selectedNode.getPort(),requestInfo.getMemoryInBytes(),requestInfo.getMemoryInBytes() * 0.67 / (1024 * 1024 * 1024),
                1,
                new ConcurrentSet<>(),
                false);
        GlobalInfo.containerInfoMap.put(containerInfo.getContainerId(),containerInfo);
        ContainerStatus containerStatus = new ContainerStatus(containerInfo.getContainerId());
        GlobalInfo.containerStatusMap.put(containerStatus.getContainerId(),containerStatus);
        GlobalInfo.nodeStatusMap.get(selectedNode.getNodeId()).getContainerStatusMap().put(containerStatus.getContainerId(),containerStatus);
        GlobalInfo.nodeInfoMap.get(selectedNode.getNodeId()).getContainerInfoMap().put(containerInfo.getContainerId(),containerInfo);
        GlobalInfo.containerIdMap.get(containerInfo.getFunctionName()).add(containerInfo.getContainerId());
        logWriter.newContainerInfo(new NewContainerDTO(requestInfo.getRequestId(),selectedNode.getNodeId(),containerInfo.getContainerId()));
        synchronized (GlobalInfo.containerLRU){
            GlobalInfo.containerLRU.put(containerInfo.getContainerId(),containerInfo);
        }
        System.out.println(requestInfo.getRequestId()+"-----");
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
        NodeInfo selectedNode = null;
        for(NodeInfo nodeInfo : GlobalInfo.nodeInfoMap.values()){
            synchronized (nodeInfo){
                if(nodeInfo.getAvailableMemInBytes() > requestInfo.getMemoryInBytes()){
                    selectedNode = nodeInfo;
                    nodeInfo.setAvailableMemInBytes(nodeInfo.getAvailableMemInBytes() - requestInfo.getMemoryInBytes());
                    selectedNode.setAvailableVCPU(selectedNode.getAvailableVCPU() - requestInfo.getMemoryInBytes() * 0.67 / (1024 * 1024 * 1024));
                    break;
                }
            }
        }
        return selectedNode;
    }

}
