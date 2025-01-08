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
import com.ruoyi.runda.domain.AlarmRemind;
import com.ruoyi.runda.service.IAlarmRemindService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 告警设置Controller
 * 
 * @author runda
 * @date 2025-01-07
 */
@RestController
@RequestMapping("/runda/remind")
public class AlarmRemindController extends BaseController
{
    @Autowired
    private IAlarmRemindService alarmRemindService;

    /**
     * 查询告警设置列表
     */
    @PreAuthorize("@ss.hasPermi('runda:remind:list')")
    @GetMapping("/list")
    public TableDataInfo list(AlarmRemind alarmRemind)
    {
        startPage();
        List<AlarmRemind> list = alarmRemindService.selectAlarmRemindList(alarmRemind);
        return getDataTable(list);
    }

    /**
     * 导出告警设置列表
     */
    @PreAuthorize("@ss.hasPermi('runda:remind:export')")
    @Log(title = "告警设置", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, AlarmRemind alarmRemind)
    {
        List<AlarmRemind> list = alarmRemindService.selectAlarmRemindList(alarmRemind);
        ExcelUtil<AlarmRemind> util = new ExcelUtil<AlarmRemind>(AlarmRemind.class);
        util.exportExcel(response, list, "告警设置数据");
    }

    /**
     * 获取告警设置详细信息
     */
    @PreAuthorize("@ss.hasPermi('runda:remind:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(alarmRemindService.selectAlarmRemindById(id));
    }

    /**
     * 新增告警设置
     */
    @PreAuthorize("@ss.hasPermi('runda:remind:add')")
    @Log(title = "告警设置", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody AlarmRemind alarmRemind)
    {
        return toAjax(alarmRemindService.insertAlarmRemind(alarmRemind));
    }

    /**
     * 修改告警设置
     */
    @PreAuthorize("@ss.hasPermi('runda:remind:edit')")
    @Log(title = "告警设置", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody AlarmRemind alarmRemind)
    {
        return toAjax(alarmRemindService.updateAlarmRemind(alarmRemind));
    }

    /**
     * 删除告警设置
     */
    @PreAuthorize("@ss.hasPermi('runda:remind:remove')")
    @Log(title = "告警设置", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(alarmRemindService.deleteAlarmRemindByIds(ids));
    }
}
