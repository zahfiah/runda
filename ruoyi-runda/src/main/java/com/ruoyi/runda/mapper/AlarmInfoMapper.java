package com.ruoyi.runda.mapper;

import java.util.List;
import com.ruoyi.runda.domain.AlarmInfo;

/**
 * 短信记录Mapper接口
 * 
 * @author runda
 * @date 2025-01-07
 */
public interface AlarmInfoMapper 
{
    /**
     * 查询短信记录
     * 
     * @param id 短信记录主键
     * @return 短信记录
     */
    public AlarmInfo selectAlarmInfoById(Long id);

    /**
     * 查询短信记录列表
     * 
     * @param alarmInfo 短信记录
     * @return 短信记录集合
     */
    public List<AlarmInfo> selectAlarmInfoList(AlarmInfo alarmInfo);

    /**
     * 新增短信记录
     * 
     * @param alarmInfo 短信记录
     * @return 结果
     */
    public int insertAlarmInfo(AlarmInfo alarmInfo);

    /**
     * 修改短信记录
     * 
     * @param alarmInfo 短信记录
     * @return 结果
     */
    public int updateAlarmInfo(AlarmInfo alarmInfo);

    /**
     * 删除短信记录
     * 
     * @param id 短信记录主键
     * @return 结果
     */
    public int deleteAlarmInfoById(Long id);

    /**
     * 批量删除短信记录
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteAlarmInfoByIds(Long[] ids);
}
