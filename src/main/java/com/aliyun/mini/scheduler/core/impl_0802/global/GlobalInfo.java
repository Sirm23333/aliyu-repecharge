package com.aliyun.mini.scheduler.core.impl_0802.global;

import com.aliyun.mini.resourcemanager.client.ResourceManagerClient;
import com.aliyun.mini.scheduler.core.impl_0802.model.*;
import com.aliyun.mini.scheduler.core.impl_0802.node_container_manager.ContainerCleanThread;
import com.aliyun.mini.scheduler.core.impl_0802.node_container_manager.CreateContainerThread;
import com.aliyun.mini.scheduler.core.impl_0802.node_container_manager.RemoveContainerThread;
import com.aliyun.mini.scheduler.core.impl_0802.node_container_manager.ReserveNodeThread;
import io.grpc.netty.shaded.io.netty.util.internal.ConcurrentSet;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 */
public class GlobalInfo {

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


    // functionName -> Lock 每个function一个锁 ， 用于Strategic线程与其他可以产生可用container的线性之间的通信
    // StrategicThread中，如果没有可用的container，则进入wait(),由其他可以产生container的线程（如创建新的container，提高了container的并行度，returnContainer）唤醒
    public static Map<String, Object> functionLockMap = new ConcurrentHashMap<>();
    // node锁，CreateContainer时如果没有node可用，则进入nodeLock.wait()状态，其他可以产生node的线程通过nodeLock.notifyAll()唤醒
    public static Object nodeLock = new Object();

    // containerId -> containerInfo 使用LinkedHashMap构建一个lru队列，当某个container使用时，就get一下，这个container就会移到队末，即队头的元素就是最需要删除的元素
    public static Map<String,ContainerInfo> containerLRU = new LinkedHashMap<>(256,0.75f,true);


    public static ExecutorService threadPool;
    // 创建container的线程队列，拿到队列中的线程后才能创建，用于限制同时创建container的数量
    public static LinkedBlockingQueue<CreateContainerThread> createContainerThreadQueue;
    public static LinkedBlockingQueue<RemoveContainerThread> removeContainerThreadQueue;
    public static LinkedBlockingQueue<ContainerCleanThread> containerCleanThreads;
    public static LinkedBlockingQueue<ReserveNodeThread> reserveNodeThreadQueue;

    public static ResourceManagerClient resourceManagerClient;

    public static Map<String,FunctionStatistics> functionStatisticsMap = new ConcurrentHashMap<>();

    public static Map<String,Long> useStartMap_Tmp = new ConcurrentHashMap<>();

    // 正在创建某个function的container的数量
    public static Map<String, AtomicInteger> creatingContainerNumMap = new ConcurrentHashMap<>();
    // 正在等待container创建的request数量
    public static Map<String,AtomicInteger> waitingCreateContainerNumMap = new ConcurrentHashMap<>();

}
