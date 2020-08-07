package com.aliyun.mini.scheduler.core.impl_0802.monitor;

import com.aliyun.mini.scheduler.core.impl_0802.global.GlobalInfo;
import com.aliyun.mini.scheduler.core.impl_0802.model.ContainerStatus;
import com.aliyun.mini.scheduler.core.impl_0802.model.FunctionStatistics;
import com.aliyun.mini.scheduler.core.impl_0802.model.NodeStatus;
import nodeservoceproto.NodeServiceOuterClass.*;

import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

/**
 *
 */
public class NodeUpdateThread implements Runnable {

    private LinkedBlockingQueue<NodeUpdateWork> nodeStatsQueue = new LinkedBlockingQueue<>();

    static class NodeUpdateWork{
        GetStatsReply stats;
        NodeStatus nodeStatus;
        public NodeUpdateWork(GetStatsReply stats , NodeStatus nodeStatus){
            this.stats = stats;
            this.nodeStatus = nodeStatus;
        }
    }

    public void submit(NodeUpdateWork nodeUpdateWork){
        try {
            nodeStatsQueue.put(nodeUpdateWork);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void run() {

        while(true){
            NodeUpdateWork nodeUpdateWork = null;
            GetStatsReply stats;
            NodeStatus nodeStatus;
            try {
                nodeUpdateWork = nodeStatsQueue.take();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            nodeStatus = nodeUpdateWork.nodeStatus;
            stats = nodeUpdateWork.stats;
            NodeStats nodeStats = stats.getNodeStats();
            nodeStatus.setTotalMemoryInBytes(nodeStats.getTotalMemoryInBytes());
            nodeStatus.appendAvailableMemoryInBytesHistory(nodeStats.getAvailableMemoryInBytes());
            nodeStatus.appendMemoryUsageInBytesHistory(nodeStats.getMemoryUsageInBytes());
            nodeStatus.appendCPUUsagePctHistory(nodeStats.getCpuUsagePct());
            Map<String, ContainerStatus> containerStatusMap = nodeStatus.getContainerStatusMap();
            ContainerStatus containerStatus;
            FunctionStatistics functionStatistics;
            for(ContainerStats containerStats : stats.getContainerStatsListList()){
                containerStatus = containerStatusMap.get(containerStats.getContainerId());
                if(containerStatus != null){
                    containerStatus.setTotalMemoryInBytes(containerStats.getTotalMemoryInBytes());
                    containerStatus.appendMemoryUsageInBytesHistory(containerStats.getMemoryUsageInBytes());
                    containerStatus.appendCPUUsagePctHistory(containerStats.getCpuUsagePct());
                    functionStatistics = GlobalInfo.functionStatisticsMap.get(containerStatus.getFunctionName());
                    if(containerStats.getMemoryUsageInBytes() > 0){
                        functionStatistics.appendMemSamp(containerStats.getMemoryUsageInBytes());
                    }
                    if(containerStats.getCpuUsagePct() > 0){
                        functionStatistics.appendCpuSamp(containerStats.getCpuUsagePct());
                    }
                }
            }
        }

    }
}

