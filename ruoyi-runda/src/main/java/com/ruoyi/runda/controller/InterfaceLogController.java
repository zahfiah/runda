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
import com.ruoyi.runda.domain.InterfaceLog;
import com.ruoyi.runda.service.IInterfaceLogService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 接口日志Controller
 * 
 * @author runda
 * @date 2025-01-07
 */
@RestController
@RequestMapping("/runda/log")
public class InterfaceLogController extends BaseController
{
    @Autowired
    private IInterfaceLogService interfaceLogService;

    /**
     * 查询接口日志列表
     */
    @PreAuthorize("@ss.hasPermi('runda:log:list')")
    @GetMapping("/list")
    public TableDataInfo list(InterfaceLog interfaceLog)
    {
        startPage();
        List<InterfaceLog> list = interfaceLogService.selectInterfaceLogList(interfaceLog);
        return getDataTable(list);
    }

    /**
     * 导出接口日志列表
     */
    @PreAuthorize("@ss.hasPermi('runda:log:export')")
    @Log(title = "接口日志", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, InterfaceLog interfaceLog)
    {
        List<InterfaceLog> list = interfaceLogService.selectInterfaceLogList(interfaceLog);
        ExcelUtil<InterfaceLog> util = new ExcelUtil<InterfaceLog>(InterfaceLog.class);
        util.exportExcel(response, list, "接口日志数据");
    }

    /**
     * 获取接口日志详细信息
     */
    @PreAuthorize("@ss.hasPermi('runda:log:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(interfaceLogService.selectInterfaceLogById(id));
    }

    /**
     * 新增接口日志
     */
    @PreAuthorize("@ss.hasPermi('runda:log:add')")
    @Log(title = "接口日志", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody InterfaceLog interfaceLog)
    {
        return toAjax(interfaceLogService.insertInterfaceLog(interfaceLog));
    }

    /**
     * 修改接口日志
     */
    @PreAuthorize("@ss.hasPermi('runda:log:edit')")
    @Log(title = "接口日志", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody InterfaceLog interfaceLog)
    {
        return toAjax(interfaceLogService.updateInterfaceLog(interfaceLog));
    }

    /**
     * 删除接口日志
     */
    @PreAuthorize("@ss.hasPermi('runda:log:remove')")
    @Log(title = "接口日志", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(interfaceLogService.deleteInterfaceLogByIds(ids));
    }
}
