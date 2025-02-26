package com.ruoyi.runda.mapper;

import java.util.List;
import com.ruoyi.runda.domain.DataQueryCountry;

/**
 * 国控数据查询Mapper接口
 * 
 * @author runda
 * @date 2025-01-07
 */
public interface DataQueryCountryMapper 
{
    /**
     * 查询国控数据查询
     * 
     * @param id 国控数据查询主键
     * @return 国控数据查询
     */
    public DataQueryCountry selectDataQueryCountryById(Long id);

    /**
     * 查询国控数据查询列表
     * 
     * @param dataQueryCountry 国控数据查询
     * @return 国控数据查询集合
     */
    public List<DataQueryCountry> selectDataQueryCountryList(DataQueryCountry dataQueryCountry);

    /**
     * 新增国控数据查询
     * 
     * @param dataQueryCountry 国控数据查询
     * @return 结果
     */
    public int insertDataQueryCountry(DataQueryCountry dataQueryCountry);

    /**
     * 修改国控数据查询
     * 
     * @param dataQueryCountry 国控数据查询
     * @return 结果
     */
    public int updateDataQueryCountry(DataQueryCountry dataQueryCountry);

    /**
     * 删除国控数据查询
     * 
     * @param id 国控数据查询主键
     * @return 结果
     */
    public int deleteDataQueryCountryById(Long id);

    /**
     * 批量删除国控数据查询
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteDataQueryCountryByIds(Long[] ids);
}
