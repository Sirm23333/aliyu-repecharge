package com.aliyun.mini.scheduler.core.impl_0802.node_container_manager;
import com.aliyun.mini.scheduler.core.impl_0802.global.GlobalInfo;
import com.aliyun.mini.scheduler.core.impl_0802.model.NodeInfo;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * node空间的碎片整理
 */
public class NodeFragmentOrderThread implements Runnable {

    private LinkedBlockingQueue<Object> orderRequest = new LinkedBlockingQueue();

    public void submit(Object obj){
        try {
            orderRequest.put(obj);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while (true){
            try {
                orderRequest.take();
                Thread.sleep(100);
                long totalMem = 0;
                long useMem = 0;
                totalMem = GlobalInfo.nodeInfoMap.size() * NodeContainerManagerContants.NODE_MEMORY;
                List<NodeInfo> nodeInfoList = new ArrayList<>(GlobalInfo.nodeInfoMap.values());
                nodeInfoList.sort((node1,node2)->{
                    if(node1.getAvailableMemInBytes() > node2.getAvailableMemInBytes()){
                        return -1;
                    }else if(node1.getAvailableMemInBytes() < node2.getAvailableMemInBytes()){
                        return 1;
                    }else {
                        return 0;
                    }
                });
                for(NodeInfo nodeInfo : nodeInfoList){
                    useMem += (NodeContainerManagerContants.NODE_MEMORY - nodeInfo.getAvailableMemInBytes());
                }
                // 保证每个node留出20%的可用空间



            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }

    }


}
