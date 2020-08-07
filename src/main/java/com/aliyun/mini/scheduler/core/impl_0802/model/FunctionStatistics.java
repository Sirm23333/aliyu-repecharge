package com.aliyun.mini.scheduler.core.impl_0802.model;
import lombok.Data;

/**
 * 对某个function的一些统计量
 */
@Data
public class FunctionStatistics {
    private String functionName;
    // 内存采样次数
    private int memSampCnt = 0;
    // 内存使用均值 单位byte
    private double avgMem = 0;
    // 内存使用平方的均值
    private double avgSqMem = 0;
    // 内存使用方差
    private double sMem = 0;
    // 内存使用最大值
    private long maxMem = 0;
    // cpu采用次数
    private int cpuSampCnt = 0;
    // cpu平均消耗 0~200
    private double avgCpu = 0;
    // cpu平方的均值
    private double avgSqCpu = 0;
    // cpu消耗方差
    private double sCpu = 0;
    // 最大cpu消耗
    private double maxCpu = 0;

    public FunctionStatistics(String functionName){
        this.functionName = functionName;
    }
    public void appendMemSamp(long mem){
        memSampCnt++;
        avgMem += (mem - avgMem) / memSampCnt;
        avgSqMem += (mem * mem - avgSqMem) / memSampCnt;
        sMem = avgSqMem - avgMem * avgMem;
        maxMem = Math.max(maxMem,mem);
    }
    public void appendCpuSamp(double cpu){
        cpuSampCnt++;
        avgCpu += (cpu - avgCpu) / cpuSampCnt;
        avgSqCpu += (cpu * cpu - avgSqCpu) / cpuSampCnt;
        sCpu = avgSqCpu - avgCpu * avgCpu;
        maxCpu = Math.max(maxCpu,cpu);
    }
}
