package com.aliyun.mini.scheduler.core.impl_0802.monitor;

import com.aliyun.mini.scheduler.core.impl_0802.global.GlobalInfo;
import com.aliyun.mini.scheduler.core.impl_0802.model.ContainerInfo;
import com.aliyun.mini.scheduler.core.impl_0802.model.FunctionStatistics;
import com.aliyun.mini.scheduler.core.impl_0802.node_container_manager.ContainerCleanThread;

import java.util.Map;

/**
 * 更改并行度，删除多余container
 */
public class ContainerUpdateThread implements Runnable {

    public static void start(){
        new Thread(new ContainerUpdateThread()).start();
        System.out.println("ContainerUpdateThread start...");
    }

    @Override
    public void run() {
        while (true){
            for(Map.Entry entry : GlobalInfo.functionStatisticsMap.entrySet()){
                FunctionStatistics functionStatistics = (FunctionStatistics) entry.getValue();
                if(functionStatistics.getCpuSampCnt() > 100 && functionStatistics.getAvgCpu() < 1 && (double)functionStatistics.getAvgUseTime() / 1000000 < 20){
                    functionStatistics.setChoiceType(1);
                }
//                if(functionStatistics.getCpuSampCnt() > 200 && functionStatistics.getAvgCpu() < 1 && functionStatistics.getParallelism() == 1){
                if(functionStatistics.getCpuSampCnt() > 500 && functionStatistics.getAvgCpu() < 0.1 && functionStatistics.getParallelism() == 1){
                    System.out.println("[TO_UPDATE_CONTAINER]"+functionStatistics);
                    int containerNum = GlobalInfo.containerIdMap.get(functionStatistics.getFunctionName()).size();
                    double cpuUse = functionStatistics.getMaxCpu();
                    double memUse = functionStatistics.getMaxMem();
                    int para = Math.min((int)(functionStatistics.getMemoryInBytes() * 0.8 / memUse ), (int)(functionStatistics.getVCPU() * 100 * 0.8 / cpuUse));
                    if(para > 1){
                        functionStatistics.setParallelism(para);
                        // 要保留的container数量
                        int retainContainerNum = containerNum / para + 1;
                        int cnt = 0;
                        for(String containerId : GlobalInfo.containerIdMap.get(functionStatistics.getFunctionName())){
                            ContainerInfo containerInfo = GlobalInfo.containerInfoMap.get(containerId);
                            if(cnt < retainContainerNum){
                                synchronized (containerInfo){
                                    containerInfo.setConcurrencyUpperLimit(para);
                                }
                            }else {
                                synchronized (containerInfo){
                                    if(!containerInfo.getRequestSet().isEmpty()){
                                        //  如果这个container正在执行，则标记为删除，在return的时候正式删除
                                        containerInfo.setDeleted(true);
                                    }else {
                                        containerInfo.setDeleted(true);
                                        try {
                                            GlobalInfo.threadPool.execute(GlobalInfo.removeContainerThreadQueue.take().build(containerInfo));
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            }
                            cnt++;
                        }
                        System.out.println("[UPDATE_CONTAINER]"+"para="+para+","+"retain="+retainContainerNum+",delete="+(containerNum - retainContainerNum));
                    }
                }
            }

            try {
                Thread.sleep(MonitorConstants.UPDATA_CONTAINER_CYC);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
