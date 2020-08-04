package com.aliyun.mini.scheduler.core.impl_0802.node_container_manager;
import com.aliyun.mini.nodeservice.client.NodeServiceClient;
import com.aliyun.mini.resourcemanager.client.ResourceManagerClient;
import com.aliyun.mini.scheduler.core.impl_0802.global.GlobalInfo;
import com.aliyun.mini.scheduler.core.impl_0802.model.NodeInfo;
import com.aliyun.mini.scheduler.core.impl_0802.model.NodeStatus;
import com.aliyun.mini.scheduler.core.impl_0802.model.RequestInfo;
import com.java.mini.faas.ana.dto.NewNodeDTO;
import com.java.mini.faas.ana.dto.ReadyToReserveNodeDTO;
import com.java.mini.faas.ana.dto.ReserveNodeErrorDTO;
import com.java.mini.faas.ana.log.LogWriter;
import lombok.extern.slf4j.Slf4j;
import resourcemanagerproto.ResourceManagerOuterClass.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 创建node 有可能创建失败 要在createContainer中判断是否重新创建
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
        synchronized (GlobalInfo.nodeLock) {
            try {
                logWriter.readyToReserveNode(new ReadyToReserveNodeDTO(requestInfo.getRequestId()));
                reserveNodeReply = resourceManager.reserveNode(ReserveNodeRequest.newBuilder().build());
                NodeServiceClient nodeServiceClient = NodeServiceClient.New(reserveNodeReply.getNode().getAddress() + ":" + reserveNodeReply.getNode().getNodeServicePort());
                newNodeInfo = new NodeInfo(reserveNodeReply.getNode().getId(),
                        reserveNodeReply.getNode().getAddress(),
                        reserveNodeReply.getNode().getNodeServicePort(),
                        reserveNodeReply.getNode().getMemoryInBytes(),
                        reserveNodeReply.getNode().getMemoryInBytes() * 0.67 / (1024 * 1024 * 1024),
                        nodeServiceClient,
                        new ConcurrentHashMap<>());
                GlobalInfo.nodeInfoMap.put(newNodeInfo.getNodeId(), newNodeInfo);
                NodeStatus nodeStatus = new NodeStatus(newNodeInfo.getNodeId());
                GlobalInfo.nodeStatusMap.put(nodeStatus.getNodeId(), nodeStatus);
//                log.info("[NEW_NODE]{},node cnt={}", newNodeInfo, GlobalInfo.nodeInfoMap.size());
                logWriter.newNodeInfo(new NewNodeDTO(requestInfo.getRequestId(), newNodeInfo.getNodeId(), newNodeInfo.getAddress(), newNodeInfo.getPort()));
            } catch (Exception e) {
                // 创建失败了
                logWriter.reserveNodeError(new ReserveNodeErrorDTO(requestInfo.getRequestId(), e));
//                log.info("[RESERVE_NODE_FAIL]{}", requestInfo);
                try {
//                    log.info("[FAIL_SLEEP]{}",requestInfo);
                    Thread.sleep(500);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }finally {
                GlobalInfo.nodeLock.notifyAll();
            }
        }
        try {
            GlobalInfo.reserveNodeThreadQueue.put(this);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
