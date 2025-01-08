package com.ruoyi.runda.service;

import java.util.List;
import com.ruoyi.runda.domain.EnterpriseList;

/**
 * 单位名录Service接口
 * 
 * @author runda
 * @date 2025-01-07
 */
public interface IEnterpriseListService 
{
    /**
     * 查询单位名录
     * 
     * @param id 单位名录主键
     * @return 单位名录
     */
    public EnterpriseList selectEnterpriseListById(Long id);

    /**
     * 查询单位名录列表
     * 
     * @param enterpriseList 单位名录
     * @return 单位名录集合
     */
    public List<EnterpriseList> selectEnterpriseListList(EnterpriseList enterpriseList);

    /**
     * 新增单位名录
     * 
     * @param enterpriseList 单位名录
     * @return 结果
     */
    public int insertEnterpriseList(EnterpriseList enterpriseList);

    /**
     * 修改单位名录
     * 
     * @param enterpriseList 单位名录
     * @return 结果
     */
    public int updateEnterpriseList(EnterpriseList enterpriseList);

    /**
     * 批量删除单位名录
     * 
     * @param ids 需要删除的单位名录主键集合
     * @return 结果
     */
    public int deleteEnterpriseListByIds(Long[] ids);

    /**
     * 删除单位名录信息
     * 
     * @param id 单位名录主键
     * @return 结果
     */
    public int deleteEnterpriseListById(Long id);
}
