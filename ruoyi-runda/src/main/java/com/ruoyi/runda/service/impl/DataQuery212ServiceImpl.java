package com.ruoyi.runda.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.runda.mapper.DataQuery212Mapper;
import com.ruoyi.runda.domain.DataQuery212;
import com.ruoyi.runda.service.IDataQuery212Service;

/**
 * 大气数据查询212Service业务层处理
 * 
 * @author runda
 * @date 2025-01-07
 */
@Service
public class DataQuery212ServiceImpl implements IDataQuery212Service 
{
    @Autowired
    private DataQuery212Mapper dataQuery212Mapper;

    /**
     * 查询大气数据查询212
     * 
     * @param id 大气数据查询212主键
     * @return 大气数据查询212
     */
    @Override
    public DataQuery212 selectDataQuery212ById(Long id)
    {
        return dataQuery212Mapper.selectDataQuery212ById(id);
    }

    /**
     * 查询大气数据查询212列表
     * 
     * @param dataQuery212 大气数据查询212
     * @return 大气数据查询212
     */
    @Override
    public List<DataQuery212> selectDataQuery212List(DataQuery212 dataQuery212)
    {
        return dataQuery212Mapper.selectDataQuery212List(dataQuery212);
    }

    /**
     * 新增大气数据查询212
     * 
     * @param dataQuery212 大气数据查询212
     * @return 结果
     */
    @Override
    public int insertDataQuery212(DataQuery212 dataQuery212)
    {
        return dataQuery212Mapper.insertDataQuery212(dataQuery212);
    }

    /**
     * 修改大气数据查询212
     * 
     * @param dataQuery212 大气数据查询212
     * @return 结果
     */
    @Override
    public int updateDataQuery212(DataQuery212 dataQuery212)
    {
        return dataQuery212Mapper.updateDataQuery212(dataQuery212);
    }

    /**
     * 批量删除大气数据查询212
     * 
     * @param ids 需要删除的大气数据查询212主键
     * @return 结果
     */
    @Override
    public int deleteDataQuery212ByIds(Long[] ids)
    {
        return dataQuery212Mapper.deleteDataQuery212ByIds(ids);
    }

    /**
     * 删除大气数据查询212信息
     * 
     * @param id 大气数据查询212主键
     * @return 结果
     */
    @Override
    public int deleteDataQuery212ById(Long id)
    {
        return dataQuery212Mapper.deleteDataQuery212ById(id);
    }
}
