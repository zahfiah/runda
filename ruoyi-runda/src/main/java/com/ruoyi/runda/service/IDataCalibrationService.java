package com.ruoyi.runda.service;

import java.util.List;
import com.ruoyi.runda.domain.DataCalibration;

/**
 * 数据校准管理Service接口
 * 
 * @author runda
 * @date 2025-01-07
 */
public interface IDataCalibrationService 
{
    /**
     * 查询数据校准管理
     * 
     * @param id 数据校准管理主键
     * @return 数据校准管理
     */
    public DataCalibration selectDataCalibrationById(Long id);

    /**
     * 查询数据校准管理列表
     * 
     * @param dataCalibration 数据校准管理
     * @return 数据校准管理集合
     */
    public List<DataCalibration> selectDataCalibrationList(DataCalibration dataCalibration);

    /**
     * 新增数据校准管理
     * 
     * @param dataCalibration 数据校准管理
     * @return 结果
     */
    public int insertDataCalibration(DataCalibration dataCalibration);

    /**
     * 修改数据校准管理
     * 
     * @param dataCalibration 数据校准管理
     * @return 结果
     */
    public int updateDataCalibration(DataCalibration dataCalibration);

    /**
     * 批量删除数据校准管理
     * 
     * @param ids 需要删除的数据校准管理主键集合
     * @return 结果
     */
    public int deleteDataCalibrationByIds(Long[] ids);

    /**
     * 删除数据校准管理信息
     * 
     * @param id 数据校准管理主键
     * @return 结果
     */
    public int deleteDataCalibrationById(Long id);
}
