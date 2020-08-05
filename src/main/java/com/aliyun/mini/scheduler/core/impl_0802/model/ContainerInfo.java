package com.aliyun.mini.scheduler.core.impl_0802.model;

import io.grpc.netty.shaded.io.netty.util.internal.ConcurrentSet;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private ConcurrentSet<String> requestSet;

    private boolean deleted;
}
