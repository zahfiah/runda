package com.ruoyi.runda.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.runda.mapper.EnterpriseListMapper;
import com.ruoyi.runda.domain.EnterpriseList;
import com.ruoyi.runda.service.IEnterpriseListService;

/**
 * 单位名录Service业务层处理
 * 
 * @author runda
 * @date 2025-01-07
 */
@Service
public class EnterpriseListServiceImpl implements IEnterpriseListService 
{
    @Autowired
    private EnterpriseListMapper enterpriseListMapper;

    /**
     * 查询单位名录
     * 
     * @param id 单位名录主键
     * @return 单位名录
     */
    @Override
    public EnterpriseList selectEnterpriseListById(Long id)
    {
        return enterpriseListMapper.selectEnterpriseListById(id);
    }

    /**
     * 查询单位名录列表
     * 
     * @param enterpriseList 单位名录
     * @return 单位名录
     */
    @Override
    public List<EnterpriseList> selectEnterpriseListList(EnterpriseList enterpriseList)
    {
        return enterpriseListMapper.selectEnterpriseListList(enterpriseList);
    }

    /**
     * 新增单位名录
     * 
     * @param enterpriseList 单位名录
     * @return 结果
     */
    @Override
    public int insertEnterpriseList(EnterpriseList enterpriseList)
    {
        enterpriseList.setCreateTime(DateUtils.getNowDate());
        return enterpriseListMapper.insertEnterpriseList(enterpriseList);
    }

    /**
     * 修改单位名录
     * 
     * @param enterpriseList 单位名录
     * @return 结果
     */
    @Override
    public int updateEnterpriseList(EnterpriseList enterpriseList)
    {
        return enterpriseListMapper.updateEnterpriseList(enterpriseList);
    }

    /**
     * 批量删除单位名录
     * 
     * @param ids 需要删除的单位名录主键
     * @return 结果
     */
    @Override
    public int deleteEnterpriseListByIds(Long[] ids)
    {
        return enterpriseListMapper.deleteEnterpriseListByIds(ids);
    }

    /**
     * 删除单位名录信息
     * 
     * @param id 单位名录主键
     * @return 结果
     */
    @Override
    public int deleteEnterpriseListById(Long id)
    {
        return enterpriseListMapper.deleteEnterpriseListById(id);
    }
}
