package com.aliyun.mini.scheduler.core.impl_0722;
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
@Slf4j
public class SchedulerImp_0722 extends SchedulerImplBase {

    private ResourceManagerClient resourceManager = ResourceManagerClient.New();
    // nodeId -> nodeInfo
    private Map<String, NodeInfo> nodeMap = new ConcurrentHashMap<>();
    // functionName -> containerMap , containerMap is containerId -> containerInfo
    private Map<String,Map<String, ContainerInfo>> functionMap = new ConcurrentHashMap<>();
    // requestId -> functionName
    private Map<String,String> requestMap = new ConcurrentHashMap<>();

    @Override
    public void acquireContainer(AcquireContainerRequest request,
                                 StreamObserver<AcquireContainerReply> responseObserver) {
        String requestId = request.getRequestId();
        String functionName = request.getFunctionName();
        String handler = request.getFunctionConfig().getHandler();
        long memoryInBytes = request.getFunctionConfig().getMemoryInBytes();
        long timeoutInMs = request.getFunctionConfig().getTimeoutInMs();
        System.out.println("New RequestId("+requestId + "):functionName="+functionName + ";memoryInBytes="+memoryInBytes + ";timeoutInMs="+timeoutInMs);
        log.info("New RequestId("+requestId + "):functionName="+functionName + ";memoryInBytes="+memoryInBytes + ";timeoutInMs="+timeoutInMs);
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
                if(tmpContainer.getRequestSet().size() < 1){
                    tmpContainer.getRequestSet().add(requestId);
                    selectedContainer = tmpContainer;
                    break;
                }
            }
        }
        // 现存在的container中没有空闲的，选一个node创建container
        if(Objects.isNull(selectedContainer)){
            NodeInfo selectedNodeInfo = null;
            for(Map.Entry entry : nodeMap.entrySet()){
                selectedNodeInfo = (NodeInfo)entry.getValue();
                synchronized (selectedNodeInfo){
                    if(selectedNodeInfo.getAvailableMemInBytes() > memoryInBytes){
                        selectedNodeInfo.setAvailableMemInBytes(selectedNodeInfo.getAvailableMemInBytes() - memoryInBytes);
                        break;
                    }
                }
            }
            // 现存的node无法创建container，直接申请一个新的node
            if(Objects.isNull(selectedNodeInfo)){
                ReserveNodeReply reserveNodeReply = resourceManager.reserveNode(ReserveNodeRequest.newBuilder()
                        .setAccountId(request.getAccountId()).build());
                NodeServiceClient nodeServiceClient = NodeServiceClient.New(reserveNodeReply.getNode().getAddress()+":"+reserveNodeReply.getNode().getNodeServicePort());
                selectedNodeInfo = new NodeInfo(
                        reserveNodeReply.getNode().getId(),
                        reserveNodeReply.getNode().getAddress(),
                        reserveNodeReply.getNode().getNodeServicePort(),
                        reserveNodeReply.getNode().getMemoryInBytes() - memoryInBytes,
                        nodeServiceClient);
                System.out.println("ReserveNode for RequestId("+requestId+"):nodeId="+selectedNodeInfo.getNodeId()+";address="+selectedNodeInfo.getAddress()+":"+selectedNodeInfo.getPort()+";availableMem="+selectedNodeInfo.getAvailableMemInBytes());
                log.info("ReserveNode for RequestId("+requestId+"):nodeId="+selectedNodeInfo.getNodeId()+";address="+selectedNodeInfo.getAddress()+":"+selectedNodeInfo.getPort()+";availableMem="+selectedNodeInfo.getAvailableMemInBytes());
                nodeMap.put(selectedNodeInfo.getNodeId(),selectedNodeInfo);
            }
            // 在选定的node上创建container
            CreateContainerReply newContainerReply = selectedNodeInfo.getClient().createContainer(
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
            HashSet<String> requestSet = new HashSet<>();
            requestSet.add(requestId);
            selectedContainer = new ContainerInfo(newContainerReply.getContainerId(),selectedNodeInfo.getAddress(),selectedNodeInfo.getPort(),selectedNodeInfo.getNodeId(),requestSet);
            functionMap.get(functionName).put(selectedContainer.getId(),selectedContainer);
        }
        System.out.println("AcquireContainer for RequestId("+requestId+"):functionName="+functionName+";nodeId="+selectedContainer.getNodeId()+";address="+selectedContainer.getAddress()+":"+selectedContainer.getPort()+";containerId="+selectedContainer.getId());
        log.info("AcquireContainer for RequestId("+requestId+"):functionName="+functionName+";nodeId="+selectedContainer.getNodeId()+";address="+selectedContainer.getAddress()+":"+selectedContainer.getPort()+";containerId="+selectedContainer.getId());
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
        System.out.println("ReturnContainer from RequestId("+request.getRequestId()+"):containerId="+containerInfo.getId()+";nodeId="+containerInfo.getNodeId());
        log.info("ReturnContainer from RequestId("+request.getRequestId()+"):containerId="+containerInfo.getId()+";nodeId="+containerInfo.getNodeId());
        synchronized (containerInfo){
            containerInfo.getRequestSet().remove(request.getRequestId());
        }
        responseObserver.onNext(null);
        responseObserver.onCompleted();
    }
}