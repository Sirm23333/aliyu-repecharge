package com.aliyun.mini.scheduler.core.impl_0802.model;
import com.aliyun.mini.scheduler.core.impl_0730.synstats.StatsConstans;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NodeStatus {
    private String nodeId;
    // node最大物理内存
    // 注意 totalMemoryInBytes > memoryUsageInBytes + availableMemoryInBytes,这里的totalMemoryInBytes好像没用
    private long totalMemoryInBytes = 0;
    // 历史使用内存
    private Queue<Long> memoryUsageInBytesHistory = new ArrayBlockingQueue<>(StatsConstans.SAVE_NODE_STATS_CYC_CNT);
    // 历史可用内存
    private Queue<Long> availableMemoryInBytesHistory = new ArrayBlockingQueue<>(StatsConstans.SAVE_NODE_STATS_CYC_CNT);
    // 历史使用CPU，范围为0~200
    private Queue<Double> cpuUsagePctHistory = new ArrayBlockingQueue<>(StatsConstans.SAVE_NODE_STATS_CYC_CNT);
    // containerId -> ContainerStats
    private Map<String , ContainerStatus> containerStatusMap = new HashMap<>();

    public NodeStatus(String nodeId){
        this.nodeId = nodeId;
    }
    public void appendMemoryUsageInBytesHistory(Long memoryUsageInBytes){
        if(memoryUsageInBytesHistory.size() >= StatsConstans.SAVE_NODE_STATS_CYC_CNT){
            memoryUsageInBytesHistory.poll();
        }
        memoryUsageInBytesHistory.add(memoryUsageInBytes);
    }
    public void appendAvailableMemoryInBytesHistory(Long availableMemoryInBytes){
        if(availableMemoryInBytesHistory.size() >= StatsConstans.SAVE_NODE_STATS_CYC_CNT){
            availableMemoryInBytesHistory.poll();
        }
        availableMemoryInBytesHistory.add(availableMemoryInBytes);
    }
    public void appendCPUUsagePctHistory(double cpuUsagePct){
        if(cpuUsagePctHistory.size() >= StatsConstans.SAVE_NODE_STATS_CYC_CNT){
            cpuUsagePctHistory.poll();
        }
        cpuUsagePctHistory.add(cpuUsagePct);
    }

}
