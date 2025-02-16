package com.ruoyi.runda.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.runda.mapper.AlarmInfoMapper;
import com.ruoyi.runda.domain.AlarmInfo;
import com.ruoyi.runda.service.IAlarmInfoService;

/**
 * 短信记录Service业务层处理
 * 
 * @author runda
 * @date 2025-02-14
 */
@Service
public class AlarmInfoServiceImpl implements IAlarmInfoService 
{
    @Autowired
    private AlarmInfoMapper alarmInfoMapper;

    /**
     * 查询短信记录
     * 
     * @param id 短信记录主键
     * @return 短信记录
     */
    @Override
    public AlarmInfo selectAlarmInfoById(Long id)
    {
        return alarmInfoMapper.selectAlarmInfoById(id);
    }

    /**
     * 查询短信记录列表
     * 
     * @param alarmInfo 短信记录
     * @return 短信记录
     */
    @Override
    public List<AlarmInfo> selectAlarmInfoList(AlarmInfo alarmInfo)
    {
        return alarmInfoMapper.selectAlarmInfoList(alarmInfo);
    }

    /**
     * 新增短信记录
     * 
     * @param alarmInfo 短信记录
     * @return 结果
     */
    @Override
    public int insertAlarmInfo(AlarmInfo alarmInfo)
    {
        return alarmInfoMapper.insertAlarmInfo(alarmInfo);
    }

    /**
     * 修改短信记录
     * 
     * @param alarmInfo 短信记录
     * @return 结果
     */
    @Override
    public int updateAlarmInfo(AlarmInfo alarmInfo)
    {
        return alarmInfoMapper.updateAlarmInfo(alarmInfo);
    }

    /**
     * 批量删除短信记录
     * 
     * @param ids 需要删除的短信记录主键
     * @return 结果
     */
    @Override
    public int deleteAlarmInfoByIds(Long[] ids)
    {
        return alarmInfoMapper.deleteAlarmInfoByIds(ids);
    }

    /**
     * 删除短信记录信息
     * 
     * @param id 短信记录主键
     * @return 结果
     */
    @Override
    public int deleteAlarmInfoById(Long id)
    {
        return alarmInfoMapper.deleteAlarmInfoById(id);
    }
}
