package com.ruoyi.runda.mapper;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.runda.domain.HourlyAverageAirData;
import com.ruoyi.runda.domain.HourlyAverageAirDataCopy;
import org.apache.ibatis.annotations.*;

/**
 * 监测小时报表Mapper接口
 * 
 * @author runda
 * @date 2025-02-08
 */
@Mapper
public interface HourlyAverageAirDataMapper 
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

    /**
     * 新增监测小时报表
     *
     * @param hourlyAverageAirData 监测小时报表
     * @return 结果
     */
    public int insertHourlyAverageAirData(HourlyAverageAirData hourlyAverageAirData);

    //计算24小时平均值

    /**
     * 获取设备在指定时间前最近的24条有效记录（不要求连续时间）
     * @param deviceId 设备ID
     * @param endTime 结束时间
     * @return 包含PM2.5和PM10数据的记录列表
     */
    @Select("SELECT average_pm2_5 as averagePm25, average_pm10 as averagePm10 " +
            "FROM hourly_average_air_data " +
            "WHERE device_id = #{deviceId} AND created_at <= #{endTime} " +
            "AND (average_pm2_5 IS NOT NULL OR average_pm10 IS NOT NULL) " +
            "ORDER BY created_at DESC " +
            "LIMIT 24")
    List<Map<String, Object>> getLatest24ValidRecords(
            @Param("deviceId") String deviceId,
            @Param("endTime") Date endTime);
    @MapKey("hour")
    List<Map<String, Object>> calculateDailyHourlyAverage(@Param("deviceId")String deviceId,
                                                          @Param("startDate")  Date startDate,
                                                          @Param("endDate")Date endDate);

    List<HourlyAverageAirDataCopy> selectHourlyAverageAirDataByDate(@Param("dateTimeStr")String dateTimeStr);

//    HourlyAverageAirData selectBeforeHour(@Param("deviceId")String deviceId,@Param("formattedTime")String formattedTime );
@Results({
        @Result(column = "average_pm2_5", property = "averagePm25"),
        @Result(column = "average_pm10", property = "averagePm10")
})
@Select("SELECT * FROM hourly_average_air_data WHERE device_id = #{deviceId} AND created_at = #{formattedTime}")
HourlyAverageAirData selectBeforeHour(@Param("deviceId") String deviceId,
                                      @Param("formattedTime") String formattedTime);
}
