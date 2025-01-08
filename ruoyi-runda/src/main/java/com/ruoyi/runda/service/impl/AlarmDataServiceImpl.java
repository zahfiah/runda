package com.ruoyi.runda.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.runda.mapper.AlarmDataMapper;
import com.ruoyi.runda.domain.AlarmData;
import com.ruoyi.runda.service.IAlarmDataService;

/**
 * 告警日志Service业务层处理
 * 
 * @author runda
 * @date 2025-01-07
 */
@Service
public class AlarmDataServiceImpl implements IAlarmDataService 
{
    @Autowired
    private AlarmDataMapper alarmDataMapper;

    /**
     * 查询告警日志
     * 
     * @param id 告警日志主键
     * @return 告警日志
     */
    @Override
    public AlarmData selectAlarmDataById(Long id)
    {
        return alarmDataMapper.selectAlarmDataById(id);
    }

    /**
     * 查询告警日志列表
     * 
     * @param alarmData 告警日志
     * @return 告警日志
     */
    @Override
    public List<AlarmData> selectAlarmDataList(AlarmData alarmData)
    {
        return alarmDataMapper.selectAlarmDataList(alarmData);
    }

    /**
     * 新增告警日志
     * 
     * @param alarmData 告警日志
     * @return 结果
     */
    @Override
    public int insertAlarmData(AlarmData alarmData)
    {
        return alarmDataMapper.insertAlarmData(alarmData);
    }

    /**
     * 修改告警日志
     * 
     * @param alarmData 告警日志
     * @return 结果
     */
    @Override
    public int updateAlarmData(AlarmData alarmData)
    {
        return alarmDataMapper.updateAlarmData(alarmData);
    }

    /**
     * 批量删除告警日志
     * 
     * @param ids 需要删除的告警日志主键
     * @return 结果
     */
    @Override
    public int deleteAlarmDataByIds(Long[] ids)
    {
        return alarmDataMapper.deleteAlarmDataByIds(ids);
    }

    /**
     * 删除告警日志信息
     * 
     * @param id 告警日志主键
     * @return 结果
     */
    @Override
    public int deleteAlarmDataById(Long id)
    {
        return alarmDataMapper.deleteAlarmDataById(id);
    }
}
