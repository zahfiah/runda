package com.ruoyi.runda.service;

import java.util.List;
import com.ruoyi.runda.domain.DataQuery212;

/**
 * 大气数据查询212Service接口
 * 
 * @author runda
 * @date 2025-01-07
 */
public interface IDataQuery212Service 
{
    /**
     * 查询大气数据查询212
     * 
     * @param id 大气数据查询212主键
     * @return 大气数据查询212
     */
    public DataQuery212 selectDataQuery212ById(Long id);

    /**
     * 查询大气数据查询212列表
     * 
     * @param dataQuery212 大气数据查询212
     * @return 大气数据查询212集合
     */
    public List<DataQuery212> selectDataQuery212List(DataQuery212 dataQuery212);

    /**
     * 新增大气数据查询212
     * 
     * @param dataQuery212 大气数据查询212
     * @return 结果
     */
    public int insertDataQuery212(DataQuery212 dataQuery212);

    /**
     * 修改大气数据查询212
     * 
     * @param dataQuery212 大气数据查询212
     * @return 结果
     */
    public int updateDataQuery212(DataQuery212 dataQuery212);

    /**
     * 批量删除大气数据查询212
     * 
     * @param ids 需要删除的大气数据查询212主键集合
     * @return 结果
     */
    public int deleteDataQuery212ByIds(Long[] ids);

    /**
     * 删除大气数据查询212信息
     * 
     * @param id 大气数据查询212主键
     * @return 结果
     */
    public int deleteDataQuery212ById(Long id);
}
