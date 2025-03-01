package com.ruoyi.runda.mapper;

import java.util.List;
import com.ruoyi.runda.domain.HourlyAverageAirData;
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


}
