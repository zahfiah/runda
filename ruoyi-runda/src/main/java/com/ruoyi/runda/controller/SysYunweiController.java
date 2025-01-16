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
import com.ruoyi.runda.domain.SysYunwei;
import com.ruoyi.runda.service.ISysYunweiService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 运维日志Controller
 * 
 * @author runda
 * @date 2025-01-11
 */
@RestController
@RequestMapping("/runda/yunwei")
public class SysYunweiController extends BaseController
{
    @Autowired
    private ISysYunweiService sysYunweiService;

    /**
     * 查询运维日志列表
     */
    @PreAuthorize("@ss.hasPermi('runda:yunwei:list')")
    @GetMapping("/list")
    public TableDataInfo list(SysYunwei sysYunwei)
    {
        startPage();
        List<SysYunwei> list = sysYunweiService.selectSysYunweiList(sysYunwei);
        return getDataTable(list);
    }

    /**
     * 导出运维日志列表
     */
    @PreAuthorize("@ss.hasPermi('runda:yunwei:export')")
    @Log(title = "运维日志", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, SysYunwei sysYunwei)
    {
        List<SysYunwei> list = sysYunweiService.selectSysYunweiList(sysYunwei);
        ExcelUtil<SysYunwei> util = new ExcelUtil<SysYunwei>(SysYunwei.class);
        util.exportExcel(response, list, "运维日志数据");
    }

    /**
     * 获取运维日志详细信息
     */
    @PreAuthorize("@ss.hasPermi('runda:yunwei:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(sysYunweiService.selectSysYunweiById(id));
    }

    /**
     * 新增运维日志
     */
    @PreAuthorize("@ss.hasPermi('runda:yunwei:add')")
    @Log(title = "运维日志", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody SysYunwei sysYunwei)
    {
        return toAjax(sysYunweiService.insertSysYunwei(sysYunwei));
    }

    /**
     * 修改运维日志
     */
    @PreAuthorize("@ss.hasPermi('runda:yunwei:edit')")
    @Log(title = "运维日志", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody SysYunwei sysYunwei)
    {
        return toAjax(sysYunweiService.updateSysYunwei(sysYunwei));
    }

    /**
     * 删除运维日志
     */
    @PreAuthorize("@ss.hasPermi('runda:yunwei:remove')")
    @Log(title = "运维日志", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(sysYunweiService.deleteSysYunweiByIds(ids));
    }
}
