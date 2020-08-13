package com.aliyun.mini.scheduler.core.impl_0730.nodemanager;

import com.aliyun.mini.nodeservice.client.NodeServiceClient;
import com.aliyun.mini.resourcemanager.client.ResourceManagerClient;
import com.aliyun.mini.scheduler.core.impl_0730.global.GlobalInfo;
import com.aliyun.mini.scheduler.core.impl_0730.model.*;
import com.java.mini.faas.ana.dto.ReserveNodeErrorDTO;
import com.java.mini.faas.ana.log.LogWriter;
import lombok.extern.slf4j.Slf4j;
import nodeservoceproto.NodeServiceOuterClass.*;
import resourcemanagerproto.ResourceManagerOuterClass.*;

import java.util.*;

/**
 *
 */
@Slf4j
public class NodeContainerManagerThread implements Runnable{

    LogWriter logWriter = LogWriter.getInstance();

    private ResourceManagerClient resourceManager;

    // 记录为哪些requestId创建了container
    private Set<String> createContainerRequestSet;

    public NodeContainerManagerThread(){
        resourceManager = ResourceManagerClient.New();
        createContainerRequestSet = new HashSet<>();
    }

    public static void start(){
        new Thread(new NodeContainerManagerThread()).start();
    }
    @Override
    public void run() {
        RequestInfo peekRequestInfo;
        while(true){
            log.info("123");
            if(GlobalInfo.requestQueueMap.isEmpty()){
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            for(Queue<RequestInfo> requestQueue : GlobalInfo.requestQueueMap.values()){
                peekRequestInfo = requestQueue.peek();
                log.info("Get request " + peekRequestInfo);
                if(peekRequestInfo != null){
                    // 等待时间超过上限并且没有为这个request创建过container(防止创建了container还有来得及消费的情况)，申请container
                    if(Calendar.getInstance().getTimeInMillis() - peekRequestInfo.getTimeStamp() > Contants.WAIT_TIME_UPPER
                            && !createContainerRequestSet.contains(peekRequestInfo.getRequestId())){
                        NodeInfo selectedNode = null;
                        // 找一个内存够的node，单线程操作nodeInfo，暂时不用考虑同步问题
                        for(NodeInfo nodeInfo : GlobalInfo.nodeInfoMap.values()){
                            if(nodeInfo.getAvailableMemInBytes() > peekRequestInfo.getMemoryInBytes()){
                                selectedNode = nodeInfo;
                                break;
                            }
                        }
                        // 没有可以用的node，直接申请一个node
                        if(selectedNode == null){
                            ReserveNodeReply reserveNodeReply ;
                            try{
                                reserveNodeReply = resourceManager.reserveNode(ReserveNodeRequest.newBuilder()
                                        .setAccountId(peekRequestInfo.getAccountId()).build());
                            }catch (Exception e){
//                                logWriter.reserveNodeError(new ReserveNodeErrorDTO(peekRequestInfo.getRequestId(),e));
                                break;
                            }
                            log.info("ReserveNode-"+reserveNodeReply.getNode().getId());
                            NodeServiceClient nodeServiceClient = NodeServiceClient.New(reserveNodeReply.getNode().getAddress()+":"+reserveNodeReply.getNode().getNodeServicePort());
                            selectedNode = new NodeInfo(reserveNodeReply.getNode().getId(),reserveNodeReply.getNode().getAddress(),
                                    reserveNodeReply.getNode().getNodeServicePort(),reserveNodeReply.getNode().getMemoryInBytes(),
                                    reserveNodeReply.getNode().getMemoryInBytes() * 0.67 / (1024 * 1024 * 1024),
                                    nodeServiceClient,new HashMap<>());
                            GlobalInfo.nodeInfoMap.put(selectedNode.getNodeId(),selectedNode);
                            NodeStatus nodeStatus = new NodeStatus(selectedNode.getNodeId());
                            GlobalInfo.nodeStatusMap.put(nodeStatus.getNodeId(),nodeStatus);
                        }
                        log.info("to createContainer..");
                        // 创建container
                        CreateContainerReply container = selectedNode.getClient().createContainer(
                                CreateContainerRequest.newBuilder()
                                        .setRequestId(peekRequestInfo.getRequestId())
                                        .setName(peekRequestInfo.getFunctionName()+ UUID.randomUUID())
                                        .setFunctionMeta(FunctionMeta.newBuilder()
                                                .setFunctionName(peekRequestInfo.getFunctionName())
                                                .setMemoryInBytes(peekRequestInfo.getMemoryInBytes())
                                                .setTimeoutInMs(peekRequestInfo.getTimeOutInMs())
                                                .setHandler(peekRequestInfo.getFunctionHandler()).build()).build());


                        ContainerInfo containerInfo = new ContainerInfo(container.getContainerId(),peekRequestInfo.getFunctionName(),selectedNode.getNodeId(),selectedNode.getAddress(),
                                selectedNode.getPort(),peekRequestInfo.getMemoryInBytes(),peekRequestInfo.getMemoryInBytes() * 0.67 / (1024 * 1024 * 1024),
                                GlobalInfo.functionConcurrencyMap.get(peekRequestInfo.getFunctionName()) == null ? 1 : GlobalInfo.functionConcurrencyMap.get(peekRequestInfo.getFunctionName()),
                                new HashSet<>());
                        log.info("CreateContainer-"+containerInfo.getContainerId() + " for " + peekRequestInfo.getFunctionName());
                        selectedNode.setAvailableMemInBytes(selectedNode.getAvailableMemInBytes() - peekRequestInfo.getMemoryInBytes());
                        log.info("aaaaaa");
                        selectedNode.setAvailableVCPU(selectedNode.getAvailableVCPU() - containerInfo.getVCPU());
                        log.info("bbbbbb");
                        GlobalInfo.containerInfoMap.put(containerInfo.getContainerId(),containerInfo);
                        log.info("ccccc");
                        ContainerStatus containerStatus = new ContainerStatus(containerInfo.getContainerId());
                        log.info("dddddd");
                        GlobalInfo.containerStatusMap.put(containerStatus.getContainerId(),containerStatus);
                        log.info("eeeeee");
                        GlobalInfo.nodeStatusMap.get(selectedNode.getNodeId()).getContainerStatusMap().put(containerStatus.getContainerId(),containerStatus);
                        GlobalInfo.nodeInfoMap.get(selectedNode.getNodeId()).getContainerInfoMap().put(containerInfo.getContainerId(),containerInfo);
                        log.info("fffffff");
                        createContainerRequestSet.add(peekRequestInfo.getRequestId());
                        log.info("ggggggg");
                        GlobalInfo.functionNameMap.get(containerInfo.getFunctionName()).add(containerInfo.getContainerId());
                        log.info("hhhhhhh");
                    }
                }
            }
            try {
                Thread.sleep(5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

}
