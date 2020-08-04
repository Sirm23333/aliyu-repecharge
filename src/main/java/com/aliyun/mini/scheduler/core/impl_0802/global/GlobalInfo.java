package com.aliyun.mini.scheduler.core.impl_0802.global;

import com.aliyun.mini.resourcemanager.client.ResourceManagerClient;
import com.aliyun.mini.scheduler.core.impl_0802.model.*;
import com.aliyun.mini.scheduler.core.impl_0802.node_container_manager.CreateContainerThread;
import com.aliyun.mini.scheduler.core.impl_0802.node_container_manager.ReserveNodeThread;
import io.grpc.netty.shaded.io.netty.util.internal.ConcurrentSet;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
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
    // functionName -> Lock 每个function一个锁 ， 用于Strategic线程与其他可以产生可用container的线性直接通信
    public static Map<String, Object> functionLockMap = new ConcurrentHashMap<>();
    // 创建或删除node时必须得到锁
    public static Object nodeLock = new Object();


    public static ExecutorService threadPool = Executors.newFixedThreadPool(64);
    public static LinkedBlockingQueue<CreateContainerThread> createContainerThreadQueue;
    public static LinkedBlockingQueue<ReserveNodeThread> reserveNodeThreadQueue;


    public static ResourceManagerClient resourceManagerClient;

}
