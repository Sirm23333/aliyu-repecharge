package com.aliyun.mini.scheduler.core.impl_0802.forecast;

import com.aliyun.mini.scheduler.core.impl_0802.model.RequestRecord;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 *
 */
public class ForecastService {


    private static long offValue = 40;

    // 根据一组请求记录，预测下一次请求来的时间和数量
    // requestRecord.functionName 函数名
    // requestRecord.time 时间戳，毫秒，long类型
    // requestRecord.num 请求数量
    // 预测以时间为主，如果达到时间上是周期性的，那就认为是周期性的，0.1s 0.2s 1s 0.5s 0.1s 0.2s 1s 0.5s ...这也算周期
    // 在时间上周期的前提下，请求的数量如果是周期的，可以预测最好，如果是随机的不能预测，就取个合适的统计量返回
    public static RequestRecord getNextRequest(List<RequestRecord> requestRecords) {
        long[] timeList = new long[requestRecords.size() - 1];
        for (int i = 1; i < requestRecords.size(); i++) {
            timeList[i - 1] = requestRecords.get(i).getTime() - requestRecords.get(i - 1).getTime();
        }
        List<Long> period = new ArrayList<>();
        int i = 0;
        boolean flag = true;
        for (; i < timeList.length / 2; i++) {
            period.add(timeList[i]);
            int j = i + 1;
            for (; j < timeList.length - 1; j++) {
                if (timeList[j] >= timeList[i] - offValue && timeList[j] <= timeList[i] + offValue) {
                    for (int a = 0; a < period.size() && flag; a++) {
                        long val = period.get(a);
                        for (int k = i + a; k < timeList.length - 1; k += period.size()) {
                            if (timeList[k] < val - offValue || timeList[k] > val + offValue) {
                                flag = false;
                                break;
                            }
                        }
                    }
                    if (flag) {
                        break;
                    } else {
                        flag = true;
                    }
                } else {
                    period.add(timeList[j]);
                }
            }
            if (j < timeList.length - 1) {
                break;
            } else {
                period.clear();
            }
        }
        if (period.size() * 2 > timeList.length - i) {
            return null;
        }
        int index = -1;
        for (int a = 0; a < period.size(); a++) {
            long ave = 0;
            int num = 0;
            int k = i + a;
            for (; k < timeList.length; k += period.size()) {
                ave += timeList[k];
                num++;
            }
            period.set(a, (long) ave / num);
            if (k == timeList.length) {
                index = a;
            }
        }
        if (index < 0) {
            return null;
        }
        int nextNum = 0, sum = 0;
        for (int b = requestRecords.size() - period.size(); b > i; b -= period.size()) {
            nextNum += requestRecords.get(b).getNum();
            sum++;
        }
        if (sum <= 0) {
            return null;
        }
        nextNum = (int) Math.ceil((double) nextNum / sum);
        return new RequestRecord(requestRecords.get(0).getFunctionName(), timeList[index] + requestRecords.get(requestRecords.size() - 1).getTime(), nextNum);
    }

    public static void main(String[] args) {
        Random random = new Random(2258);
        List<RequestRecord> requestRecords = new ArrayList<>();
        long deltaT = 0;
        for (int i = 0; i < 20; i++) {
            requestRecords.add(new RequestRecord("fun_1", deltaT, 9 + random.nextInt(2)));
            deltaT += 480 + random.nextInt(40);
            requestRecords.add(new RequestRecord("fun_1", deltaT, 4 + random.nextInt(2)));
            deltaT += 180 + random.nextInt(40);
            requestRecords.add(new RequestRecord("fun_1", deltaT, 18 + random.nextInt(2)));
            deltaT += 980 + random.nextInt(40);
        }
        requestRecords.add(new RequestRecord("fun_1", deltaT, 9 + random.nextInt(2)));
        RequestRecord res = getNextRequest(requestRecords);
        System.out.println(res.getTime());
        System.out.println(res.getNum());
    }
}