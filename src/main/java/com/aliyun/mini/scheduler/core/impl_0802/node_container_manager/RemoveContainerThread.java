package com.aliyun.mini.scheduler.core.impl_0802.node_container_manager;

import com.aliyun.mini.scheduler.core.impl_0802.global.GlobalInfo;
import com.aliyun.mini.scheduler.core.impl_0802.model.ContainerInfo;
import com.aliyun.mini.scheduler.core.impl_0802.model.NodeInfo;
import com.java.mini.faas.ana.dto.RemoveContainerDTO;
import com.java.mini.faas.ana.log.LogWriter;
import nodeservoceproto.NodeServiceOuterClass;

/**
 * returnContainer若执行失败调用启动
 */
public class RemoveContainerThread implements Runnable{

    LogWriter logWriter = LogWriter.getInstance();

    private ContainerInfo containerInfo;

    public RemoveContainerThread(ContainerInfo containerInfo){
        this.containerInfo = containerInfo;
    }

    @Override
    public void run() {
        String containerId = containerInfo.getContainerId();
        String functionName = containerInfo.getFunctionName();
        String nodeId = containerInfo.getNodeId();
        GlobalInfo.containerIdMap.get(functionName).remove(containerId);
        GlobalInfo.containerInfoMap.remove(containerId);
        NodeInfo nodeInfo =  GlobalInfo.nodeInfoMap.get(nodeId);
        nodeInfo.getContainerInfoMap().remove(containerId);
        nodeInfo.setAvailableMemInBytes(nodeInfo.getAvailableMemInBytes() + containerInfo.getMemoryInBytes());
        nodeInfo.setAvailableVCPU(nodeInfo.getAvailableVCPU() + containerInfo.getVCPU());
        // 正式删除container
        GlobalInfo.nodeInfoMap.get(nodeId).getClient().removeContainer(NodeServiceOuterClass.RemoveContainerRequest.newBuilder().setContainerId(containerId).build());
        try {
            logWriter.removeContainerInfo(new RemoveContainerDTO(containerId));
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
