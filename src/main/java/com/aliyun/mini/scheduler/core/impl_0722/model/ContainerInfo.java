package com.aliyun.mini.scheduler.core.impl_0722.model;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.HashSet;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ContainerInfo {
    // containerId
    private String id;
    // node address
    private String address;
    // node port
    private long port;
    // node id
    private String nodeId;
    // 正在使用该container的request集合
    private HashSet<String> requestSet;
}
