package com.ruoyi.runda.service;

import java.util.List;
import com.ruoyi.runda.domain.Station;

/**
 * 监测站点管理Service接口
 * 
 * @author runda
 * @date 2025-01-07
 */
public interface IStationService 
{
    /**
     * 查询监测站点管理
     * 
     * @param id 监测站点管理主键
     * @return 监测站点管理
     */
    public Station selectStationById(Long id);

    /**
     * 查询监测站点管理列表
     * 
     * @param station 监测站点管理
     * @return 监测站点管理集合
     */
    public List<Station> selectStationList(Station station);

    /**
     * 新增监测站点管理
     * 
     * @param station 监测站点管理
     * @return 结果
     */
    public int insertStation(Station station);

    /**
     * 修改监测站点管理
     * 
     * @param station 监测站点管理
     * @return 结果
     */
    public int updateStation(Station station);

    /**
     * 批量删除监测站点管理
     * 
     * @param ids 需要删除的监测站点管理主键集合
     * @return 结果
     */
    public int deleteStationByIds(Long[] ids);

    /**
     * 删除监测站点管理信息
     * 
     * @param id 监测站点管理主键
     * @return 结果
     */
    public int deleteStationById(Long id);
}
