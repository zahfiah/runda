package com.ruoyi.runda.mapper;

import java.util.List;
import com.ruoyi.runda.domain.InterfaceLog;

/**
 * 接口日志Mapper接口
 * 
 * @author runda
 * @date 2025-01-07
 */
public interface InterfaceLogMapper 
{
    /**
     * 查询接口日志
     * 
     * @param id 接口日志主键
     * @return 接口日志
     */
    public InterfaceLog selectInterfaceLogById(Long id);

    /**
     * 查询接口日志列表
     * 
     * @param interfaceLog 接口日志
     * @return 接口日志集合
     */
    public List<InterfaceLog> selectInterfaceLogList(InterfaceLog interfaceLog);

    /**
     * 新增接口日志
     * 
     * @param interfaceLog 接口日志
     * @return 结果
     */
    public int insertInterfaceLog(InterfaceLog interfaceLog);

    /**
     * 修改接口日志
     * 
     * @param interfaceLog 接口日志
     * @return 结果
     */
    public int updateInterfaceLog(InterfaceLog interfaceLog);

    /**
     * 删除接口日志
     * 
     * @param id 接口日志主键
     * @return 结果
     */
    public int deleteInterfaceLogById(Long id);

    /**
     * 批量删除接口日志
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteInterfaceLogByIds(Long[] ids);
}
