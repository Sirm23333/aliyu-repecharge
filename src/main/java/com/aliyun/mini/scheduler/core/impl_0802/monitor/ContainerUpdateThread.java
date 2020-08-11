package com.aliyun.mini.scheduler.core.impl_0802.monitor;

import com.aliyun.mini.scheduler.core.impl_0802.global.GlobalInfo;
import com.aliyun.mini.scheduler.core.impl_0802.model.ContainerInfo;
import com.aliyun.mini.scheduler.core.impl_0802.model.FunctionStatistics;
import lombok.AllArgsConstructor;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 更改并行度，删除多余container
 */
public class ContainerUpdateThread implements Runnable {

    private static LinkedBlockingQueue<ContainerUpdateWork> containerUpdateWorks = new LinkedBlockingQueue<>();

    public static void submit(ContainerUpdateWork work){
        try {
            containerUpdateWorks.put(work);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    public static void start(){
        new Thread(new ContainerUpdateThread()).start();
        System.out.println("ContainerUpdateThread start...");
    }

    @Override
    public void run() {

        while(true){
            try {
                ContainerUpdateWork work = containerUpdateWorks.take();
                int flag = work.flag;
                FunctionStatistics functionStatistics = work.functionStatistics;
                if (flag == 1){
                    // 提高函数的并行度
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
                                    containerInfo.getChoiceTmpCnt().set(containerInfo.getRequestSet().size());
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
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    @AllArgsConstructor
    public static class ContainerUpdateWork{
        // 1:可并行，需要调整并行上限
        int flag;
        FunctionStatistics functionStatistics;
    }
}
