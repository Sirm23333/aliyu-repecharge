package com.aliyun.mini.scheduler.core.impl_0802.forecast;

import com.aliyun.mini.scheduler.core.impl_0802.model.RequestRecord;

import java.util.List;

/**
 *
 */
public class ForecastService {

    // 根据一组请求记录，预测下一次请求来的时间和数量
    // requestRecord.functionName 函数名
    // requestRecord.time 时间戳，毫秒，long类型
    // requestRecord.num 请求数量
    // 预测以时间为主，如果达到时间上是周期性的，那就认为是周期性的，0.1s 0.2s 1s 0.5s 0.1s 0.2s 1s 0.5s ...这也算周期
    // 在时间上周期的前提下，请求的数量如果是周期的，可以预测最好，如果是随机的不能预测，就取个合适的统计量返回
    public static RequestRecord getNextRequest(List<RequestRecord> requestRecords){
        return null;
    }

}
