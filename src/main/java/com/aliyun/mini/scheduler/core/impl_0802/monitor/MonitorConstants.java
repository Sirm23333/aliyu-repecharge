package com.aliyun.mini.scheduler.core.impl_0802.monitor;

/**
 *
 */
public class MonitorConstants {
    // 同步stats周期，单位ms
    public static final long SYN_NODE_STATS_CYC = 80;
    // 保存近几个周期的nodeStats信息
    public static final int SAVE_NODE_STATS_CYC_CNT = 600;
    // 最少cpu采样次数
    public static final int MIN_CPU_SAMP_CNT = 500;
    // 最少需要的内存采样次数
    public static final int MIN_MEM_SAMP_CNT = 1000;
    // 平均内存大于100M视为内存密集型函数
    public static final long MEM_THRESHOLD = 100 * 1024 * 1024;
    // 平均cpu大于10视为cpu密集型
    public static final double CPU_THRESHOLD = 10;
    // 平均cpu小于0.1视为可并行
    public static final double CPU_THRESHOLD_LOW = 0.1;


}
