package com.aliyun.mini.scheduler.core.impl_0802.node_container_manager;

/**
 *
 */
public class NodeContainerManagerContants {
    // 允许同时创建container的最大线程数
    public static int CREATE_CONTAINER_CONCURRENT_UPPER = 10;
    // 允许同时删除container的最大线程数
    public static int REMOVE_CONTAINER_CONCURRENT_UPPER = 10;
    // 允许同时清理container的最大线程数
    public static int CLEAN_CONTAINER_CONCURRENT_UPPER = 5;
    // 允许同时申请node的最大线程数
    public static int RESERVE_NODE_CONCURRENT_UPPER = 1;
    // 最多允许使用的node数
    public static int MAX_NODE_NUM = 18;
    // 清理container时使用多级缓存置换+LRU算法，对响应时间小于以下阈值时间(ms)的container做为低优先清除的container
    public static long CONTAINER_CLEAN_AVG_DURATION_LOWER = 100;
    // 低优先级清除的container需要标记CONTAINER_CLEAN_SIGN_CNT次才可以被删除
    public static int CONTAINER_CLEAN_SIGN_CNT_LOWER = 10;
}
