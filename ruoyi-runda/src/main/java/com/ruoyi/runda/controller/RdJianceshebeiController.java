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
import com.ruoyi.runda.domain.RdJianceshebei;
import com.ruoyi.runda.service.IRdJianceshebeiService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 设备Controller
 * 
 * @author ruoyi
 * @date 2024-12-28
 */
@RestController
@RequestMapping("/runda/jianceshebei")
public class RdJianceshebeiController extends BaseController
{
    @Autowired
    private IRdJianceshebeiService rdJianceshebeiService;

    /**
     * 查询设备列表
     */
    @PreAuthorize("@ss.hasPermi('runda:jianceshebei:list')")
    @GetMapping("/list")
    public TableDataInfo list(RdJianceshebei rdJianceshebei)
    {
        startPage();
        List<RdJianceshebei> list = rdJianceshebeiService.selectRdJianceshebeiList(rdJianceshebei);
        return getDataTable(list);
    }

    /**
     * 导出设备列表
     */
    @PreAuthorize("@ss.hasPermi('runda:jianceshebei:export')")
    @Log(title = "设备", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, RdJianceshebei rdJianceshebei)
    {
        List<RdJianceshebei> list = rdJianceshebeiService.selectRdJianceshebeiList(rdJianceshebei);
        ExcelUtil<RdJianceshebei> util = new ExcelUtil<RdJianceshebei>(RdJianceshebei.class);
        util.exportExcel(response, list, "设备数据");
    }

    /**
     * 获取设备详细信息
     */
    @PreAuthorize("@ss.hasPermi('runda:jianceshebei:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(rdJianceshebeiService.selectRdJianceshebeiById(id));
    }

    /**
     * 新增设备
     */
    @PreAuthorize("@ss.hasPermi('runda:jianceshebei:add')")
    @Log(title = "设备", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody RdJianceshebei rdJianceshebei)
    {
        return toAjax(rdJianceshebeiService.insertRdJianceshebei(rdJianceshebei));
    }

    /**
     * 修改设备
     */
    @PreAuthorize("@ss.hasPermi('runda:jianceshebei:edit')")
    @Log(title = "设备", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody RdJianceshebei rdJianceshebei)
    {
        return toAjax(rdJianceshebeiService.updateRdJianceshebei(rdJianceshebei));
    }

    /**
     * 删除设备
     */
    @PreAuthorize("@ss.hasPermi('runda:jianceshebei:remove')")
    @Log(title = "设备", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(rdJianceshebeiService.deleteRdJianceshebeiByIds(ids));
    }
}
