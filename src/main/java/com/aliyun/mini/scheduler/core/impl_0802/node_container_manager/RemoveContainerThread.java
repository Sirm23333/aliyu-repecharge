package com.aliyun.mini.scheduler.core.impl_0802.node_container_manager;

import com.aliyun.mini.scheduler.core.impl_0802.global.GlobalInfo;
import com.aliyun.mini.scheduler.core.impl_0802.model.ContainerInfo;
import com.aliyun.mini.scheduler.core.impl_0802.model.NodeInfo;
import com.java.mini.faas.ana.dto.RemoveContainerDTO;
import com.java.mini.faas.ana.log.LogWriter;
import nodeservoceproto.NodeServiceOuterClass;

/**
 * 删除container
 * 1.container执行异常时启动一次
 * 2.container清理线程中启动n次
 */
public class RemoveContainerThread implements Runnable{

    LogWriter logWriter = LogWriter.getInstance();

    private ContainerInfo containerInfo;

    public RemoveContainerThread build(ContainerInfo containerInfo){
        this.containerInfo = containerInfo;
        return this;
    }

    @Override
    public void run() {
        try {
            String containerId = containerInfo.getContainerId();
            String functionName = containerInfo.getFunctionName();
            String nodeId = containerInfo.getNodeId();
            GlobalInfo.containerIdMap.get(functionName).remove(containerId);
            GlobalInfo.containerInfoMap.remove(containerId);
            NodeInfo nodeInfo =  GlobalInfo.nodeInfoMap.get(nodeId);
            nodeInfo.getContainerInfoMap().remove(containerId);
            nodeInfo.setAvailableMemInBytes(nodeInfo.getAvailableMemInBytes() + containerInfo.getRealMemoryInBytes());
            nodeInfo.setAvailableVCPU(nodeInfo.getAvailableVCPU() + containerInfo.getVCPU());
            GlobalInfo.nodeInfoMap.get(nodeId).getContainerNumMap().put(containerInfo.getFunctionName(),GlobalInfo.nodeInfoMap.get(nodeId).getContainerNumMap().get(containerInfo.getFunctionName())-1);
            // 正式删除container
            GlobalInfo.nodeInfoMap.get(nodeId).getClient().removeContainer(NodeServiceOuterClass.RemoveContainerRequest.newBuilder().setContainerId(containerId).build());
            synchronized (GlobalInfo.nodeLock){
                GlobalInfo.nodeLock.notifyAll();
            }
            logWriter.removeContainerInfo(new RemoveContainerDTO(containerId));
        }catch (Exception e){
            e.printStackTrace();
        }
        try {
            GlobalInfo.removeContainerThreadQueue.put(this);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
