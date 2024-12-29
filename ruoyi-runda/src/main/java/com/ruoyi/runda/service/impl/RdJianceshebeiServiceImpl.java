package com.ruoyi.runda.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.runda.mapper.RdJianceshebeiMapper;
import com.ruoyi.runda.domain.RdJianceshebei;
import com.ruoyi.runda.service.IRdJianceshebeiService;

/**
 * 设备Service业务层处理
 * 
 * @author ruoyi
 * @date 2024-12-28
 */
@Service
public class RdJianceshebeiServiceImpl implements IRdJianceshebeiService 
{
    @Autowired
    private RdJianceshebeiMapper rdJianceshebeiMapper;

    /**
     * 查询设备
     * 
     * @param id 设备主键
     * @return 设备
     */
    @Override
    public RdJianceshebei selectRdJianceshebeiById(Long id)
    {
        return rdJianceshebeiMapper.selectRdJianceshebeiById(id);
    }

    /**
     * 查询设备列表
     * 
     * @param rdJianceshebei 设备
     * @return 设备
     */
    @Override
    public List<RdJianceshebei> selectRdJianceshebeiList(RdJianceshebei rdJianceshebei)
    {
        return rdJianceshebeiMapper.selectRdJianceshebeiList(rdJianceshebei);
    }

    /**
     * 新增设备
     * 
     * @param rdJianceshebei 设备
     * @return 结果
     */
    @Override
    public int insertRdJianceshebei(RdJianceshebei rdJianceshebei)
    {
        return rdJianceshebeiMapper.insertRdJianceshebei(rdJianceshebei);
    }

    /**
     * 修改设备
     * 
     * @param rdJianceshebei 设备
     * @return 结果
     */
    @Override
    public int updateRdJianceshebei(RdJianceshebei rdJianceshebei)
    {
        return rdJianceshebeiMapper.updateRdJianceshebei(rdJianceshebei);
    }

    /**
     * 批量删除设备
     * 
     * @param ids 需要删除的设备主键
     * @return 结果
     */
    @Override
    public int deleteRdJianceshebeiByIds(Long[] ids)
    {
        return rdJianceshebeiMapper.deleteRdJianceshebeiByIds(ids);
    }

    /**
     * 删除设备信息
     * 
     * @param id 设备主键
     * @return 结果
     */
    @Override
    public int deleteRdJianceshebeiById(Long id)
    {
        return rdJianceshebeiMapper.deleteRdJianceshebeiById(id);
    }
}
