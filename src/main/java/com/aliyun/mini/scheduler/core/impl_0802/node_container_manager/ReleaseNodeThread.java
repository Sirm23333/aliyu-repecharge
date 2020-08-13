package com.aliyun.mini.scheduler.core.impl_0802.node_container_manager;

import com.aliyun.mini.resourcemanager.client.ResourceManagerClient;
import com.aliyun.mini.scheduler.core.impl_0802.global.GlobalInfo;
import com.aliyun.mini.scheduler.core.impl_0802.model.ContainerInfo;
import com.aliyun.mini.scheduler.core.impl_0802.model.NodeInfo;
import com.aliyun.mini.scheduler.core.impl_0802.model.NodeStatus;
import com.aliyun.mini.scheduler.core.impl_0802.monitor.NodeMonitorThread;
import lombok.extern.slf4j.Slf4j;
import resourcemanagerproto.ResourceManagerOuterClass;

@Slf4j
public class ReleaseNodeThread implements Runnable {
    private NodeInfo nodeInfo;
    private ResourceManagerClient resourceManager = GlobalInfo.resourceManagerClient;
    public ReleaseNodeThread build(NodeInfo nodeInfo){
        this.nodeInfo = nodeInfo;
        return this;
    }
    @Override
    public void run() {
        try {
//            System.out.println("to delete..."+nodeInfo);
            synchronized (nodeInfo){
                nodeInfo.setDeleted(true);
            }
            boolean canDeleted = true;
            // 检测有没有正在使用的container
            for(ContainerInfo containerInfo : nodeInfo.getContainerInfoMap().values()){
                canDeleted = false;
                synchronized (containerInfo){
                    if(!containerInfo.getRequestSet().isEmpty()){
                        //  如果这个container正在执行，则标记为删除，在return的时候正式删除
                        containerInfo.setDeleted(true);
                    }else if(!containerInfo.isDeleted()) {
                        containerInfo.setDeleted(true);
                        GlobalInfo.threadPool.execute(GlobalInfo.removeContainerThreadQueue.take().build(containerInfo));
                    }
                }
            }
            // 如果还有在运行的就等10s正式删除
            if(!canDeleted){
                Thread.sleep(10000);
            }

            GlobalInfo.nodeInfoMap.remove(nodeInfo.getNodeId());
            NodeStatus nodeStatus = GlobalInfo.nodeStatusMap.get(nodeInfo.getNodeId());
            GlobalInfo.nodeStatusMap.remove(nodeInfo.getNodeId());
            // 删除监控
            NodeMonitorThread.removeNode(nodeStatus);
            resourceManager.releaseNode(ResourceManagerOuterClass.ReleaseNodeRequest.newBuilder().setId(nodeInfo.getNodeId()).build());
//            System.out.println("release Node");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            try {
                GlobalInfo.releaseNodeThreadQueue.put(this);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
