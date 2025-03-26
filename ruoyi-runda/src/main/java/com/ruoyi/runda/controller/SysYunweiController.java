package com.ruoyi.runda.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;

import com.ruoyi.common.utils.StringUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
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
    public TableDataInfo list(SysYunwei sysYunwei,
                              @RequestParam(value = "beginTime", required = false) String beginTime,
                              @RequestParam(value = "endTime", required = false) String endTime) {

        // 确保params对象存在
        if (sysYunwei.getParams() == null) {
            sysYunwei.setParams(new HashMap<>());
        }

        // 打印接收到的参数（调试用）
        System.out.println("接收到的参数 - beginTime: " + beginTime + ", endTime: " + endTime);

        // 设置开始时间（如果不为空）
        if (StringUtils.isNotBlank(beginTime)) {
            // 如果是纯日期格式（yyyy-MM-dd），补全时间部分
            if (beginTime.length() == 10) {
                sysYunwei.getParams().put("beginTime", beginTime + " 00:00:00");
            } else {
                sysYunwei.getParams().put("beginTime", beginTime);
            }
        }

        // 设置结束时间（如果不为空）
        if (StringUtils.isNotBlank(endTime)) {
            // 如果是纯日期格式（yyyy-MM-dd），补全时间部分
            if (endTime.length() == 10) {
                sysYunwei.getParams().put("endTime", endTime + " 23:59:59");
            } else {
                sysYunwei.getParams().put("endTime", endTime);
            }
        }

        // 打印最终设置的参数（调试用）
        System.out.println("设置的参数 - beginTime: " + sysYunwei.getParams().get("beginTime")
                + ", endTime: " + sysYunwei.getParams().get("endTime"));

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
    public void export(HttpServletResponse response, SysYunwei sysYunwei,
                       @RequestParam(value = "beginTime", required = false) String beginTime,
                       @RequestParam(value = "endTime", required = false) String endTime)
    {
        // 设置时间范围条件
        if (StringUtils.isNotBlank(beginTime) && StringUtils.isNotBlank(endTime)) {
            sysYunwei.setParams(Map.of(
                    "beginTime", beginTime,
                    "endTime", endTime
            ));
        }
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
