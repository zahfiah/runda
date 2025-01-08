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
import com.ruoyi.runda.domain.AlarmData;
import com.ruoyi.runda.service.IAlarmDataService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 告警日志Controller
 * 
 * @author runda
 * @date 2025-01-07
 */
@RestController
@RequestMapping("/runda/data")
public class AlarmDataController extends BaseController
{
    @Autowired
    private IAlarmDataService alarmDataService;

    /**
     * 查询告警日志列表
     */
    @PreAuthorize("@ss.hasPermi('runda:data:list')")
    @GetMapping("/list")
    public TableDataInfo list(AlarmData alarmData)
    {
        startPage();
        List<AlarmData> list = alarmDataService.selectAlarmDataList(alarmData);
        return getDataTable(list);
    }

    /**
     * 导出告警日志列表
     */
    @PreAuthorize("@ss.hasPermi('runda:data:export')")
    @Log(title = "告警日志", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, AlarmData alarmData)
    {
        List<AlarmData> list = alarmDataService.selectAlarmDataList(alarmData);
        ExcelUtil<AlarmData> util = new ExcelUtil<AlarmData>(AlarmData.class);
        util.exportExcel(response, list, "告警日志数据");
    }

    /**
     * 获取告警日志详细信息
     */
    @PreAuthorize("@ss.hasPermi('runda:data:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(alarmDataService.selectAlarmDataById(id));
    }

    /**
     * 新增告警日志
     */
    @PreAuthorize("@ss.hasPermi('runda:data:add')")
    @Log(title = "告警日志", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody AlarmData alarmData)
    {
        return toAjax(alarmDataService.insertAlarmData(alarmData));
    }

    /**
     * 修改告警日志
     */
    @PreAuthorize("@ss.hasPermi('runda:data:edit')")
    @Log(title = "告警日志", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody AlarmData alarmData)
    {
        return toAjax(alarmDataService.updateAlarmData(alarmData));
    }

    /**
     * 删除告警日志
     */
    @PreAuthorize("@ss.hasPermi('runda:data:remove')")
    @Log(title = "告警日志", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(alarmDataService.deleteAlarmDataByIds(ids));
    }
}
