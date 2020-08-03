package com.aliyun.mini.scheduler.core.impl_0722;
import com.aliyun.mini.nodeservice.client.NodeServiceClient;
import com.aliyun.mini.resourcemanager.client.ResourceManagerClient;
import com.aliyun.mini.scheduler.core.impl_0722.model.ContainerInfo;
import com.aliyun.mini.scheduler.core.impl_0722.model.NodeInfo;
import com.aliyun.mini.scheduler.proto.SchedulerGrpc.*;
import com.aliyun.mini.scheduler.util.NumberUtil;
import com.java.mini.faas.ana.dto.*;
import com.java.mini.faas.ana.log.LogWriter;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import nodeservoceproto.NodeServiceOuterClass.*;
import resourcemanagerproto.ResourceManagerOuterClass.*;
import schedulerproto.SchedulerOuterClass.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class SchedulerImp_0722 extends SchedulerImplBase {

    private LogWriter logWriter = LogWriter.getInstance();

    private ResourceManagerClient resourceManager = ResourceManagerClient.New();
    // nodeId -> nodeInfo
    private Map<String, NodeInfo> nodeMap = new ConcurrentHashMap<>();
    // functionName -> containerMap , containerMap is containerId -> containerInfo
    private Map<String,Map<String, ContainerInfo>> functionMap = new ConcurrentHashMap<>();
    // requestId -> functionName
    private Map<String,String> requestMap = new ConcurrentHashMap<>();

    private boolean started = false;

    private static Map<String,Object> functionNameLockMap = new HashMap<>();
    private static Map<String,Integer> functionNameContainerNumMap = new HashMap<>();


    @Override
    public void acquireContainer(AcquireContainerRequest request,
                                 StreamObserver<AcquireContainerReply> responseObserver) {
        String requestId = request.getRequestId();
        String functionName = request.getFunctionName();
        String handler = request.getFunctionConfig().getHandler();
        long memoryInBytes = request.getFunctionConfig().getMemoryInBytes();
        long timeoutInMs = request.getFunctionConfig().getTimeoutInMs();




        logWriter.newRequestInfo(new NewRequestDTO(requestId,functionName,memoryInBytes,timeoutInMs));
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
                if(containerMap.containsKey(tmpContainer.getId()) && tmpContainer.getRequestSet().size() < tmpContainer.getReqLimit()){
                    tmpContainer.getRequestSet().add(requestId);
                    selectedContainer = tmpContainer;
                    break ;
                }
            }
        }

        // 现存在的container中没有空闲的，选一个node创建container
        if(Objects.isNull(selectedContainer)){

//            Object obj = functionNameLockMap.get(functionName);
//            if(obj == null){
//                synchronized (functionNameLockMap){
//                    obj = functionNameLockMap.get(functionName);
//                    if(obj == null){
//                        functionNameLockMap.put(functionName,new Object());
//                        functionNameContainerNumMap.put(functionName,0);
//                        obj = functionNameLockMap.get(functionName);
//                    }
//                }
//            }
//            boolean create = false;
//            synchronized (obj){
//                if(functionNameContainerNumMap.get(functionName) < 10){
//                    functionNameContainerNumMap.put(functionName,functionNameContainerNumMap.get(functionName) + 1);
//                    create = true;
//                }
//            }
//            if(!create){
//                try {
//                    Thread.sleep(100);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                acquireContainer(request,responseObserver);
//                return;
//            }
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

                    logWriter.readyToReserveNode(new ReadyToReserveNodeDTO(requestId));
                    reserveNodeReply = resourceManager.reserveNode(ReserveNodeRequest.newBuilder()
                            .setAccountId(request.getAccountId()).build());
                }catch (Exception e){
                    logWriter.reserveNodeError(new ReserveNodeErrorDTO(requestId,e));
                    System.out.println("try again..");
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                    // 创建失败再尝试
                    acquireContainer(request,responseObserver);
                    return ;
                }
                NodeServiceClient nodeServiceClient = NodeServiceClient.New(reserveNodeReply.getNode().getAddress()+":"+reserveNodeReply.getNode().getNodeServicePort());
                selectedNodeInfo = new NodeInfo(
                        reserveNodeReply.getNode().getId(),
                        reserveNodeReply.getNode().getAddress(),
                        reserveNodeReply.getNode().getNodeServicePort(),
                        reserveNodeReply.getNode().getMemoryInBytes() - memoryInBytes,
                        nodeServiceClient,new HashSet<>());

                logWriter.newNodeInfo(new NewNodeDTO(requestId,selectedNodeInfo.getNodeId(),selectedNodeInfo.getAddress(),selectedNodeInfo.getPort()));
                nodeMap.put(selectedNodeInfo.getNodeId(),selectedNodeInfo);
            }
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
                logWriter.createContainerError(new CreateContainerErrorDTO(requestId,e));
                responseObserver.onNext(null);
                responseObserver.onCompleted();
                return;
            }
            HashSet<String> requestSet = new HashSet<>();
            requestSet.add(requestId);
            selectedContainer = new ContainerInfo(newContainerReply.getContainerId(),selectedNodeInfo.getAddress(),selectedNodeInfo.getPort(),selectedNodeInfo.getNodeId(),requestSet,request.getFunctionConfig().getMemoryInBytes(),1);
