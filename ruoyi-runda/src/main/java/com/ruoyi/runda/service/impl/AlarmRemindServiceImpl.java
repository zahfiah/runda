package com.ruoyi.runda.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.runda.mapper.AlarmRemindMapper;
import com.ruoyi.runda.domain.AlarmRemind;
import com.ruoyi.runda.service.IAlarmRemindService;

/**
 * 告警设置Service业务层处理
 * 
 * @author runda
 * @date 2025-01-07
 */
@Service
public class AlarmRemindServiceImpl implements IAlarmRemindService 
{
    @Autowired
    private AlarmRemindMapper alarmRemindMapper;

    /**
     * 查询告警设置
     * 
     * @param id 告警设置主键
     * @return 告警设置
     */
    @Override
    public AlarmRemind selectAlarmRemindById(Long id)
    {
        return alarmRemindMapper.selectAlarmRemindById(id);
    }

    /**
     * 查询告警设置列表
     * 
     * @param alarmRemind 告警设置
     * @return 告警设置
     */
    @Override
    public List<AlarmRemind> selectAlarmRemindList(AlarmRemind alarmRemind)
    {
        return alarmRemindMapper.selectAlarmRemindList(alarmRemind);
    }

    /**
     * 新增告警设置
     * 
     * @param alarmRemind 告警设置
     * @return 结果
     */
    @Override
    public int insertAlarmRemind(AlarmRemind alarmRemind)
    {
        return alarmRemindMapper.insertAlarmRemind(alarmRemind);
    }

    /**
     * 修改告警设置
     * 
     * @param alarmRemind 告警设置
     * @return 结果
     */
    @Override
    public int updateAlarmRemind(AlarmRemind alarmRemind)
    {
        return alarmRemindMapper.updateAlarmRemind(alarmRemind);
    }

    /**
     * 批量删除告警设置
     * 
     * @param ids 需要删除的告警设置主键
     * @return 结果
     */
    @Override
    public int deleteAlarmRemindByIds(Long[] ids)
    {
        return alarmRemindMapper.deleteAlarmRemindByIds(ids);
    }

    /**
     * 删除告警设置信息
     * 
     * @param id 告警设置主键
     * @return 结果
     */
    @Override
    public int deleteAlarmRemindById(Long id)
    {
        return alarmRemindMapper.deleteAlarmRemindById(id);
    }
}
