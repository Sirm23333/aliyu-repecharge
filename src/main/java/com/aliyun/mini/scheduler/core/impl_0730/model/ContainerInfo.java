package com.aliyun.mini.scheduler.core.impl_0730.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContainerInfo {
    private String containerId;
    private String functionName;
    private String nodeId;
    private String address;
    private long port;
    // 该容器的内存上限
    private long memoryInBytes;
    // 该容器的 vCPU = memoryInBytes / (1024 * 1024 * 1024) * 0.67
    private double vCPU;
    // 并行数上限
    private int concurrencyUpperLimit;
    // 正在使用中的requestId
    private Set<String> requestSet;
}
