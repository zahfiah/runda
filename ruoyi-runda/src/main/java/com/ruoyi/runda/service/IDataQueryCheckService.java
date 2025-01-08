package com.ruoyi.runda.service;

import java.util.List;
import com.ruoyi.runda.domain.DataQueryCheck;

/**
 * 手动矫正Service接口
 * 
 * @author runda
 * @date 2025-01-07
 */
public interface IDataQueryCheckService 
{
    /**
     * 查询手动矫正
     * 
     * @param id 手动矫正主键
     * @return 手动矫正
     */
    public DataQueryCheck selectDataQueryCheckById(Long id);

    /**
     * 查询手动矫正列表
     * 
     * @param dataQueryCheck 手动矫正
     * @return 手动矫正集合
     */
    public List<DataQueryCheck> selectDataQueryCheckList(DataQueryCheck dataQueryCheck);

    /**
     * 新增手动矫正
     * 
     * @param dataQueryCheck 手动矫正
     * @return 结果
     */
    public int insertDataQueryCheck(DataQueryCheck dataQueryCheck);

    /**
     * 修改手动矫正
     * 
     * @param dataQueryCheck 手动矫正
     * @return 结果
     */
    public int updateDataQueryCheck(DataQueryCheck dataQueryCheck);

    /**
     * 批量删除手动矫正
     * 
     * @param ids 需要删除的手动矫正主键集合
     * @return 结果
     */
    public int deleteDataQueryCheckByIds(Long[] ids);

    /**
     * 删除手动矫正信息
     * 
     * @param id 手动矫正主键
     * @return 结果
     */
    public int deleteDataQueryCheckById(Long id);
}
