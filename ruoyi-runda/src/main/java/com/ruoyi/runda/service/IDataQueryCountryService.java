package com.ruoyi.runda.service;

import java.util.List;
import com.ruoyi.runda.domain.DataQueryCountry;

/**
 * 国控数据Service接口
 *
 * @author ruoyi
 * @date 2025-02-25
 */
public interface IDataQueryCountryService
{
    /**
     * 查询国控数据
     *
     * @param id 国控数据主键
     * @return 国控数据
     */
    public DataQueryCountry selectDataQueryCountryById(Long id);

    /**
     * 查询国控数据列表
     *
     * @param dataQueryCountry 国控数据
     * @return 国控数据集合
     */
    public List<DataQueryCountry> selectDataQueryCountryList(DataQueryCountry dataQueryCountry);

    /**
     * 新增国控数据
     *
     * @param dataQueryCountry 国控数据
     * @return 结果
     */
    public int insertDataQueryCountry(DataQueryCountry dataQueryCountry);

    /**
     * 修改国控数据
     *
     * @param dataQueryCountry 国控数据
     * @return 结果
     */
    public int updateDataQueryCountry(DataQueryCountry dataQueryCountry);

    /**
     * 批量删除国控数据
     *
     * @param ids 需要删除的国控数据主键集合
     * @return 结果
     */
    public int deleteDataQueryCountryByIds(Long[] ids);

    /**
     * 删除国控数据信息
     *
     * @param id 国控数据主键
     * @return 结果
     */
    public int deleteDataQueryCountryById(Long id);

    public void httpRequest();
}
