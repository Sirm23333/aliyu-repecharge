package com.aliyun.mini.scheduler.core.impl_0730.global;

import com.aliyun.mini.scheduler.core.impl_0730.model.*;

import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 */
public class GlobalInfo {

    // functionName -> RequestQueue
    public static Map<String , Queue<RequestInfo>> requestQueueMap = new ConcurrentHashMap<>();
    // nodeId -> NodeInfo
    public static Map<String , NodeInfo> nodeInfoMap = new ConcurrentHashMap<>();
    // ContainerId -> ContainerInfo
    public static Map<String, ContainerInfo> containerInfoMap = new ConcurrentHashMap<>();
    // nodeId -> NodeStats
    public static Map<String, NodeStatus> nodeStatusMap = new ConcurrentHashMap<>();
    // nodeId -> ContainerStats
    public static Map<String, ContainerStatus> containerStatusMap = new ConcurrentHashMap<>();

    // functionName -> containerIds set
    public static Map<String, Set<String>> functionNameMap = new ConcurrentHashMap<>();
    // function比较保守的并行能力，由synstats设置，nodeContainerManager在创建container时用来初始化containerInfo
    public static Map<String, Integer> functionConcurrencyMap = new ConcurrentHashMap<>();

}
