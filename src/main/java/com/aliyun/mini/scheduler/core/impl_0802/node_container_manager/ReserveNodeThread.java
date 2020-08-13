package com.aliyun.mini.scheduler.core.impl_0802.node_container_manager;
import com.aliyun.mini.nodeservice.client.NodeServiceClient;
import com.aliyun.mini.resourcemanager.client.ResourceManagerClient;
import com.aliyun.mini.scheduler.core.impl_0802.global.GlobalInfo;
import com.aliyun.mini.scheduler.core.impl_0802.model.NodeInfo;
import com.aliyun.mini.scheduler.core.impl_0802.model.NodeStatus;
import com.aliyun.mini.scheduler.core.impl_0802.model.RequestInfo;
import com.aliyun.mini.scheduler.core.impl_0802.monitor.MonitorConstants;
import com.aliyun.mini.scheduler.core.impl_0802.monitor.NodeMonitorThread;
import com.java.mini.faas.ana.dto.NewNodeDTO;
import com.java.mini.faas.ana.dto.ReadyToReserveNodeDTO;
import com.java.mini.faas.ana.dto.ReserveNodeErrorDTO;
import com.java.mini.faas.ana.log.LogWriter;
import lombok.extern.slf4j.Slf4j;
import resourcemanagerproto.ResourceManagerOuterClass.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 创建node直到创建成功
 * 1.系统运行时，由NodeApplyThread启动
 */
@Slf4j
public class ReserveNodeThread implements Runnable {

    private ResourceManagerClient resourceManager = GlobalInfo.resourceManagerClient;

    private RequestInfo requestInfo;

    LogWriter logWriter = LogWriter.getInstance();


    public ReserveNodeThread build(RequestInfo requestInfo){
        this.requestInfo = requestInfo;
        return this;
    }
    @Override
    public void run() {
        NodeInfo newNodeInfo;
        ReserveNodeReply reserveNodeReply;
        try {
//            logWriter.readyToReserveNode(new ReadyToReserveNodeDTO(requestInfo.getRequestId()));
            reserveNodeReply = resourceManager.reserveNode(ReserveNodeRequest.newBuilder().build());
            NodeServiceClient nodeServiceClient = NodeServiceClient.New(reserveNodeReply.getNode().getAddress() + ":" + reserveNodeReply.getNode().getNodeServicePort());
            newNodeInfo = new NodeInfo(reserveNodeReply.getNode().getId(),
                    reserveNodeReply.getNode().getAddress(),
                    reserveNodeReply.getNode().getNodeServicePort(),
                    (long) (reserveNodeReply.getNode().getMemoryInBytes() * 2),
                    reserveNodeReply.getNode().getMemoryInBytes() * 0.67 / (1024 * 1024 * 1024),
                    nodeServiceClient,
                    new ConcurrentHashMap<>(),
                    new ConcurrentHashMap<>(),
                    new AtomicInteger(0),
                    false);
            GlobalInfo.nodeInfoMap.put(newNodeInfo.getNodeId(), newNodeInfo);
            NodeStatus nodeStatus = new NodeStatus(newNodeInfo.getNodeId(),nodeServiceClient);
            GlobalInfo.nodeStatusMap.put(nodeStatus.getNodeId(), nodeStatus);
            // 加入监控
            NodeMonitorThread.addNode(nodeStatus);
//            logWriter.newNodeInfo(new NewNodeDTO(requestInfo.getRequestId(), newNodeInfo.getNodeId(), newNodeInfo.getAddress(), newNodeInfo.getPort()));
        } catch (Exception e) {
            // 创建失败了
//            logWriter.reserveNodeError(new ReserveNodeErrorDTO(requestInfo.getRequestId(), e));
            try {
                Thread.sleep(1000);
                run();
                return;
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
        try {
            GlobalInfo.reserveNodeThreadQueue.put(this);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
