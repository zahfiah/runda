package com.ruoyi.runda.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.runda.mapper.InterfaceLogMapper;
import com.ruoyi.runda.domain.InterfaceLog;
import com.ruoyi.runda.service.IInterfaceLogService;

/**
 * 接口日志Service业务层处理
 * 
 * @author runda
 * @date 2025-01-07
 */
@Service
public class InterfaceLogServiceImpl implements IInterfaceLogService 
{
    @Autowired
    private InterfaceLogMapper interfaceLogMapper;

    /**
     * 查询接口日志
     * 
     * @param id 接口日志主键
     * @return 接口日志
     */
    @Override
    public InterfaceLog selectInterfaceLogById(Long id)
    {
        return interfaceLogMapper.selectInterfaceLogById(id);
    }

    /**
     * 查询接口日志列表
     * 
     * @param interfaceLog 接口日志
     * @return 接口日志
     */
    @Override
    public List<InterfaceLog> selectInterfaceLogList(InterfaceLog interfaceLog)
    {
        return interfaceLogMapper.selectInterfaceLogList(interfaceLog);
    }

    /**
     * 新增接口日志
     * 
     * @param interfaceLog 接口日志
     * @return 结果
     */
    @Override
    public int insertInterfaceLog(InterfaceLog interfaceLog)
    {
        interfaceLog.setCreateTime(DateUtils.getNowDate());
        return interfaceLogMapper.insertInterfaceLog(interfaceLog);
    }

    /**
     * 修改接口日志
     * 
     * @param interfaceLog 接口日志
     * @return 结果
     */
    @Override
    public int updateInterfaceLog(InterfaceLog interfaceLog)
    {
        return interfaceLogMapper.updateInterfaceLog(interfaceLog);
    }

    /**
     * 批量删除接口日志
     * 
     * @param ids 需要删除的接口日志主键
     * @return 结果
     */
    @Override
    public int deleteInterfaceLogByIds(Long[] ids)
    {
        return interfaceLogMapper.deleteInterfaceLogByIds(ids);
    }

    /**
     * 删除接口日志信息
     * 
     * @param id 接口日志主键
     * @return 结果
     */
    @Override
    public int deleteInterfaceLogById(Long id)
    {
        return interfaceLogMapper.deleteInterfaceLogById(id);
    }
}
