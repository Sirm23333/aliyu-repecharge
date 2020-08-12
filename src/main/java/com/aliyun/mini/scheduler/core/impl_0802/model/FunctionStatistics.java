package com.aliyun.mini.scheduler.core.impl_0802.model;
import com.aliyun.mini.scheduler.core.impl_0802.monitor.ContainerUpdateThread;
import lombok.Data;

/**
 * 对某个function的一些统计量
 */
@Data
public class FunctionStatistics {
    private String functionName;
    private long memoryInBytes;
    private double vCPU;
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

    // 延迟采样次数
    private int delayTimeSampCnt = 0;
    //最大延迟时间
    private long maxDelayTime = 0;
    //最小延迟时间
    private long minDelayTime = Long.MAX_VALUE;
    //平均延迟时间
    private long avgDelayTime = 0;
    // 延迟采样次数
    private int useTimeSampCnt = 0;
    //最大执行时间
    private long maxUseTime = 0;
    //最小执行时间
    private long minUseTime = Long.MAX_VALUE;
    //平均执行时间
    private long avgUseTime = 0;
    // 函数的可并行度
    private int parallelism = 1;
    // 是否已经提交了更改并行度的任务
    private boolean submitUpdateParaWork = false;
    // 是否已经提交了判断为计算密集型的任务
    private boolean submitUpdateCPUTypeWork = false;
    // 0为最优匹配原则，1为最先匹配原则
    private int choiceType = 0;
    private boolean isCpuIntensive = false;

    public FunctionStatistics(String functionName,long memoryInBytes){
        this.functionName = functionName;
        this.memoryInBytes = memoryInBytes;
        this.vCPU = memoryInBytes * 0.67 / (1024 * 1024 * 1024);
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
        if(!submitUpdateParaWork && cpuSampCnt > 500 && avgCpu < 0.1){
            submitUpdateParaWork = true;
            ContainerUpdateThread.submit(new ContainerUpdateThread.ContainerUpdateWork(1,this));
        }
        if(!submitUpdateCPUTypeWork && cpuSampCnt > 200 && avgCpu > 10){
            submitUpdateCPUTypeWork = true;
            isCpuIntensive = true;
            ContainerUpdateThread.submit(new ContainerUpdateThread.ContainerUpdateWork(2,this));
        }
    }
    public void appendDelaySamp(long time) {
        delayTimeSampCnt++;
        maxDelayTime = Math.max(maxDelayTime, time);
        minDelayTime = Math.min(minDelayTime, time);
        avgDelayTime += (time - avgDelayTime) / delayTimeSampCnt;
    }
    public void appendUseTime(long time) {
        useTimeSampCnt++;
        maxUseTime = Math.max(maxUseTime, time);
        minUseTime = Math.min(minUseTime, time);
        avgUseTime += (time - avgUseTime) / useTimeSampCnt;
        if((double)avgUseTime / 1000000 < 20 && choiceType == 0 && cpuSampCnt > 100 && avgCpu < 1){
            choiceType = 1;
        }
    }
}