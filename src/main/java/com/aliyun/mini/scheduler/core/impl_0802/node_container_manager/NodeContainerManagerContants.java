package com.aliyun.mini.scheduler.core.impl_0802.node_container_manager;

import io.grpc.netty.shaded.io.netty.util.internal.ConcurrentSet;

/**
 *
 */
public class NodeContainerManagerContants {
    // 允许创建container的并发上限
    public static int CREATE_CONTAINER_CONCURRENT_UPPER = 5;
    // 允许创建node的并发上限
    public static int RESERVE_NODE_CONCURRENT_UPPER = 1;
}
