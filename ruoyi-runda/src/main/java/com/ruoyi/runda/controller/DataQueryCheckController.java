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
import com.ruoyi.runda.domain.DataQueryCheck;
import com.ruoyi.runda.service.IDataQueryCheckService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 手动矫正Controller
 * 
 * @author runda
 * @date 2025-01-07
 */
@RestController
@RequestMapping("/runda/check")
public class DataQueryCheckController extends BaseController
{
    @Autowired
    private IDataQueryCheckService dataQueryCheckService;

    /**
     * 查询手动矫正列表
     */
    @PreAuthorize("@ss.hasPermi('runda:check:list')")
    @GetMapping("/list")
    public TableDataInfo list(DataQueryCheck dataQueryCheck)
    {
        startPage();
        List<DataQueryCheck> list = dataQueryCheckService.selectDataQueryCheckList(dataQueryCheck);
        return getDataTable(list);
    }

    /**
     * 导出手动矫正列表
     */
    @PreAuthorize("@ss.hasPermi('runda:check:export')")
    @Log(title = "手动矫正", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, DataQueryCheck dataQueryCheck)
    {
        List<DataQueryCheck> list = dataQueryCheckService.selectDataQueryCheckList(dataQueryCheck);
        ExcelUtil<DataQueryCheck> util = new ExcelUtil<DataQueryCheck>(DataQueryCheck.class);
        util.exportExcel(response, list, "手动矫正数据");
    }

    /**
     * 获取手动矫正详细信息
     */
    @PreAuthorize("@ss.hasPermi('runda:check:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(dataQueryCheckService.selectDataQueryCheckById(id));
    }

    /**
     * 新增手动矫正
     */
    @PreAuthorize("@ss.hasPermi('runda:check:add')")
    @Log(title = "手动矫正", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody DataQueryCheck dataQueryCheck)
    {
        return toAjax(dataQueryCheckService.insertDataQueryCheck(dataQueryCheck));
    }

    /**
     * 修改手动矫正
     */
    @PreAuthorize("@ss.hasPermi('runda:check:edit')")
    @Log(title = "手动矫正", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody DataQueryCheck dataQueryCheck)
    {
        return toAjax(dataQueryCheckService.updateDataQueryCheck(dataQueryCheck));
    }

    /**
     * 删除手动矫正
     */
    @PreAuthorize("@ss.hasPermi('runda:check:remove')")
    @Log(title = "手动矫正", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(dataQueryCheckService.deleteDataQueryCheckByIds(ids));
    }
}
