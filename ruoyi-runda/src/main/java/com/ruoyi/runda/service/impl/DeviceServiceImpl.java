package com.ruoyi.runda.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.runda.mapper.DeviceMapper;
import com.ruoyi.runda.domain.Device;
import com.ruoyi.runda.service.IDeviceService;

/**
 * 监测设备管理设备Service业务层处理
 * 
 * @author runda
 * @date 2025-01-11
 */
@Service
public class DeviceServiceImpl implements IDeviceService 
{
    @Autowired
    private DeviceMapper deviceMapper;

    /**
     * 查询监测设备管理设备
     * 
     * @param id 监测设备管理设备主键
     * @return 监测设备管理设备
     */
    @Override
    public Device selectDeviceById(Long id)
    {
        return deviceMapper.selectDeviceById(id);
    }

    /**
     * 查询监测设备管理设备列表
     * 
     * @param device 监测设备管理设备
     * @return 监测设备管理设备
     */
    @Override
    public List<Device> selectDeviceList(Device device)
    {
        return deviceMapper.selectDeviceList(device);
    }

    /**
     * 新增监测设备管理设备
     * 
     * @param device 监测设备管理设备
     * @return 结果
     */
    @Override
    public int insertDevice(Device device)
    {
        return deviceMapper.insertDevice(device);
    }

    /**
     * 修改监测设备管理设备
     * 
     * @param device 监测设备管理设备
     * @return 结果
     */
    @Override
    public int updateDevice(Device device)
    {
        return deviceMapper.updateDevice(device);
    }

    /**
     * 批量删除监测设备管理设备
     * 
     * @param ids 需要删除的监测设备管理设备主键
     * @return 结果
     */
    @Override
    public int deleteDeviceByIds(Long[] ids)
    {
        return deviceMapper.deleteDeviceByIds(ids);
    }

    /**
     * 删除监测设备管理设备信息
     * 
     * @param id 监测设备管理设备主键
     * @return 结果
     */
    @Override
    public int deleteDeviceById(Long id)
    {
        return deviceMapper.deleteDeviceById(id);
    }
}
