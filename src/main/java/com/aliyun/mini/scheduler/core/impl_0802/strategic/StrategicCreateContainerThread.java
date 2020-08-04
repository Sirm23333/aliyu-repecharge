package com.aliyun.mini.scheduler.core.impl_0802.strategic;

import com.aliyun.mini.scheduler.core.impl_0802.global.GlobalInfo;
import com.aliyun.mini.scheduler.core.impl_0802.model.RequestInfo;
import com.aliyun.mini.scheduler.core.impl_0802.node_container_manager.CreateContainerThread;
import lombok.extern.slf4j.Slf4j;
import java.util.Calendar;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 伴随StrategicThread启动
 */
@Slf4j
public class StrategicCreateContainerThread implements Runnable {

    private LinkedBlockingQueue<RequestInfo> blockRequestInfoQueue;

    // requestInfoQueue仅用来查询，根据阻塞情况判断创建container，一定不要在这个线程去put、take
    private LinkedBlockingQueue<RequestInfo> requestInfoQueue ;


    public static void start(LinkedBlockingQueue<RequestInfo> requestInfoQueue  , LinkedBlockingQueue<RequestInfo> blockRequestInfoQueue){
       new Thread(new StrategicCreateContainerThread(requestInfoQueue , blockRequestInfoQueue)).start();
    }
    public StrategicCreateContainerThread(LinkedBlockingQueue<RequestInfo> requestInfoQueue  , LinkedBlockingQueue<RequestInfo> blockRequestInfoQueue){
        this.requestInfoQueue = requestInfoQueue;
        this.blockRequestInfoQueue = blockRequestInfoQueue;
    }

    @Override
    public void run() {

        while(true){
            try {
                RequestInfo blockRequestInfo = blockRequestInfoQueue.take();

//                log.info("[STRATEGIC_CREATE_CONTAINER]get request {}",blockRequestInfo);
                // do something
                if(blockRequestInfo.getEnd().get()) {
                    // 这个请求已经被消费了
//                    log.info("[GIVE_UP_CREATE_CONTAINER_1]{}",blockRequestInfo);
                    continue;
                }else if(GlobalInfo.containerIdMap.get(blockRequestInfo.getFunctionName()).isEmpty()){
                    // 如果这个function没有container，则马上创建1个container
//                    log.info("[TRY_TO_CREATE_CONTAINER_1]{}",blockRequestInfo);
                    CreateContainerThread createContainerThread = GlobalInfo.createContainerThreadQueue.take();
                    GlobalInfo.threadPool.execute(createContainerThread.build(blockRequestInfo));
                }else {
                    // 等待超过100ms就创建
                    long timeStamp = blockRequestInfo.getTimeStamp();
                    long now = Calendar.getInstance().getTimeInMillis();
                    if(now - timeStamp > StrategicContants.WAIT_TIME_UPPER && !blockRequestInfo.getEnd().get()){
//                        log.info("[TRY_TO_CREATE_CONTAINER_2]{}",blockRequestInfo);
                        CreateContainerThread createContainerThread = GlobalInfo.createContainerThreadQueue.take();
                        if(!blockRequestInfo.getEnd().get()){
                            GlobalInfo.threadPool.execute(createContainerThread.build(blockRequestInfo));
                        }else {
                            GlobalInfo.createContainerThreadQueue.put(createContainerThread);
                        }
                    }else{
                        if(now - timeStamp < StrategicContants.WAIT_TIME_UPPER){
//                            log.info("[SLEEP]{},{}",StrategicContants.WAIT_TIME_UPPER - (now - timeStamp),blockRequestInfo);
                            Thread.sleep(StrategicContants.WAIT_TIME_UPPER - (now - timeStamp));
                        }
                        if(!blockRequestInfo.getEnd().get()){
//                            log.info("[TRY_TO_CREATE_CONTAINER_3]{}",blockRequestInfo);
                            CreateContainerThread createContainerThread = GlobalInfo.createContainerThreadQueue.take();
                            if(!blockRequestInfo.getEnd().get()){
                                GlobalInfo.threadPool.execute(createContainerThread.build(blockRequestInfo));
                            }else {
                                GlobalInfo.createContainerThreadQueue.put(createContainerThread);
                            }
                        }else {
//                            log.info("[GIVE_UP_CREATE_CONTAINER_2]{}",blockRequestInfo);
                        }
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
