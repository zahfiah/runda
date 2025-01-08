package com.ruoyi.runda.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.runda.mapper.DataCalibrationMapper;
import com.ruoyi.runda.domain.DataCalibration;
import com.ruoyi.runda.service.IDataCalibrationService;

/**
 * 数据校准管理Service业务层处理
 * 
 * @author runda
 * @date 2025-01-07
 */
@Service
public class DataCalibrationServiceImpl implements IDataCalibrationService 
{
    @Autowired
    private DataCalibrationMapper dataCalibrationMapper;

    /**
     * 查询数据校准管理
     * 
     * @param id 数据校准管理主键
     * @return 数据校准管理
     */
    @Override
    public DataCalibration selectDataCalibrationById(Long id)
    {
        return dataCalibrationMapper.selectDataCalibrationById(id);
    }

    /**
     * 查询数据校准管理列表
     * 
     * @param dataCalibration 数据校准管理
     * @return 数据校准管理
     */
    @Override
    public List<DataCalibration> selectDataCalibrationList(DataCalibration dataCalibration)
    {
        return dataCalibrationMapper.selectDataCalibrationList(dataCalibration);
    }

    /**
     * 新增数据校准管理
     * 
     * @param dataCalibration 数据校准管理
     * @return 结果
     */
    @Override
    public int insertDataCalibration(DataCalibration dataCalibration)
    {
        return dataCalibrationMapper.insertDataCalibration(dataCalibration);
    }

    /**
     * 修改数据校准管理
     * 
     * @param dataCalibration 数据校准管理
     * @return 结果
     */
    @Override
    public int updateDataCalibration(DataCalibration dataCalibration)
    {
        return dataCalibrationMapper.updateDataCalibration(dataCalibration);
    }

    /**
     * 批量删除数据校准管理
     * 
     * @param ids 需要删除的数据校准管理主键
     * @return 结果
     */
    @Override
    public int deleteDataCalibrationByIds(Long[] ids)
    {
        return dataCalibrationMapper.deleteDataCalibrationByIds(ids);
    }

    /**
     * 删除数据校准管理信息
     * 
     * @param id 数据校准管理主键
     * @return 结果
     */
    @Override
    public int deleteDataCalibrationById(Long id)
    {
        return dataCalibrationMapper.deleteDataCalibrationById(id);
    }
}
