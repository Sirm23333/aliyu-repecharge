package com.aliyun.mini.scheduler.core.impl_0802.monitor;

import com.aliyun.mini.scheduler.core.impl_0802.global.GlobalInfo;
import com.aliyun.mini.scheduler.core.impl_0802.model.ContainerInfo;
import com.aliyun.mini.scheduler.core.impl_0802.model.FunctionStatistics;
import com.aliyun.mini.scheduler.core.impl_0802.model.NodeInfo;
import com.aliyun.mini.scheduler.core.impl_0802.node_container_manager.CreateContainerThread;
import com.aliyun.mini.scheduler.core.impl_0802.node_container_manager.NodeContainerManagerContants;
import lombok.AllArgsConstructor;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
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
//        System.out.println("ContainerUpdateThread start...");
    }

    @Override
    public void run() {
        while(true){
            try {
                ContainerUpdateWork work = containerUpdateWorks.take();
                int flag = work.flag;
                FunctionStatistics functionStatistics = work.functionStatistics;
                if (flag == NodeContainerManagerContants.FUNCTION_TYPE_PARA){
                    // 提高函数的并行度
//                    System.out.println("[TO_UPDATE_CONTAINER1]"+functionStatistics);
                    int containerNum = GlobalInfo.containerIdMap.get(functionStatistics.getFunctionName()).size();
                    double cpuUse = functionStatistics.getMaxCpu();
                    double memUse = functionStatistics.getMaxMem();
                    int para = Math.min((int)(functionStatistics.getMemoryInBytes() * 0.8 / memUse ), (int)(functionStatistics.getVCPU() * 100 * 0.8 / cpuUse));
                    if(para > 1){
                        functionStatistics.setParallelism(para);
                        // 要保留的container数量
                        int retainContainerNum = containerNum / para + 1;
                        List<ContainerInfo> containerInfoList = new ArrayList<>();
                        for(String containerId : GlobalInfo.containerIdMap.get(functionStatistics.getFunctionName())){
                            containerInfoList.add(GlobalInfo.containerInfoMap.get(containerId));
                        }
                        containerInfoList.sort((containerInfo1,containerInfo2)->{
                            int cnt1 = getNodeParaContainerNum(containerInfo1);
                            int cnt2 = getNodeParaContainerNum(containerInfo2);
                            return cnt1 - cnt2;
                        });

                        int cnt = 0;
                        for(ContainerInfo containerInfo : containerInfoList){
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
                                    }else if(!containerInfo.isDeleted()){
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
//                        System.out.println("[UPDATE_CONTAINER]"+"para="+para+","+"retain="+retainContainerNum+",delete="+(containerNum - retainContainerNum));
                    }
                }else if(flag == NodeContainerManagerContants.FUNCTION_TYPE_CPU){
//                    System.out.println("[TO_UPDATE_CONTAINER2]"+functionStatistics);
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
                                            }else if(!containerInfo.isDeleted()){
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
                }else if(flag == NodeContainerManagerContants.FUNCTION_TYPE_MEM){
                    // 是内存密集型函数，尽量分开
                } else if(flag == NodeContainerManagerContants.FUNCTION_TYPE_OTHER){
                    // 是其他类型，先不动
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    // 得到container所在node的可并行的container的数量
    private int getNodeParaContainerNum(ContainerInfo containerInfo){
        int cnt = 0;
        NodeInfo nodeInfo = GlobalInfo.nodeInfoMap.get(containerInfo.getNodeId());
        for(ContainerInfo container : nodeInfo.getContainerInfoMap().values()){
            if(GlobalInfo.functionStatisticsMap.get(container.getFunctionName()).getFunctionType() == NodeContainerManagerContants.FUNCTION_TYPE_PARA){
                cnt++;
            }
        }
        return cnt;
    }

    @AllArgsConstructor
    public static class ContainerUpdateWork{
        // 0:可并行，需要调整并行上限
        // 1:cpu密集型函数，把container数量控制在和node数量相等,每个node上创建一个container
        // 2:是内存密集型函数，尽量分开
        int flag;
        FunctionStatistics functionStatistics;
    }

}
