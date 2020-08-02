package com.aliyun.mini.scheduler.core.impl_0802.global;

import com.aliyun.mini.scheduler.core.impl_0802.model.*;
import io.grpc.netty.shaded.io.netty.util.internal.ConcurrentSet;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

/**
 *
 */
public class GlobalInfo {

    // functionName -> RequestQueue
    public static Map<String , LinkedBlockingQueue<RequestInfo>> requestQueueMap = new ConcurrentHashMap<>();
    // nodeId -> NodeInfo
    public static Map<String , NodeInfo> nodeInfoMap = new ConcurrentHashMap<>();
    // ContainerId -> ContainerInfo
    public static Map<String, ContainerInfo> containerInfoMap = new ConcurrentHashMap<>();
    // nodeId -> NodeStats
    public static Map<String, NodeStatus> nodeStatusMap = new ConcurrentHashMap<>();
    // nodeId -> ContainerStats
    public static Map<String, ContainerStatus> containerStatusMap = new ConcurrentHashMap<>();

    // functionName -> containerIds set
    public static Map<String, ConcurrentSet<String>> containerIdMap = new ConcurrentHashMap<>();
    // function比较保守的并行能力，由synstats设置，nodeContainerManager在创建container时用来初始化containerInfo
    public static Map<String, Integer> functionConcurrencyMap = new ConcurrentHashMap<>();
    // functionName -> Lock 每个function一个锁
    public static Map<String, Object> lockMap = new ConcurrentHashMap<>();

}
