package com.aliyun.mini.scheduler.core.impl_0802.monitor;

import com.aliyun.mini.scheduler.core.impl_0802.global.GlobalInfo;
import com.aliyun.mini.scheduler.core.impl_0802.model.ContainerInfo;
import com.aliyun.mini.scheduler.core.impl_0802.model.FunctionStatistics;
import com.aliyun.mini.scheduler.core.impl_0802.model.NodeInfo;
import com.aliyun.mini.scheduler.core.impl_0802.node_container_manager.CreateContainerThread;
import lombok.AllArgsConstructor;

import java.util.ArrayList;
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
                    System.out.println("[TO_UPDATE_CONTAINER1]"+functionStatistics);
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
                }else if(flag == 2){
                    System.out.println("[TO_UPDATE_CONTAINER2]"+functionStatistics);
                    ContainerInfo modelContainerInfo = GlobalInfo.containerInfoMap.get(new ArrayList<>(GlobalInfo.containerIdMap.get(functionStatistics.getFunctionName())).get(0));
                    for(NodeInfo nodeInfo : GlobalInfo.nodeInfoMap.values()){
                        if(nodeInfo.getContainerNumMap().get(functionStatistics.getFunctionName()) != null
                            && nodeInfo.getContainerNumMap().get(functionStatistics.getFunctionName()) > 1){
                            // 把多余的container删除
                            int cnt = 0;
                            for(ContainerInfo containerInfo : nodeInfo.getContainerInfoMap().values()){
                                if(containerInfo.getFunctionName().equals(functionStatistics.getFunctionName())){
                                    if(cnt > 0){
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
                            }
                        }else if(nodeInfo.getContainerNumMap().get(functionStatistics.getFunctionName()) == null
                            || nodeInfo.getContainerNumMap().get(functionStatistics.getFunctionName()) == 0){
                            // 没有的创建一个
                            // 需要先拿到创建线程，如果暂时拿不到就阻塞在这
                            CreateContainerThread createContainerThread = GlobalInfo.createContainerThreadQueue.take();
                            GlobalInfo.threadPool.execute(createContainerThread.build(modelContainerInfo.getRequestInfo(),nodeInfo));
                        }
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
        // 2:是cpu密集型的函数，把container数量控制在和node数量相等,每个node上创建一个container
        int flag;
        FunctionStatistics functionStatistics;
    }

}
