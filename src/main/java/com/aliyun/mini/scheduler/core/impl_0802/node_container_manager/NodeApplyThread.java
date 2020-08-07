package com.aliyun.mini.scheduler.core.impl_0802.node_container_manager;

import com.aliyun.mini.scheduler.core.impl_0802.global.GlobalInfo;
import com.aliyun.mini.scheduler.core.impl_0802.model.FunctionStatistics;
import com.aliyun.mini.scheduler.core.impl_0802.model.RequestInfo;

/**
 * 该线程系统运行时启动，直接申请MAX个node直到成功
 */
public class NodeApplyThread implements Runnable {

    public static void start(){
        new Thread(new NodeApplyThread()).start();
        System.out.println("NodeApplyThread start...");
    }

    @Override
    public void run() {
        while(true){
            ReserveNodeThread reserveNodeThread = null;
            try {
                reserveNodeThread = GlobalInfo.reserveNodeThreadQueue.take();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if(GlobalInfo.nodeInfoMap.size() >= NodeContainerManagerContants.MAX_NODE_NUM){
                break;
            }
            // --日志需要--
            RequestInfo fakeRequest = new RequestInfo();
            fakeRequest.setRequestId("fake request");
            GlobalInfo.threadPool.execute(reserveNodeThread.build(fakeRequest));
        }
        try{
            for(int i = 0; i < 6; i++){
                try {
                    Thread.sleep(120000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                for(FunctionStatistics functionStatistics : GlobalInfo.functionStatisticsMap.values()){
                    System.out.println("[FUNCTION_INFO]"+functionStatistics);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
