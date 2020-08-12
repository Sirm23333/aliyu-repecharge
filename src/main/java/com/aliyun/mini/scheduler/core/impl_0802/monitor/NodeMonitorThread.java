package com.aliyun.mini.scheduler.core.impl_0802.monitor;

import com.aliyun.mini.scheduler.core.impl_0802.model.NodeStatus;
import nodeservoceproto.NodeServiceOuterClass.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class NodeMonitorThread implements Runnable {

    // 需要监控的node列表
    private static List<NodeStatus> nodeStatusList= new ArrayList<>();

    private static NodeUpdateThread nodeUpdateThread = new NodeUpdateThread();

    public static void start(){
        new Thread(new NodeMonitorThread()).start();
        System.out.println("NodeMonitor start...");
        new Thread(nodeUpdateThread).start();
        System.out.println("nodeUpdateThread start...");
    }

    public static void addNode(NodeStatus nodeStatus){
        synchronized (nodeStatusList){
            nodeStatusList.add(nodeStatus);
        }
    }
    public static void removeNode(NodeStatus nodeStatus){
        synchronized (nodeStatusList){
            nodeStatusList.remove(nodeStatus);
        }
    }

    @Override
    public void run() {
        while (true){
            synchronized (nodeStatusList){
                for(NodeStatus nodeStatus : nodeStatusList){
                    GetStatsReply stats = nodeStatus.getNodeServiceClient().getStats(null);
                    nodeUpdateThread.submit(new NodeUpdateThread.NodeUpdateWork(stats,nodeStatus));
                }
            }
            try {
                Thread.sleep(MonitorConstants.SYN_NODE_STATS_CYC);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


}
