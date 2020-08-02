package com.aliyun.mini.scheduler.core.impl_0802.model;

import com.aliyun.mini.scheduler.core.impl_0730.synstats.StatsConstans;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ContainerStatus {
    private String containerId;
    // container的内存上限
    private long totalMemoryInBytes = 0;
    // 正在使用内存
    private Queue<Long> memoryUsageInBytesHistory = new ArrayBlockingQueue<>(StatsConstans.SAVE_NODE_STATS_CYC_CNT);;
    // 正在使用CPU，范围为0~200
    private Queue<Double> cpuUsagePctHistory = new ArrayBlockingQueue<>(StatsConstans.SAVE_NODE_STATS_CYC_CNT);;

    public ContainerStatus(String containerId){
        this.containerId = containerId;
    }
    public void appendMemoryUsageInBytesHistory(Long memoryUsageInBytes){
        if(memoryUsageInBytesHistory.size() >= StatsConstans.SAVE_NODE_STATS_CYC_CNT){
            memoryUsageInBytesHistory.poll();
        }
        memoryUsageInBytesHistory.add(memoryUsageInBytes);
    }
    public void appendCPUUsagePctHistory(double cpuUsagePct){
        if(cpuUsagePctHistory.size() >= StatsConstans.SAVE_NODE_STATS_CYC_CNT){
            cpuUsagePctHistory.poll();
        }
        cpuUsagePctHistory.add(cpuUsagePct);
    }


}
