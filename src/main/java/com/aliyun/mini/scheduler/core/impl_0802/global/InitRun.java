package com.aliyun.mini.scheduler.core.impl_0802.global;

import com.aliyun.mini.resourcemanager.client.ResourceManagerClient;
import com.aliyun.mini.scheduler.core.impl_0802.node_container_manager.CreateContainerThread;
import com.aliyun.mini.scheduler.core.impl_0802.node_container_manager.NodeContainerManagerContants;
import com.aliyun.mini.scheduler.core.impl_0802.node_container_manager.ReserveNodeThread;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

/**
 *
 */
public class InitRun {

    static {
        GlobalInfo.resourceManagerClient = ResourceManagerClient.New();
        GlobalInfo.threadPool = Executors.newFixedThreadPool(64);
        GlobalInfo.createContainerThreadQueue = new LinkedBlockingQueue<>();
        for(int i = 0; i < NodeContainerManagerContants.CREATE_CONTAINER_CONCURRENT_UPPER;i++){
            try {
                GlobalInfo.createContainerThreadQueue.put(new CreateContainerThread());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        GlobalInfo.reserveNodeThreadQueue = new LinkedBlockingQueue<>();
        for(int i = 0; i < NodeContainerManagerContants.RESERVE_NODE_CONCURRENT_UPPER;i++){
            try {
                GlobalInfo.reserveNodeThreadQueue.put(new ReserveNodeThread());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
//        String path = "/aliyuncnpc/scheduler/log/application.log";
//        FileOutputStream puts = null;
//        try {
//            puts = new FileOutputStream(path,true);
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
//        PrintStream out = new PrintStream(puts);
//        System.setOut(out);
//        System.setErr(out);
    }

}
