package com.ruoyi.runda.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.runda.mapper.StationMapper;
import com.ruoyi.runda.domain.Station;
import com.ruoyi.runda.service.IStationService;

/**
 * 监测站点管理Service业务层处理
 * 
 * @author runda
 * @date 2025-01-10
 */
@Service
public class StationServiceImpl implements IStationService 
{
    @Autowired
    private StationMapper stationMapper;

    /**
     * 查询监测站点管理
     * 
     * @param id 监测站点管理主键
     * @return 监测站点管理
     */
    @Override
    public Station selectStationById(Long id)
    {
        return stationMapper.selectStationById(id);
    }

    /**
     * 查询监测站点管理列表
     * 
     * @param station 监测站点管理
     * @return 监测站点管理
     */
    @Override
    public List<Station> selectStationList(Station station)
    {
        return stationMapper.selectStationList(station);
    }

    /**
     * 新增监测站点管理
     * 
     * @param station 监测站点管理
     * @return 结果
     */
    @Override
    public int insertStation(Station station)
    {
        return stationMapper.insertStation(station);
    }

    /**
     * 修改监测站点管理
     * 
     * @param station 监测站点管理
     * @return 结果
     */
    @Override
    public int updateStation(Station station)
    {
        return stationMapper.updateStation(station);
    }

    /**
     * 批量删除监测站点管理
     * 
     * @param ids 需要删除的监测站点管理主键
     * @return 结果
     */
    @Override
    public int deleteStationByIds(Long[] ids)
    {
        return stationMapper.deleteStationByIds(ids);
    }

    /**
     * 删除监测站点管理信息
     * 
     * @param id 监测站点管理主键
     * @return 结果
     */
    @Override
    public int deleteStationById(Long id)
    {
        return stationMapper.deleteStationById(id);
    }
}
