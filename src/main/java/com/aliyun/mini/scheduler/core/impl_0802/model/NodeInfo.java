package com.aliyun.mini.scheduler.core.impl_0802.model;

import com.aliyun.mini.nodeservice.client.NodeServiceClient;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NodeInfo {
    private String nodeId;
    private String address;
    private long port;
    // 剩余可用内存
    private long availableMemInBytes;
    // 剩余可分配vCPU
    private double availableVCPU;
    private NodeServiceClient client;
    // 现加载的container,ContainerId -> ContainerInfo
    private ConcurrentHashMap<String, ContainerInfo> containerInfoMap;
}
