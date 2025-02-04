package com.aliyun.mini.scheduler.core.impl_0802.node_container_manager;

public class NodeContainerManagerContants {
    // 允许同时创建container的最大线程数
    public static int CREATE_CONTAINER_CONCURRENT_UPPER = 20;
    // 允许同时删除container的最大线程数
    public static int REMOVE_CONTAINER_CONCURRENT_UPPER = 10;
    // 同时迁移container的线程数量
    public static int MOVE_CONTAINER_CONCURRENT_UPPER = 10;
    // 允许同时清理container的最大线程数
    public static int CLEAN_CONTAINER_CONCURRENT_UPPER = 5;
    // 允许同时申请node的最大线程数
    public static int RESERVE_NODE_CONCURRENT_UPPER = 1;
    // 允许同时删除node的最大线程数
    public static int RELEASE_NODE_CONCURRENT_UPPER = 5;
    // 最多允许使用的node数
    public static int MAX_NODE_NUM = 10;
    public static int MIN_NODE_NUM = 5;

    // 清理container时使用多级缓存置换+LRU算法，对响应时间小于以下阈值时间(ms)的container做为低优先清除的container
    public static long CONTAINER_CLEAN_AVG_DURATION_LOWER = 100;
    // 低优先级清除的container需要标记CONTAINER_CLEAN_SIGN_CNT次才可以被删除
    public static int CONTAINER_CLEAN_SIGN_CNT_LOWER = 10;
    // node碎片整理周期时间 5min
    public static long NODE_ORDER_START_TIME = 3 * 60 * 1000;
//    public static long NODE_ORDER_START_TIME = 60 * 1000;

    public static int FUNCTION_TYPE_PARA = 0;
    public static int FUNCTION_TYPE_CPU = 1;
    public static int FUNCTION_TYPE_MEM = 2;
    public static int FUNCTION_TYPE_OTHER = 3;



    // 允许同时创建container的最大线程数
//    public static int CREATE_CONTAINER_CONCURRENT_UPPER = 4;
//    // 允许同时删除container的最大线程数
//    public static int REMOVE_CONTAINER_CONCURRENT_UPPER = 4;
//    // 允许同时清理container的最大线程数
//    public static int CLEAN_CONTAINER_CONCURRENT_UPPER = 2;
//    // 允许同时申请node的最大线程数
//    public static int RESERVE_NODE_CONCURRENT_UPPER = 1;
//    // 最多允许使用的node数
//    public static int MAX_NODE_NUM = 2;
//    // 清理container时使用多级缓存置换+LRU算法，对响应时间小于以下阈值时间(ms)的container做为低优先清除的container
//    public static long CONTAINER_CLEAN_AVG_DURATION_LOWER = 50;
//    // 低优先级清除的container需要标记CONTAINER_CLEAN_SIGN_CNT次才可以被删除
//    public static int CONTAINER_CLEAN_SIGN_CNT_LOWER = 10;

    public static final long NODE_MEMORY = 3072 * 1024 * 1024;
}
