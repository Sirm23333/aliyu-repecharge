package com.aliyun.mini.scheduler.core.impl_0802.node_container_manager;
import com.aliyun.mini.scheduler.core.impl_0802.global.GlobalInfo;
import com.aliyun.mini.scheduler.core.impl_0802.model.ContainerInfo;
import com.aliyun.mini.scheduler.core.impl_0802.model.FunctionStatistics;
import com.aliyun.mini.scheduler.core.impl_0802.model.NodeInfo;
import java.util.*;

/**
 * node空间的碎片整理
 */
public class NodeFragmentOrderThread implements Runnable {


    public static void start(){
        new Thread(new NodeFragmentOrderThread()).start();
    }
    @Override
    public void run() {
        try {
            // sleep 5min
            Thread.sleep(NodeContainerManagerContants.NODE_ORDER_START_TIME);
            for(FunctionStatistics functionStatistics : GlobalInfo.functionStatisticsMap.values()){
                while(functionStatistics.getFunctionType() < 0){
                    // 还没有确定函数类型
                    Thread.sleep(1000);
                }
            }
            // 等待一段时间，让container处理完成
            Thread.sleep(5000);
            System.out.println("to start order");
            List<ContainerMoveThread.MoveWork> moveWorks = new ArrayList<>();
            long totalMem = 0; // 现存在的所有container需要的总内存，除了cpu密集型的
            int retainNodeNum ; // 需要保留的node数量
            int nodeNum = GlobalInfo.nodeInfoMap.size(); // 现有的node数量
            long cpuIntensiveMemByNode = 0; // 一个node中存放所有的cpu密集型的container需要的内存
            List<NodeInfo> toDelete = new ArrayList<>();
            for(FunctionStatistics functionStatistics : GlobalInfo.functionStatisticsMap.values()){
                if(functionStatistics.isCpuIntensive()){
                    cpuIntensiveMemByNode += functionStatistics.getRealMemoryInBytes();
                }
            }
            for(ContainerInfo containerInfo : GlobalInfo.containerInfoMap.values()){
                if(!GlobalInfo.functionStatisticsMap.get(containerInfo.getFunctionName()).isCpuIntensive()){
                    totalMem += containerInfo.getRealMemoryInBytes();
                }
            }
            retainNodeNum = (int) Math.max(Math.round(Math.ceil( (double) totalMem / (NodeContainerManagerContants.NODE_MEMORY - cpuIntensiveMemByNode))), NodeContainerManagerContants.MIN_NODE_NUM);
            System.out.println("retainNum "+retainNodeNum);
            // 所有node列表
            List<NodeInfo> nodeInfoList = new ArrayList<>(GlobalInfo.nodeInfoMap.values());
            // 将列表按container数量由多到少排序，即会保留container数量多的前retainNodeNum个node
            nodeInfoList.sort((node1,node2)->{
                return node2.getContainerInfoMap().size() - node1.getContainerInfoMap().size();
            });
            // 对应nodeInfoList顺序的nodeInfo中的containerInfo列表
            List<List<ContainerInfo>> nodeContainerListList = new ArrayList<>();
            for(NodeInfo nodeInfo : nodeInfoList){
                List<ContainerInfo> containerIdList = new ArrayList<>(nodeInfo.getContainerInfoMap().values());
                nodeContainerListList.add(containerIdList);
            }
            // retainNodeNum以后的node都要删除
            for(int i = retainNodeNum; i < nodeNum; i++){
                NodeInfo deleteNode = nodeInfoList.get(i);
                toDelete.add(deleteNode);
                deleteNode.setDeleted(true); // 不能再在这个node上创建container了
                for(ContainerInfo containerInfo : nodeContainerListList.get(i)){
                    // 不是cpu密集型的container迁移到前面
                    if(getType(containerInfo) != NodeContainerManagerContants.FUNCTION_TYPE_CPU){
                        int bestNodeIdx = getBestNodeMoveTo(containerInfo, nodeContainerListList, 0, retainNodeNum);
                        nodeContainerListList.get(bestNodeIdx).add(containerInfo);
                        moveWorks.add(new ContainerMoveThread.MoveWork(containerInfo,deleteNode,nodeInfoList.get(bestNodeIdx)));
                    }else {
                        // 是cpu密集型的直接删掉
                        synchronized (containerInfo){
                            if(!containerInfo.getRequestSet().isEmpty()){
                                //  如果这个container正在执行，则标记为删除，在return的时候正式删除
                                containerInfo.setDeleted(true);
                            }else {
                                containerInfo.setDeleted(true);
                                GlobalInfo.threadPool.execute(GlobalInfo.removeContainerThreadQueue.take().build(containerInfo));
                            }
                        }
                    }
                }
            }
            for(ContainerMoveThread.MoveWork moveWork : moveWorks){
                GlobalInfo.threadPool.execute(GlobalInfo.containerMoveThreadQueue.take().build(moveWork));
            }
            for(NodeInfo nodeInfo : toDelete){
                GlobalInfo.threadPool.execute(GlobalInfo.releaseNodeThreadQueue.take().build(nodeInfo));
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    // 在nodeContainerList[start]到nodeContainerList[end-1]中找一个最合适的node的索引返回
    // 同样的function的container尽量不放在一起，同是可并发的container尽量不放一起，内存密集型的尽量不放在一起
    private int getBestNodeMoveTo(ContainerInfo containerInfo,List<List<ContainerInfo>> nodeContainerListList,int start,int end){
        int minNum = Integer.MAX_VALUE;
        long maxAvailableMem = -1;
        int selectedIdx = 0;
        for(int i = start; i < end; i++){
            int sameAndParaContainerNum = getSameAndParaContainerNum(containerInfo, nodeContainerListList.get(i));
            long availableMem = getAvailableMem(nodeContainerListList.get(i));
            if(availableMem > containerInfo.getRealMemoryInBytes()){
                if(minNum > sameAndParaContainerNum){
                    minNum = sameAndParaContainerNum;
                    selectedIdx = i;
                    maxAvailableMem = availableMem;
                }else if(minNum == sameAndParaContainerNum){
                    if(availableMem > maxAvailableMem){
                        selectedIdx = i;
                        maxAvailableMem = availableMem;
                    }
                }
            }
        }
        return selectedIdx;
    }
    private long getAvailableMem(List<ContainerInfo> nodeContainerList){
        long mem = 0;
        for(ContainerInfo containerInfo : nodeContainerList){
            mem += containerInfo.getRealMemoryInBytes();
        }
        return mem;
    }
    // 获得nodeInfo中和containerInfo一样的container的数量
    private int getSameAndParaContainerNum(ContainerInfo containerInfo , List<ContainerInfo> nodeContainerList){
        int cnt = 0;
        for(ContainerInfo tmpContainerInfo : nodeContainerList){
            if(tmpContainerInfo.getFunctionName().equals(containerInfo.getFunctionName())){
                cnt += 4;
            }else if(GlobalInfo.functionStatisticsMap.get(tmpContainerInfo.getFunctionName()).getFunctionType()
                    == GlobalInfo.functionStatisticsMap.get(containerInfo.getFunctionName()).getFunctionType()){
                cnt++;
            }
        }
        return cnt;
    }

    private int getType(ContainerInfo containerInfo){
        return GlobalInfo.functionStatisticsMap.get(containerInfo.getFunctionName()).getFunctionType();
    }
    // 列出相同function的多余container
    private List<ContainerInfo> listSameContainer(List<ContainerInfo> containerInfoList){
        List<ContainerInfo> result = new ArrayList<>();
        Set<String> functionNameSet = new HashSet<>();
        for(ContainerInfo containerInfo : containerInfoList){
            if(functionNameSet.contains(containerInfo.getFunctionName())){
                result.add(containerInfo);
            }else {
                functionNameSet.add(containerInfo.getFunctionName());
            }
        }
        return result;
    }
    // 列出重复的可并行的container，保证剩余的只有一个可并行的container
    private List<ContainerInfo> listParallelizableContainer(List<ContainerInfo> containerInfoList){
        List<ContainerInfo> result = new ArrayList<>();
        boolean has = false;
        for(ContainerInfo containerInfo : containerInfoList){
            if(getType(containerInfo) == NodeContainerManagerContants.FUNCTION_TYPE_PARA){
                if(!has){
                    has = true;
                }else {
                    result.add(containerInfo);
                }
            }
        }
        return result;
    }
    private List<ContainerInfo> listMemoryContainer(List<ContainerInfo> containerInfoList){
        List<ContainerInfo> result = new ArrayList<>();
        boolean has = false;
        for(ContainerInfo containerInfo : containerInfoList){
            if(getType(containerInfo) == NodeContainerManagerContants.FUNCTION_TYPE_MEM){
                if(!has){
                    has = true;
                }else {
                    result.add(containerInfo);
                }
            }
        }
        return result;
    }

}
