package com.aliyun.mini.scheduler.core.impl_0802.node_container_manager;

import com.aliyun.mini.scheduler.core.impl_0802.global.GlobalInfo;
import com.aliyun.mini.scheduler.core.impl_0802.model.ContainerInfo;
import com.aliyun.mini.scheduler.core.impl_0802.model.NodeInfo;
import lombok.AllArgsConstructor;

import java.util.concurrent.LinkedBlockingQueue;

/**
 *
 */
public class ContainerMoveThread implements Runnable {

    //    private  LinkedBlockingQueue<MoveWork> moveWorks = new LinkedBlockingQueue<>();
    private MoveWork moveWork;

    //    public static void start(){
//        new Thread(new ContainerMoveThread()).start();
//    }
//    public  void submit(MoveWork moveWork){
//        try {
//            moveWorks.put(moveWork);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//    }
    public ContainerMoveThread build(MoveWork moveWork){
        this.moveWork = moveWork;
        return this;
    }
    @Override
    public void run() {
        try {
            System.out.println("[TRY_TO_MOVE]"+moveWork.containerInfo.getContainerId()+";"
                    +moveWork.fromNodeInfo.getNodeId()+";"+moveWork.toNodeInfo.getNodeId());
            ContainerInfo containerInfo = moveWork.containerInfo;
            NodeInfo from = moveWork.fromNodeInfo;
            NodeInfo to = moveWork.toNodeInfo;
            GlobalInfo.threadPool.submit(GlobalInfo.createContainerThreadQueue.take().build(containerInfo.getRequestInfo(),to)).get();
            synchronized (containerInfo){
                if(!containerInfo.getRequestSet().isEmpty()){
                    //  如果这个container正在执行，则标记为删除，在return的时候正式删除
                    containerInfo.setDeleted(true);
                }else {
                    containerInfo.setDeleted(true);
                    GlobalInfo.threadPool.execute(GlobalInfo.removeContainerThreadQueue.take().build(containerInfo));
                }
            }
            System.out.println("[CONTAINER_MOVE]"+moveWork.containerInfo.getContainerId()+";"
                    +moveWork.fromNodeInfo.getNodeId()+";"+moveWork.toNodeInfo.getNodeId());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                GlobalInfo.containerMoveThreadQueue.put(this);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    @AllArgsConstructor
    public static class MoveWork{
        ContainerInfo containerInfo;
        NodeInfo fromNodeInfo;
        NodeInfo toNodeInfo;
    }
}
