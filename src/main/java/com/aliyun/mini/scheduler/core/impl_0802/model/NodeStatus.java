package com.aliyun.mini.scheduler.core.impl_0802.model;
import com.aliyun.mini.nodeservice.client.NodeServiceClient;
import com.aliyun.mini.scheduler.core.impl_0802.monitor.MonitorConstants;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

@Data
public class NodeStatus {

    private String nodeId;
    // node最大物理内存
    // 注意 totalMemoryInBytes > memoryUsageInBytes + availableMemoryInBytes,这里的totalMemoryInBytes好像没用
    private long totalMemoryInBytes = 0;
    // 历史使用内存
    private ArrayDeque<Long> memoryUsageInBytesHistory = new ArrayDeque<>(MonitorConstants.SAVE_NODE_STATS_CYC_CNT);
    // 历史可用内存
    private ArrayDeque<Long> availableMemoryInBytesHistory = new ArrayDeque<>(MonitorConstants.SAVE_NODE_STATS_CYC_CNT);
    // 历史使用CPU，范围为0~200
    private ArrayDeque<Double> cpuUsagePctHistory = new ArrayDeque<>(MonitorConstants.SAVE_NODE_STATS_CYC_CNT);
    // containerId -> ContainerStats
    private Map<String , ContainerStatus> containerStatusMap = new HashMap<>();
    private NodeServiceClient nodeServiceClient;

    public NodeStatus(String nodeId, NodeServiceClient nodeServiceClient) {
        this.nodeId = nodeId;
        this.nodeServiceClient = nodeServiceClient;
    }

    public void appendMemoryUsageInBytesHistory(Long memoryUsageInBytes){
        if(memoryUsageInBytesHistory.size() >= MonitorConstants.SAVE_NODE_STATS_CYC_CNT){
            memoryUsageInBytesHistory.poll();
        }
        memoryUsageInBytesHistory.add(memoryUsageInBytes);
    }
    public void appendAvailableMemoryInBytesHistory(Long availableMemoryInBytes){
        if(availableMemoryInBytesHistory.size() >= MonitorConstants.SAVE_NODE_STATS_CYC_CNT){
            availableMemoryInBytesHistory.poll();
        }
        availableMemoryInBytesHistory.add(availableMemoryInBytes);
    }
    public void appendCPUUsagePctHistory(double cpuUsagePct){
        if(cpuUsagePctHistory.size() >= MonitorConstants.SAVE_NODE_STATS_CYC_CNT){
            cpuUsagePctHistory.poll();
        }
        cpuUsagePctHistory.add(cpuUsagePct);
    }

}
