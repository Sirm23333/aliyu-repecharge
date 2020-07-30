package com.aliyun.mini.scheduler.core.impl_0722.model;

import com.aliyun.mini.nodeservice.client.NodeServiceClient;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NodeInfo {
    private String nodeId;
    private String address;
    private long port;
    // 剩余可用内存
    private long availableMemInBytes;
    private NodeServiceClient client;
    private Set<String> containerSet;

}
