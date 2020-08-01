package com.aliyun.mini.scheduler.core.impl_0730.synstats;

import com.aliyun.mini.scheduler.core.impl_0730.model.NodeInfo;
import com.aliyun.mini.scheduler.core.impl_0730.model.NodeStatus;

public class SynStatsThread implements Runnable  {

    private NodeStatus nodeStats;

    private NodeInfo nodeInfo;

    private SynStatsRun synStatsRun;

    public SynStatsThread(NodeStatus nodeStats , NodeInfo nodeInfo){
        this.nodeStats = nodeStats;
        this.nodeInfo = nodeInfo;
    }

    @Override
    public void run() {

    }



}
