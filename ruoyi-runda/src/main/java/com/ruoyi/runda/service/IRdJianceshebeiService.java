package com.ruoyi.runda.service;

import java.util.List;
import com.ruoyi.runda.domain.RdJianceshebei;

/**
 * 设备Service接口
 * 
 * @author ruoyi
 * @date 2024-12-28
 */
public interface IRdJianceshebeiService 
{
    /**
     * 查询设备
     * 
     * @param id 设备主键
     * @return 设备
     */
    public RdJianceshebei selectRdJianceshebeiById(Long id);

    /**
     * 查询设备列表
     * 
     * @param rdJianceshebei 设备
     * @return 设备集合
     */
    public List<RdJianceshebei> selectRdJianceshebeiList(RdJianceshebei rdJianceshebei);

    /**
     * 新增设备
     * 
     * @param rdJianceshebei 设备
     * @return 结果
     */
    public int insertRdJianceshebei(RdJianceshebei rdJianceshebei);

    /**
     * 修改设备
     * 
     * @param rdJianceshebei 设备
     * @return 结果
     */
    public int updateRdJianceshebei(RdJianceshebei rdJianceshebei);

    /**
     * 批量删除设备
     * 
     * @param ids 需要删除的设备主键集合
     * @return 结果
     */
    public int deleteRdJianceshebeiByIds(Long[] ids);

    /**
     * 删除设备信息
     * 
     * @param id 设备主键
     * @return 结果
     */
    public int deleteRdJianceshebeiById(Long id);
}
