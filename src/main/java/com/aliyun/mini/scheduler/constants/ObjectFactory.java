package com.aliyun.mini.scheduler.constants;

import com.aliyun.mini.resourcemanager.client.ResourceManagerClient;
import com.aliyun.mini.scheduler.core.impl_0722.SchedulerImp_0722;
import com.aliyun.mini.scheduler.core.impl_0730.SchedulerImp_0730;
import com.aliyun.mini.scheduler.core.impl_0802.SchedulerImp_0802;
import com.aliyun.mini.scheduler.core.impl_0802.global.InitRun;
import com.aliyun.mini.scheduler.proto.SchedulerGrpc.*;

/**
 *
 */
public class ObjectFactory {
    public static SchedulerImplBase SchedulerImp ;
    public static InitRun init;
    static {
        SchedulerImp = new SchedulerImp_0802();
        init = new InitRun();
    }
}
