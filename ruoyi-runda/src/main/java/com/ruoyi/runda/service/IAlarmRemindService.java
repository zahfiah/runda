package com.ruoyi.runda.service;

import java.util.List;
import com.ruoyi.runda.domain.AlarmRemind;

/**
 * 告警设置Service接口
 * 
 * @author runda
 * @date 2025-01-07
 */
public interface IAlarmRemindService 
{
    /**
     * 查询告警设置
     * 
     * @param id 告警设置主键
     * @return 告警设置
     */
    public AlarmRemind selectAlarmRemindById(Long id);

    /**
     * 查询告警设置列表
     * 
     * @param alarmRemind 告警设置
     * @return 告警设置集合
     */
    public List<AlarmRemind> selectAlarmRemindList(AlarmRemind alarmRemind);

    /**
     * 新增告警设置
     * 
     * @param alarmRemind 告警设置
     * @return 结果
     */
    public int insertAlarmRemind(AlarmRemind alarmRemind);

    /**
     * 修改告警设置
     * 
     * @param alarmRemind 告警设置
     * @return 结果
     */
    public int updateAlarmRemind(AlarmRemind alarmRemind);

    /**
     * 批量删除告警设置
     * 
     * @param ids 需要删除的告警设置主键集合
     * @return 结果
     */
    public int deleteAlarmRemindByIds(Long[] ids);

    /**
     * 删除告警设置信息
     * 
     * @param id 告警设置主键
     * @return 结果
     */
    public int deleteAlarmRemindById(Long id);
}
