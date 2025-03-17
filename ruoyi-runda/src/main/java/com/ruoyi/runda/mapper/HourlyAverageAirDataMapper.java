package com.ruoyi.runda.mapper;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.runda.domain.HourlyAverageAirData;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 监测小时报表Mapper接口
 * 
 * @author runda
 * @date 2025-02-08
 */
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

    List<Map<String, Object>> calculateDailyHourlyAverage(
            @Param("deviceId") String deviceId,
            @Param("startDate") Date startDate,
            @Param("endDate") Date endDate
    );

}
