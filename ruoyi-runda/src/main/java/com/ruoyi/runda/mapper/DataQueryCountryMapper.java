package com.ruoyi.runda.mapper;

import java.util.List;
import com.ruoyi.runda.domain.DataQueryCountry;
import org.apache.ibatis.annotations.Select;

/**
 * 国控数据Mapper接口
 *
 * @author ruoyi
 * @date 2025-02-25
 */
public interface DataQueryCountryMapper
{
    /**
     * 查询国控数据
     *
     * @param id 国控数据主键
     * @return 国控数据
     */
    public DataQueryCountry selectDataQueryCountryById(Long id);

    /**
     * 查询国控数据
     *
     * @param name 国控站点名称
     * @return 国控数据
     */
    public DataQueryCountry selectDataQueryCountryByName(String name);

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
     * 删除国控数据
     *
     * @param id 国控数据主键
     * @return 结果
     */
    public int deleteDataQueryCountryById(Long id);

    /**
     * 批量删除国控数据
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteDataQueryCountryByIds(Long[] ids);

    @Select("SELECT id, latitude, longitude from data_query_country")
    public   List<DataQueryCountry> getAllDataQueryCountries();

    }
