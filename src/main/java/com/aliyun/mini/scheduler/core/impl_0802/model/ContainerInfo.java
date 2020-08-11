package com.aliyun.mini.scheduler.core.impl_0802.model;

import io.grpc.netty.shaded.io.netty.util.internal.ConcurrentSet;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

@Data
public class ContainerInfo {
    private String containerId;
    private String functionName;
    private String nodeId;
    private String address;
    private RequestInfo requestInfo;
    private long port;
    // 该容器的内存上限
    private long memoryInBytes;
    // 该容器的 vCPU = memoryInBytes / (1024 * 1024 * 1024) * 0.67
    private double vCPU;
    // 并行数上限
    private int concurrencyUpperLimit;

    // 正在使用中的requestId
    private ConcurrentSet<String> requestSet = new ConcurrentSet<>();
    // 是否已经被删除
    private boolean deleted = false;
    // 使用这个container的次数
    private int useCnt = 0;
    // 平均使用时间,ms
    private double avgDuration = 0;
    // 标记清除次数，如果avgDuration小于一个阈值(CONTAINER_CLEAN_AVG_DURATION_LOWER)，则清除时需要标记多次(CONTAINER_CLEAN_SIGN_CNT)
    private int signCleanCnt = 0;

    private AtomicInteger choiceTmpCnt = new AtomicInteger(0);

    private long lastUseTimeStamp = Long.MAX_VALUE;


    public ContainerInfo(String containerId, String functionName, String nodeId, String address, long port, long memoryInBytes, double vCPU, int concurrencyUpperLimit,RequestInfo requestInfo) {
        this.containerId = containerId;
        this.functionName = functionName;
        this.nodeId = nodeId;
        this.address = address;
        this.port = port;
        this.memoryInBytes = memoryInBytes;
        this.vCPU = vCPU;
        this.concurrencyUpperLimit = concurrencyUpperLimit;
        this.requestInfo = requestInfo;
    }
}
