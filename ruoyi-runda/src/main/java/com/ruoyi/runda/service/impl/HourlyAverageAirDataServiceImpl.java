package com.ruoyi.runda.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.runda.mapper.HourlyAverageAirDataMapper;
import com.ruoyi.runda.domain.HourlyAverageAirData;
import com.ruoyi.runda.service.IHourlyAverageAirDataService;

/**
 * 监测小时报表Service业务层处理
 * 
 * @author runda
 * @date 2025-02-08
 */
@Service
public class HourlyAverageAirDataServiceImpl implements IHourlyAverageAirDataService 
{
    @Autowired
    private HourlyAverageAirDataMapper hourlyAverageAirDataMapper;

    /**
     * 查询监测小时报表
     * 
     * @param id 监测小时报表主键
     * @return 监测小时报表
     */
    @Override
    public HourlyAverageAirData selectHourlyAverageAirDataById(Long id)
    {
        return hourlyAverageAirDataMapper.selectHourlyAverageAirDataById(id);
    }

    /**
     * 查询监测小时报表列表
     * 
     * @param hourlyAverageAirData 监测小时报表
     * @return 监测小时报表
     */
    @Override
    public List<HourlyAverageAirData> selectHourlyAverageAirDataList(HourlyAverageAirData hourlyAverageAirData)
    {
        return hourlyAverageAirDataMapper.selectHourlyAverageAirDataList(hourlyAverageAirData);
    }


}
