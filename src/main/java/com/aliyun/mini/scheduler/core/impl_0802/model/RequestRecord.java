package com.aliyun.mini.scheduler.core.impl_0802.model;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 *
 */
@Data
@AllArgsConstructor
public class RequestRecord {
    String functionName;
    long time;
    int num;
}
