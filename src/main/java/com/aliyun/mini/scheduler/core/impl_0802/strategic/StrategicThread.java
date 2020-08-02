package com.aliyun.mini.scheduler.core.impl_0802.strategic;
import com.aliyun.mini.scheduler.core.impl_0802.global.GlobalInfo;
import com.aliyun.mini.scheduler.core.impl_0802.model.ContainerInfo;
import com.aliyun.mini.scheduler.core.impl_0802.model.RequestInfo;
import io.grpc.netty.shaded.io.netty.util.internal.ConcurrentSet;
import lombok.extern.slf4j.Slf4j;
import schedulerproto.SchedulerOuterClass;
import java.util.concurrent.LinkedBlockingQueue;

@Slf4j
public class StrategicThread implements Runnable{

    private String functionName;

    private LinkedBlockingQueue<RequestInfo> requestInfoQueue ;

    private ConcurrentSet<String> containerIds;

    private Object lock;



    public StrategicThread(String functionName , LinkedBlockingQueue<RequestInfo> requestInfoQueue){
        this.requestInfoQueue = requestInfoQueue;
        this.functionName = functionName;
        this.containerIds = GlobalInfo.containerIdMap.get(functionName);
        this.lock = GlobalInfo.lockMap.get(functionName);
    }

    public static void start(String functionName , LinkedBlockingQueue<RequestInfo> requestInfoQueue){
        new Thread(new StrategicThread(functionName,requestInfoQueue),"StrategicThread-"+functionName).start();
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
                        ContainerInfo selectContainer = getBestContainer(requestInfo);
                        if(selectContainer == null){
                            // wait
                            // 阻塞到有container可用，container来源：1.returnContainer 2.createContainer 3.提高了container的并发上限
                            lock.wait();
                        }else{
                            selectContainer.getRequestSet().add(requestInfo.getRequestId());
                            log.info("thread-"+functionName + " choice "+ selectContainer.getContainerId());
                            // 下面的不知道会不会阻塞?
                            requestInfo.getResponseObserver().onNext(SchedulerOuterClass.AcquireContainerReply.newBuilder()
                                    .setNodeId(selectContainer.getNodeId())
                                    .setNodeAddress(selectContainer.getAddress())
                                    .setNodeServicePort(selectContainer.getPort())
                                    .setContainerId(selectContainer.getContainerId())
                                    .build());
                            requestInfo.getResponseObserver().onCompleted();
                            log.info("thread-"+functionName + " final "+ requestInfo.getRequestId());
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
