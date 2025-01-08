package com.ruoyi.runda.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.runda.mapper.DataQueryCheckMapper;
import com.ruoyi.runda.domain.DataQueryCheck;
import com.ruoyi.runda.service.IDataQueryCheckService;

/**
 * 手动矫正Service业务层处理
 * 
 * @author runda
 * @date 2025-01-07
 */
@Service
public class DataQueryCheckServiceImpl implements IDataQueryCheckService 
{
    @Autowired
    private DataQueryCheckMapper dataQueryCheckMapper;

    /**
     * 查询手动矫正
     * 
     * @param id 手动矫正主键
     * @return 手动矫正
     */
    @Override
    public DataQueryCheck selectDataQueryCheckById(Long id)
    {
        return dataQueryCheckMapper.selectDataQueryCheckById(id);
    }

    /**
     * 查询手动矫正列表
     * 
     * @param dataQueryCheck 手动矫正
     * @return 手动矫正
     */
    @Override
    public List<DataQueryCheck> selectDataQueryCheckList(DataQueryCheck dataQueryCheck)
    {
        return dataQueryCheckMapper.selectDataQueryCheckList(dataQueryCheck);
    }

    /**
     * 新增手动矫正
     * 
     * @param dataQueryCheck 手动矫正
     * @return 结果
     */
    @Override
    public int insertDataQueryCheck(DataQueryCheck dataQueryCheck)
    {
        return dataQueryCheckMapper.insertDataQueryCheck(dataQueryCheck);
    }

    /**
     * 修改手动矫正
     * 
     * @param dataQueryCheck 手动矫正
     * @return 结果
     */
    @Override
    public int updateDataQueryCheck(DataQueryCheck dataQueryCheck)
    {
        return dataQueryCheckMapper.updateDataQueryCheck(dataQueryCheck);
    }

    /**
     * 批量删除手动矫正
     * 
     * @param ids 需要删除的手动矫正主键
     * @return 结果
     */
    @Override
    public int deleteDataQueryCheckByIds(Long[] ids)
    {
        return dataQueryCheckMapper.deleteDataQueryCheckByIds(ids);
    }

    /**
     * 删除手动矫正信息
     * 
     * @param id 手动矫正主键
     * @return 结果
     */
    @Override
    public int deleteDataQueryCheckById(Long id)
    {
        return dataQueryCheckMapper.deleteDataQueryCheckById(id);
    }
}
