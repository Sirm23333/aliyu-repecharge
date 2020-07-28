package com.aliyun.mini.scheduler.core.impl_0722;
import com.aliyun.mini.client.APIClient;
import com.aliyun.mini.nodeservice.client.NodeServiceClient;
import com.aliyun.mini.resourcemanager.client.ResourceManagerClient;
import com.aliyun.mini.scheduler.core.impl_0722.model.ContainerInfo;
import com.aliyun.mini.scheduler.core.impl_0722.model.NodeInfo;
import com.aliyun.mini.scheduler.proto.SchedulerGrpc.*;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import nodeservoceproto.NodeServiceOuterClass.*;
import resourcemanagerproto.ResourceManagerOuterClass.*;
import schedulerproto.SchedulerOuterClass.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Logger;

@Slf4j
public class SchedulerImp_0722 extends SchedulerImplBase {


    private ResourceManagerClient resourceManager = ResourceManagerClient.New();
    // nodeId -> nodeInfo
    private Map<String, NodeInfo> nodeMap = new ConcurrentHashMap<>();
    // functionName -> containerMap , containerMap is containerId -> containerInfo
    private Map<String,Map<String, ContainerInfo>> functionMap = new ConcurrentHashMap<>();
    // requestId -> functionName
    private Map<String,String> requestMap = new ConcurrentHashMap<>();

    private final long GB2Bytes = 1024 * 1024 * 1024 ;

    private final double GBUseVCPU = 0.67;

    private final Lock lock = new ReentrantLock();

