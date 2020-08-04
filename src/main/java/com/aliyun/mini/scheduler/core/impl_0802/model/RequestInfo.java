package com.aliyun.mini.scheduler.core.impl_0802.model;

import io.grpc.stub.StreamObserver;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import schedulerproto.SchedulerOuterClass;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestInfo {
    private String accountId;
    private String requestId;
    private String functionName;
    private String functionHandler;
    private long memoryInBytes;
    private long timeOutInMs;
    private StreamObserver<SchedulerOuterClass.AcquireContainerReply> responseObserver;
    private long timeStamp;
    private AtomicBoolean end; // 是否已经处理结束
}
