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
 * StrategicCreateContainerThread调用启动
 *
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
//        log.info("[PRE_CREATE_CONTAINER]{}",requestInfo);
        // 先找一个可以创建Container的node
        NodeInfo selectedNode  = null;
        while (true){
            // 如果已经被消费了，就不用再找node创建container了
            if(requestInfo.getEnd().get()){
//                log.info("[GIVE_UP_CREATE_CONTAINER_3]{}",requestInfo);
                try {
//                    log.info("[PUT_CREATE]{}",requestInfo);
                    GlobalInfo.createContainerThreadQueue.put(this);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return;
            }
            selectedNode = getBestNode();
            if(selectedNode == null){
                // 没有可以用的node，申请一个node?等待?这里先直接申请
                synchronized (GlobalInfo.nodeLock){
                    // double check
                    selectedNode  = getBestNode();
                    if(selectedNode == null){
                        // 这里不用阻塞，如果队列中没有可用的ReserveNodeThread，说明某个CreateContainerThread已经在创建node了，直接进入wait状态即可
                        ReserveNodeThread reserveNodeThread = GlobalInfo.reserveNodeThreadQueue.poll();
                        if(reserveNodeThread == null){
                            try {
                                GlobalInfo.nodeLock.wait();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }else{
                            GlobalInfo.threadPool.execute(reserveNodeThread.build(requestInfo));
                        }
                    }
                }
            }else {
                break;
            }
        }
//        log.info("[GET_BEST_NODE_TO_CREATE_CONTAINER]{}",requestInfo);
        // 如果request已经被消费了，那么不再创建container，返还预拿的node的memory
        if(requestInfo.getEnd().get()){
//            log.info("[GIVE_UP_CREATE_CONTAINER_4]{}",requestInfo);
            synchronized (selectedNode){
                selectedNode.setAvailableMemInBytes(selectedNode.getAvailableMemInBytes() + requestInfo.getMemoryInBytes());
                selectedNode.setAvailableVCPU(selectedNode.getAvailableVCPU() + requestInfo.getMemoryInBytes() * 0.67 / (1024 * 1024 * 1024));
            }
            try {
//                log.info("[PUT_CREATE]{}",requestInfo);
                GlobalInfo.createContainerThreadQueue.put(this);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return;
        }
//        log.info("[REAL_TO_CREATE]{}",requestInfo);
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
//        log.info("[NEW_CONTAINER]{}",requestInfo);
        ContainerInfo containerInfo = new ContainerInfo(container.getContainerId(),requestInfo.getFunctionName(),selectedNode.getNodeId(),selectedNode.getAddress(),
                selectedNode.getPort(),requestInfo.getMemoryInBytes(),requestInfo.getMemoryInBytes() * 0.67 / (1024 * 1024 * 1024),
                1,
                new ConcurrentSet<>());
//        selectedNode.setAvailableMemInBytes(selectedNode.getAvailableMemInBytes() - requestInfo.getMemoryInBytes());
//        selectedNode.setAvailableVCPU(selectedNode.getAvailableVCPU() - containerInfo.getVCPU());
        GlobalInfo.containerInfoMap.put(containerInfo.getContainerId(),containerInfo);
        ContainerStatus containerStatus = new ContainerStatus(containerInfo.getContainerId());
        GlobalInfo.containerStatusMap.put(containerStatus.getContainerId(),containerStatus);
        GlobalInfo.nodeStatusMap.get(selectedNode.getNodeId()).getContainerStatusMap().put(containerStatus.getContainerId(),containerStatus);
        GlobalInfo.nodeInfoMap.get(selectedNode.getNodeId()).getContainerInfoMap().put(containerInfo.getContainerId(),containerInfo);
        GlobalInfo.containerIdMap.get(containerInfo.getFunctionName()).add(containerInfo.getContainerId());
        logWriter.newContainerInfo(new NewContainerDTO(requestInfo.getRequestId(),selectedNode.getNodeId(),containerInfo.getContainerId()));
//        log.info("[NEW_CONTAINER_BUILD]{},{}",requestInfo,containerInfo);
        Object lock = GlobalInfo.functionLockMap.get(containerInfo.getFunctionName());
        synchronized (lock){
            lock.notifyAll();
        }
        try {
//            log.info("[PUT_CREATE]{}",requestInfo);
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