    @Override
    public void acquireContainer(AcquireContainerRequest request,
                                 StreamObserver<AcquireContainerReply> responseObserver) {
        String requestId = request.getRequestId();
        String functionName = request.getFunctionName();
        String handler = request.getFunctionConfig().getHandler();
        long memoryInBytes = request.getFunctionConfig().getMemoryInBytes();
        long timeoutInMs = request.getFunctionConfig().getTimeoutInMs();
//        System.out.println("New RequestId("+requestId + "):functionName="+functionName + ";memoryInBytes="+memoryInBytes + ";timeoutInMs="+timeoutInMs);
//        log.info("New RequestId("+requestId + "):functionName="+functionName + ";memoryInBytes="+memoryInBytes + ";timeoutInMs="+timeoutInMs);
        requestMap.put(requestId,functionName);
        ContainerInfo selectedContainer = null , tmpContainer;
        Map<String, ContainerInfo> containerMap = functionMap.get(functionName);
        if(Objects.isNull(containerMap)){
            containerMap = new ConcurrentHashMap<>();
            functionMap.put(functionName,containerMap);
        }
        for(Map.Entry entry : containerMap.entrySet()){
            tmpContainer = (ContainerInfo) entry.getValue();
            synchronized (tmpContainer){
                if(tmpContainer.getRequestSet().size() < 5){
                    tmpContainer.getRequestSet().add(requestId);
                    selectedContainer = tmpContainer;
                    break;
                }
            }
        }
        // 现存在的container中没有空闲的，选一个node创建container
        if(Objects.isNull(selectedContainer)){
            NodeInfo selectedNodeInfo = null , tmpNodeInfo;
            for(Map.Entry entry : nodeMap.entrySet()){
                tmpNodeInfo = (NodeInfo)entry.getValue();
                synchronized (tmpNodeInfo){
                    if(tmpNodeInfo.getAvailableMemInBytes() > memoryInBytes){
                        selectedNodeInfo = tmpNodeInfo;
                        selectedNodeInfo.setAvailableMemInBytes(selectedNodeInfo.getAvailableMemInBytes() - memoryInBytes);
                        break;
                    }
                }
            }
            // 现存的node无法创建container，直接申请一个新的node
            if(Objects.isNull(selectedNodeInfo)){
                ReserveNodeReply reserveNodeReply = null;
                try{
                    reserveNodeReply = resourceManager.reserveNode(ReserveNodeRequest.newBuilder()
                            .setAccountId(request.getAccountId()).build());
                }catch (Exception e){
                    System.out.println("ReserveNode fail.."+e.getStackTrace());
                    log.info("ReserveNode fail.."+e.getStackTrace());
                    responseObserver.onNext(null);
                    responseObserver.onCompleted();
                    return ;
                }
                NodeServiceClient nodeServiceClient = NodeServiceClient.New(reserveNodeReply.getNode().getAddress()+":"+reserveNodeReply.getNode().getNodeServicePort());
                selectedNodeInfo = new NodeInfo(
                        reserveNodeReply.getNode().getId(),
                        reserveNodeReply.getNode().getAddress(),
                        reserveNodeReply.getNode().getNodeServicePort(),
                        reserveNodeReply.getNode().getMemoryInBytes() - memoryInBytes,
                        nodeServiceClient);
                System.out.println("ReserveNode for RequestId("+requestId+"),functionNme="+functionName+",selectedNode="+selectedNodeInfo);
                log.info("ReserveNode for RequestId("+requestId+"),functionNme="+functionName+",selectedNode="+selectedNodeInfo);
                nodeMap.put(selectedNodeInfo.getNodeId(),selectedNodeInfo);
            }
//            log.info("Get node to createContainer for request "+requestId + " "+functionName+":selectedNode="+selectedNodeInfo+";nodeStats="+selectedNodeInfo.getClient().getStats(null));
//            System.out.println("Get node to createContainer for request "+requestId + " "+functionName+":selectedNode="+selectedNodeInfo+";nodeStats="+selectedNodeInfo.getClient().getStats(null));
            // 在选定的node上创建container
            CreateContainerReply newContainerReply = null;
            try{
                newContainerReply = selectedNodeInfo.getClient().createContainer(
                        CreateContainerRequest.newBuilder()
                                .setRequestId(requestId)
                                .setName(functionName + UUID.randomUUID())
                                .setFunctionMeta(FunctionMeta.newBuilder()
                                        .setMemoryInBytes(memoryInBytes)
                                        .setFunctionName(functionName)
                                        .setTimeoutInMs(timeoutInMs)
                                        .setHandler(handler)
                                        .build())
                                .build()
                );
            }catch (Exception e){
                System.out.println(e.getStackTrace());
                System.out.println("Error node:"+selectedNodeInfo);
                log.info("{}",e.getStackTrace());
                log.info("Error node:"+selectedNodeInfo);
                responseObserver.onNext(null);
                responseObserver.onCompleted();
                return;
            }
            System.out.println("CreateContainer "+functionName+" " + newContainerReply.getContainerId() +" on nodeId=" + selectedNodeInfo.getNodeId());
            log.info("CreateContainer "+functionName+" " + newContainerReply.getContainerId() +" on nodeId=" + selectedNodeInfo.getNodeId());
            HashSet<String> requestSet = new HashSet<>();
            requestSet.add(requestId);
            selectedContainer = new ContainerInfo(newContainerReply.getContainerId(),selectedNodeInfo.getAddress(),selectedNodeInfo.getPort(),selectedNodeInfo.getNodeId(),requestSet,request.getFunctionConfig().getMemoryInBytes());
            functionMap.get(functionName).put(selectedContainer.getId(),selectedContainer);
        }
        GetStatsReply stats = nodeMap.get(selectedContainer.getNodeId()).getClient().getStats(null);
        ContainerStats containerStats = null;
        for(ContainerStats tmp : stats.getContainerStatsListList()){
            if(tmp.getContainerId().equals(selectedContainer.getId())){
                containerStats = tmp;
            }
        }
        System.out.println("AcquireContainer for RequestId("+requestId+"):functionName="+functionName+"selectedContainer="+selectedContainer + ";nodeStats="+ stats.getNodeStats() + ";containerStats" + containerStats +"Request using num="+(selectedContainer.getRequestSet().size()-1));
        log.info("AcquireContainer for RequestId("+requestId+"):functionName="+functionName+"selectedContainer="+selectedContainer + ";nodeStats="+ stats.getNodeStats() + ";containerStats" + containerStats+"Request using num="+(selectedContainer.getRequestSet().size()-1));
        AcquireContainerReply acquireContainerReply = AcquireContainerReply.newBuilder()
                .setNodeId(selectedContainer.getNodeId())
                .setNodeAddress(selectedContainer.getAddress())
                .setNodeServicePort(selectedContainer.getPort())
                .setContainerId(selectedContainer.getId())
                .build();
        responseObserver.onNext(acquireContainerReply);
        responseObserver.onCompleted();
    }

    @Override
    public void returnContainer(ReturnContainerRequest request,
                                StreamObserver<ReturnContainerReply> responseObserver) {
        ContainerInfo containerInfo = functionMap.get(requestMap.get(request.getRequestId())).get(request.getContainerId());
        NodeInfo nodeInfo = nodeMap.get(containerInfo.getNodeId());
        System.out.println("ReturnContainer from RequestId("+request.getRequestId()+"):container="+containerInfo+";request="+request);
        log.info("ReturnContainer from RequestId("+request.getRequestId()+"):container="+containerInfo+";request="+request);
        synchronized (containerInfo){
            containerInfo.getRequestSet().remove(request.getRequestId());
            if(request.getErrorCode() != null && !request.getErrorCode().equals("")){
                // 有错误 删除容器
                nodeInfo.getClient().removeContainer(RemoveContainerRequest.newBuilder().setContainerId(containerInfo.getId()).build());
                nodeInfo.setAvailableMemInBytes(nodeInfo.getAvailableMemInBytes() + containerInfo.getMemoryInBytes());
                functionMap.get(requestMap.get(request.getRequestId())).remove(containerInfo);
            }
        }
        requestMap.remove(request.getRequestId());
        responseObserver.onNext(null);
        responseObserver.onCompleted();
    }
}