package com.aliyun.mini.scheduler.core.impl_0802.strategic;
import com.aliyun.mini.scheduler.core.impl_0802.global.GlobalInfo;
import com.aliyun.mini.scheduler.core.impl_0802.model.ContainerInfo;
import com.aliyun.mini.scheduler.core.impl_0802.model.RequestInfo;
import com.java.mini.faas.ana.dto.SelectedContainerDTO;
import com.java.mini.faas.ana.log.LogWriter;
import io.grpc.netty.shaded.io.netty.util.internal.ConcurrentSet;
import lombok.extern.slf4j.Slf4j;
import schedulerproto.SchedulerOuterClass;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/*
 *  在acquireContainer中启动
 */
@Slf4j
public class StrategicThread implements Runnable{

    LogWriter logWriter = LogWriter.getInstance();

    private String functionName;

    private LinkedBlockingQueue<RequestInfo> requestInfoQueue ;

    private ConcurrentSet<String> containerIds;

     // functionLock
    private Object lock;

    // 如果requestInfoQueue队头无法马上被消费，则放入blockRequestInfoQueue，
    // 让StrategicCreateContainerThread拿到去分析是否需要创建新的container
    private LinkedBlockingQueue<RequestInfo> blockRequestInfoQueue;



    public StrategicThread(String functionName , LinkedBlockingQueue<RequestInfo> requestInfoQueue){
        this.requestInfoQueue = requestInfoQueue;
        this.functionName = functionName;
        this.containerIds = GlobalInfo.containerIdMap.get(functionName);
        this.lock = GlobalInfo.functionLockMap.get(functionName);
        this.blockRequestInfoQueue = new LinkedBlockingQueue<>();
    }

    public static void start(String functionName , LinkedBlockingQueue<RequestInfo> requestInfoQueue){
        StrategicThread strategicThread = new StrategicThread(functionName,requestInfoQueue);
        new Thread((strategicThread),"StrategicThread-"+functionName).start();
        StrategicCreateContainerThread.start(strategicThread.requestInfoQueue,strategicThread.blockRequestInfoQueue);
//        log.info("StrategicThread start..."+"StrategicThread-"+functionName);
        System.out.println("StrategicThread start..."+"StrategicThread-"+functionName);
    }

    @Override
    public void run() {
        RequestInfo requestInfo = null;
        while(true){
            try {
                // 阻塞直到拿到元素
                requestInfo = requestInfoQueue.take();
                synchronized (lock){
                    // 选择一个最好的container
                    while(true){
                        ContainerInfo selectedContainer = getBestContainer(requestInfo);
                        // 没有可以用的container
                        if(selectedContainer == null){
                            blockRequestInfoQueue.put(requestInfo);
                            // wait
                            // 阻塞到有container可用，container来源：1.returnContainer 2.createContainer 3.提高了container的并发上限
//                            log.info("[WAIT]{}",requestInfo);
                            lock.wait();
                        }else{
                            requestInfo.getEnd().set(true);
                            selectedContainer.getRequestSet().add(requestInfo.getRequestId());
                            requestInfo.getResponseObserver().onNext(SchedulerOuterClass.AcquireContainerReply.newBuilder()
                                    .setNodeId(selectedContainer.getNodeId())
                                    .setNodeAddress(selectedContainer.getAddress())
                                    .setNodeServicePort(selectedContainer.getPort())
                                    .setContainerId(selectedContainer.getContainerId())
                                    .build());
                            requestInfo.getResponseObserver().onCompleted();
//                            log.info("[REQUEST_FINAL]{},{}",requestInfo,selectedContainer.getContainerId());
                            logWriter.selectedContainerInfo(new SelectedContainerDTO(requestInfo.getRequestId(),selectedContainer.getContainerId()));
                            break;
                        }
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private ContainerInfo getBestContainer(RequestInfo requestInfo){

        ContainerInfo selectContainer = null,tmpContainer;
        for(String containerId : containerIds){
            tmpContainer = GlobalInfo.containerInfoMap.get(containerId);
            // 只根据并行上限指定container
            // 对同一函数已经串行化，暂时不用考虑同步问题
            if(tmpContainer.getRequestSet().size() < tmpContainer.getConcurrencyUpperLimit()){
                selectContainer = tmpContainer;
                break;
            }
        }
        return selectContainer;
    }

}
