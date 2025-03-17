package com.ruoyi.runda.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.ruoyi.runda.domain.HourlyAverageAirData;
import org.apache.ibatis.annotations.Param;

/**
 * 监测小时报表Service接口
 * 
 * @author runda
 * @date 2025-02-08
 */
public interface IHourlyAverageAirDataService 
{
    /**
     * 查询监测小时报表
     * 
     * @param id 监测小时报表主键
     * @return 监测小时报表
     */
    public HourlyAverageAirData selectHourlyAverageAirDataById(Long id);

    /**
     * 查询监测小时报表列表
     * 
     * @param hourlyAverageAirData 监测小时报表
     * @return 监测小时报表集合
     */
    public List<HourlyAverageAirData> selectHourlyAverageAirDataList(HourlyAverageAirData hourlyAverageAirData);

    List<Map<String, Object>> calculateDailyHourlyAverage(
            @Param("deviceId") String deviceId,
            @Param("startDate") Date startDate,
            @Param("endDate") Date endDate
    );

}
