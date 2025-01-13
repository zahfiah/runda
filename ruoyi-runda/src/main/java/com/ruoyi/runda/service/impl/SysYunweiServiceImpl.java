package com.ruoyi.runda.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.runda.mapper.SysYunweiMapper;
import com.ruoyi.runda.domain.SysYunwei;
import com.ruoyi.runda.service.ISysYunweiService;

/**
 * 运维日志Service业务层处理
 * 
 * @author runda
 * @date 2025-01-11
 */
@Service
public class SysYunweiServiceImpl implements ISysYunweiService 
{
    @Autowired
    private SysYunweiMapper sysYunweiMapper;

    /**
     * 查询运维日志
     * 
     * @param id 运维日志主键
     * @return 运维日志
     */
    @Override
    public SysYunwei selectSysYunweiById(Long id)
    {
        return sysYunweiMapper.selectSysYunweiById(id);
    }

    /**
     * 查询运维日志列表
     * 
     * @param sysYunwei 运维日志
     * @return 运维日志
     */
    @Override
    public List<SysYunwei> selectSysYunweiList(SysYunwei sysYunwei)
    {
        return sysYunweiMapper.selectSysYunweiList(sysYunwei);
    }

    /**
     * 新增运维日志
     * 
     * @param sysYunwei 运维日志
     * @return 结果
     */
    @Override
    public int insertSysYunwei(SysYunwei sysYunwei)
    {
        return sysYunweiMapper.insertSysYunwei(sysYunwei);
    }

    /**
     * 修改运维日志
     * 
     * @param sysYunwei 运维日志
     * @return 结果
     */
    @Override
    public int updateSysYunwei(SysYunwei sysYunwei)
    {
        return sysYunweiMapper.updateSysYunwei(sysYunwei);
    }

    /**
     * 批量删除运维日志
     * 
     * @param ids 需要删除的运维日志主键
     * @return 结果
     */
    @Override
    public int deleteSysYunweiByIds(Long[] ids)
    {
        return sysYunweiMapper.deleteSysYunweiByIds(ids);
    }

    /**
     * 删除运维日志信息
     * 
     * @param id 运维日志主键
     * @return 结果
     */
    @Override
    public int deleteSysYunweiById(Long id)
    {
        return sysYunweiMapper.deleteSysYunweiById(id);
    }
}
