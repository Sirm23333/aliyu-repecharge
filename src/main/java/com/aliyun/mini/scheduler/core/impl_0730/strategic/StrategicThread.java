package com.aliyun.mini.scheduler.core.impl_0730.strategic;
import com.aliyun.mini.scheduler.core.impl_0730.global.GlobalInfo;
import com.aliyun.mini.scheduler.core.impl_0730.model.ContainerInfo;
import com.aliyun.mini.scheduler.core.impl_0730.model.RequestInfo;
import lombok.extern.slf4j.Slf4j;
import schedulerproto.SchedulerOuterClass;

import java.util.HashSet;
import java.util.Queue;
import java.util.Set;

/**
 *
 */
@Slf4j
public class StrategicThread implements Runnable{

    private String functionName;

    private Queue<RequestInfo> requestInfoQueue ;

    private Set<String> containerIds;



    public StrategicThread(String functionName , Queue<RequestInfo> requestInfoQueue){
        this.requestInfoQueue = requestInfoQueue;
        this.functionName = functionName;
        if(GlobalInfo.functionNameMap.get(functionName) == null){
            GlobalInfo.functionNameMap.put(functionName,new HashSet<>());
        }
        this.containerIds = GlobalInfo.functionNameMap.get(functionName);
    }

    public static void start(String functionName , Queue<RequestInfo> requestInfoQueue){
        new Thread(new StrategicThread(functionName,requestInfoQueue),"StrategicThread-"+functionName).start();
    }

    @Override
    public void run() {
        RequestInfo requestInfo = null;
        while(true){
            requestInfo = requestInfoQueue.peek();
            // 队列为空
            if(requestInfo == null){
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            for(String containerId : containerIds){
                ContainerInfo containerInfo = GlobalInfo.containerInfoMap.get(containerId);
                // 只根据并行上限指定container
                // 对同一函数已经串行化，暂时不用考虑同步问题
                if(containerInfo.getRequestSet().size() < containerInfo.getConcurrencyUpperLimit()){
                    containerInfo.getRequestSet().add(requestInfo.getRequestId());
                    log.info("thread-"+functionName + " choice "+ containerId);
                    // 下面的不知道会不会阻塞?
                    requestInfo.getResponseObserver().onNext(SchedulerOuterClass.AcquireContainerReply.newBuilder()
                            .setNodeId(containerInfo.getNodeId())
                            .setNodeAddress(containerInfo.getAddress())
                            .setNodeServicePort(containerInfo.getPort())
                            .setContainerId(containerInfo.getContainerId())
                            .build());
                    requestInfo.getResponseObserver().onCompleted();
                    requestInfoQueue.poll();
                    log.info("thread-"+functionName + " final "+ requestInfo.getRequestId());
                    break;
                }
            }

        }



    }

}
