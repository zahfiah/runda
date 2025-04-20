package com.ruoyi.runda.service;

import com.ruoyi.runda.domain.HourlyAverageAirData;

import java.util.List;

public interface DataPushService {
    /**
     * 获取小时平均空气数据
     * @return 小时平均空气数据列表
     */
    List<HourlyAverageAirData> getHourlyAverageAirData();
    /**
     * 批量推送空气数据到接口
     * @param dataList 空气数据列表
     * @return 推送是否成功
     */

    boolean batchPushAirData(List<HourlyAverageAirData> dataList);
}
