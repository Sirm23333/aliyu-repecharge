package com.aliyun.mini.scheduler.core.impl_0802.model;

import com.aliyun.mini.scheduler.core.impl_0802.monitor.MonitorConstants;
import lombok.Data;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

@Data
public class ContainerStatus {
    private String containerId;
    private String functionName;
    // container的内存上限
    private long totalMemoryInBytes = 0;
    // 正在使用内存
    private ArrayDeque<Long> memoryUsageInBytesHistory = new ArrayDeque<>(MonitorConstants.SAVE_NODE_STATS_CYC_CNT);;
    // 正在使用CPU，范围为0~200
    private ArrayDeque<Double> cpuUsagePctHistory = new ArrayDeque<>(MonitorConstants.SAVE_NODE_STATS_CYC_CNT);;

    public ContainerStatus(String containerId , String functionName) {
        this.containerId = containerId;
        this.functionName = functionName;
    }

    public void appendMemoryUsageInBytesHistory(Long memoryUsageInBytes){
        if(memoryUsageInBytesHistory.size() >= MonitorConstants.SAVE_NODE_STATS_CYC_CNT){
            memoryUsageInBytesHistory.poll();
        }
        memoryUsageInBytesHistory.add(memoryUsageInBytes);
    }
    public void appendCPUUsagePctHistory(double cpuUsagePct){
        if(cpuUsagePctHistory.size() >= MonitorConstants.SAVE_NODE_STATS_CYC_CNT){
            cpuUsagePctHistory.poll();
        }
        cpuUsagePctHistory.add(cpuUsagePct);
    }


}