//            if(request.getFunctionName().contains("6") || request.getFunctionName().contains("7") || request.getFunctionName().contains("8") || request.getFunctionName().contains("9")){
//                selectedContainer.setReqLimit(1);
//            }
            logWriter.newContainerInfo(new NewContainerDTO(requestId,selectedNodeInfo.getNodeId(),selectedContainer.getId()));

            functionMap.get(functionName).put(selectedContainer.getId(),selectedContainer);
            selectedNodeInfo.getContainerSet().add(selectedContainer.getId());
        }
        GetStatsReply stats = nodeMap.get(selectedContainer.getNodeId()).getClient().getStats(null);
        ContainerStats containerStats = null;
        for(ContainerStats tmp : stats.getContainerStatsListList()){
            if(tmp.getContainerId().equals(selectedContainer.getId())){
                containerStats = tmp;
            }
        }
        try{
            logWriter.selectedContainerInfo(new SelectedContainerDTO(requestId,selectedContainer.getId()));
            logWriter.nodeStatsInfo(new NodeStatsDTO(selectedContainer.getNodeId(),stats.getNodeStats().getTotalMemoryInBytes(),stats.getNodeStats().getMemoryUsageInBytes(),stats.getNodeStats().getAvailableMemoryInBytes(),stats.getNodeStats().getCpuUsagePct()));
            logWriter.containerStatsInfo(new ContainerStatsDTO(selectedContainer.getId(),containerStats.getTotalMemoryInBytes(),containerStats.getMemoryUsageInBytes(),containerStats.getCpuUsagePct(),selectedContainer.getRequestSet().size()));

        }catch (Exception e){
            e.printStackTrace();
        }
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

        logWriter.containerRunInfo(new ContainerRunDTO(request.getRequestId(),request.getDurationInNanos(),request.getMaxMemoryUsageInBytes(),request.getErrorCode(),request.getErrorMessage()));

        synchronized (containerInfo){
            containerInfo.getRequestSet().remove(request.getRequestId());
            if(request.getErrorCode() != null && !request.getErrorCode().equals("") && nodeInfo.getContainerSet().contains(containerInfo.getId())){
                // 有错误 删除容器
                nodeInfo.getClient().removeContainer(RemoveContainerRequest.newBuilder().setContainerId(containerInfo.getId()).build());
                nodeInfo.setAvailableMemInBytes(nodeInfo.getAvailableMemInBytes() + containerInfo.getMemoryInBytes());
                nodeInfo.getContainerSet().remove(containerInfo.getId());
                functionMap.get(requestMap.get(request.getRequestId())).remove(request.getContainerId());
                logWriter.removeContainerInfo(new RemoveContainerDTO(containerInfo.getId()));
            }
        }
        requestMap.remove(request.getRequestId());
        responseObserver.onNext(null);
        responseObserver.onCompleted();
    }
}