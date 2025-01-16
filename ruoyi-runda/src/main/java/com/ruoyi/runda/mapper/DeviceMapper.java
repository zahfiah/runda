package com.ruoyi.runda.mapper;

import java.util.List;
import com.ruoyi.runda.domain.Device;

/**
 * 监测设备管理设备Mapper接口
 * 
 * @author runda
 * @date 2025-01-11
 */
public interface DeviceMapper 
{
    /**
     * 查询监测设备管理设备
     * 
     * @param id 监测设备管理设备主键
     * @return 监测设备管理设备
     */
    public Device selectDeviceById(Long id);

    /**
     * 查询监测设备管理设备列表
     * 
     * @param device 监测设备管理设备
     * @return 监测设备管理设备集合
     */
    public List<Device> selectDeviceList(Device device);

    /**
     * 新增监测设备管理设备
     * 
     * @param device 监测设备管理设备
     * @return 结果
     */
    public int insertDevice(Device device);

    /**
     * 修改监测设备管理设备
     * 
     * @param device 监测设备管理设备
     * @return 结果
     */
    public int updateDevice(Device device);

    /**
     * 删除监测设备管理设备
     * 
     * @param id 监测设备管理设备主键
     * @return 结果
     */
    public int deleteDeviceById(Long id);

    /**
     * 批量删除监测设备管理设备
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteDeviceByIds(Long[] ids);
}
