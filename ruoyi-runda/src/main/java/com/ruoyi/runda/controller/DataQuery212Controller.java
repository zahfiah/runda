package com.ruoyi.runda.controller;

import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.runda.domain.DataQuery212;
import com.ruoyi.runda.service.IDataQuery212Service;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 大气数据查询212Controller
 * 
 * @author runda
 * @date 2025-01-07
 */
@RestController
@RequestMapping("/runda/query212")
public class DataQuery212Controller extends BaseController
{
    @Autowired
    private IDataQuery212Service dataQuery212Service;

    /**
     * 查询大气数据查询212列表
     */
    @PreAuthorize("@ss.hasPermi('runda:query212:list')")
    @GetMapping("/list")
    public TableDataInfo list(DataQuery212 dataQuery212)
    {
        startPage();
        List<DataQuery212> list = dataQuery212Service.selectDataQuery212List(dataQuery212);
        return getDataTable(list);
    }

    /**
     * 导出大气数据查询212列表
     */
    @PreAuthorize("@ss.hasPermi('runda:query212:export')")
    @Log(title = "大气数据查询212", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, DataQuery212 dataQuery212)
    {
        List<DataQuery212> list = dataQuery212Service.selectDataQuery212List(dataQuery212);
        ExcelUtil<DataQuery212> util = new ExcelUtil<DataQuery212>(DataQuery212.class);
        util.exportExcel(response, list, "大气数据查询212数据");
    }

    /**
     * 获取大气数据查询212详细信息
     */
    @PreAuthorize("@ss.hasPermi('runda:query212:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(dataQuery212Service.selectDataQuery212ById(id));
    }

    /**
     * 新增大气数据查询212
     */
    @PreAuthorize("@ss.hasPermi('runda:query212:add')")
    @Log(title = "大气数据查询212", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody DataQuery212 dataQuery212)
    {
        return toAjax(dataQuery212Service.insertDataQuery212(dataQuery212));
    }

    /**
     * 修改大气数据查询212
     */
    @PreAuthorize("@ss.hasPermi('runda:query212:edit')")
    @Log(title = "大气数据查询212", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody DataQuery212 dataQuery212)
    {
        return toAjax(dataQuery212Service.updateDataQuery212(dataQuery212));
    }

    /**
     * 删除大气数据查询212
     */
    @PreAuthorize("@ss.hasPermi('runda:query212:remove')")
    @Log(title = "大气数据查询212", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(dataQuery212Service.deleteDataQuery212ByIds(ids));
    }
}
