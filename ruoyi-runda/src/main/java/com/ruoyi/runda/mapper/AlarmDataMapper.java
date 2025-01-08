package com.ruoyi.runda.mapper;

import java.util.List;
import com.ruoyi.runda.domain.AlarmData;

/**
 * 告警日志Mapper接口
 * 
 * @author runda
 * @date 2025-01-07
 */
public interface AlarmDataMapper 
{
    /**
     * 查询告警日志
     * 
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
     * 删除告警日志
     * 
     * @param id 告警日志主键
     * @return 结果
     */
    public int deleteAlarmDataById(Long id);

    /**
     * 批量删除告警日志
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteAlarmDataByIds(Long[] ids);
}
