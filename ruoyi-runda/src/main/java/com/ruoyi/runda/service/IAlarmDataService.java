package com.ruoyi.runda.service;

import java.util.List;
import com.ruoyi.runda.domain.AlarmData;

/**
 * 告警日志Service接口
 * 
 * @author runda
 * @date 2025-01-07
 */
public interface IAlarmDataService 
{
    /**
     * 查询告警日志
     * 1
     * @param id 告警日志主键
     * @return 告警日志
     */
    public AlarmData selectAlarmDataById(Long id);

    /**
     * 查询告警日志列表
     * 
     * @param alarmData 告警日志
     * @return 告警日志集合
     */
    public List<AlarmData> selectAlarmDataList(AlarmData alarmData);

    /**
     * 新增告警日志
     * 
     * @param alarmData 告警日志
     * @return 结果
     */
    public int insertAlarmData(AlarmData alarmData);

    /**
     * 修改告警日志
     * 
     * @param alarmData 告警日志
     * @return 结果
     */
    public int updateAlarmData(AlarmData alarmData);

    /**
     * 批量删除告警日志
     * 
     * @param ids 需要删除的告警日志主键集合
     * @return 结果
     */
    public int deleteAlarmDataByIds(Long[] ids);

    /**
     * 删除告警日志信息
     * 
     * @param id 告警日志主键
     * @return 结果
     */
    public int deleteAlarmDataById(Long id);
}
