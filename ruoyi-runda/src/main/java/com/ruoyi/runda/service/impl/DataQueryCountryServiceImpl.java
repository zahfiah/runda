package com.ruoyi.runda.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.runda.mapper.DataQueryCountryMapper;
import com.ruoyi.runda.domain.DataQueryCountry;
import com.ruoyi.runda.service.IDataQueryCountryService;

/**
 * 国控数据查询Service业务层处理
 * 
 * @author runda
 * @date 2025-01-07
 */
@Service
public class DataQueryCountryServiceImpl implements IDataQueryCountryService 
{
    @Autowired
    private DataQueryCountryMapper dataQueryCountryMapper;

    /**
     * 查询国控数据查询
     * 
     * @param id 国控数据查询主键
     * @return 国控数据查询
     */
    @Override
    public DataQueryCountry selectDataQueryCountryById(Long id)
    {
        return dataQueryCountryMapper.selectDataQueryCountryById(id);
    }

    /**
     * 查询国控数据查询列表
     * 
     * @param dataQueryCountry 国控数据查询
     * @return 国控数据查询
     */
    @Override
    public List<DataQueryCountry> selectDataQueryCountryList(DataQueryCountry dataQueryCountry)
    {
        return dataQueryCountryMapper.selectDataQueryCountryList(dataQueryCountry);
    }

    /**
     * 新增国控数据查询
     * 
     * @param dataQueryCountry 国控数据查询
     * @return 结果
     */
    @Override
    public int insertDataQueryCountry(DataQueryCountry dataQueryCountry)
    {
        return dataQueryCountryMapper.insertDataQueryCountry(dataQueryCountry);
    }

    /**
     * 修改国控数据查询
     * 
     * @param dataQueryCountry 国控数据查询
     * @return 结果
     */
    @Override
    public int updateDataQueryCountry(DataQueryCountry dataQueryCountry)
    {
        return dataQueryCountryMapper.updateDataQueryCountry(dataQueryCountry);
    }

    /**
     * 批量删除国控数据查询
     * 
     * @param ids 需要删除的国控数据查询主键
     * @return 结果
     */
    @Override
    public int deleteDataQueryCountryByIds(Long[] ids)
    {
        return dataQueryCountryMapper.deleteDataQueryCountryByIds(ids);
    }

    /**
     * 删除国控数据查询信息
     * 
     * @param id 国控数据查询主键
     * @return 结果
     */
    @Override
    public int deleteDataQueryCountryById(Long id)
    {
        return dataQueryCountryMapper.deleteDataQueryCountryById(id);
    }
}
