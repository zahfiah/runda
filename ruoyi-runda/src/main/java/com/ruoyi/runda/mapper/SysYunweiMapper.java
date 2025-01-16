package com.ruoyi.runda.mapper;

import java.util.List;
import com.ruoyi.runda.domain.SysYunwei;

/**
 * 运维日志Mapper接口
 * 
 * @author runda
 * @date 2025-01-11
 */
public interface SysYunweiMapper 
{
    /**
     * 查询运维日志
     * 
     * @param id 运维日志主键
     * @return 运维日志
     */
    public SysYunwei selectSysYunweiById(Long id);

    /**
     * 查询运维日志列表
     * 
     * @param sysYunwei 运维日志
     * @return 运维日志集合
     */
    public List<SysYunwei> selectSysYunweiList(SysYunwei sysYunwei);

    /**
     * 新增运维日志
     * 
     * @param sysYunwei 运维日志
     * @return 结果
     */
    public int insertSysYunwei(SysYunwei sysYunwei);

    /**
     * 修改运维日志
     * 
     * @param sysYunwei 运维日志
     * @return 结果
     */
    public int updateSysYunwei(SysYunwei sysYunwei);

    /**
     * 删除运维日志
     * 
     * @param id 运维日志主键
     * @return 结果
     */
    public int deleteSysYunweiById(Long id);

    /**
     * 批量删除运维日志
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteSysYunweiByIds(Long[] ids);
}
