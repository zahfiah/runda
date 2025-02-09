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
import com.ruoyi.runda.domain.HourlyAverageAirData;
import com.ruoyi.runda.service.IHourlyAverageAirDataService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 监测小时报表Controller
 * 
 * @author runda
 * @date 2025-02-08
 */
@RestController
@RequestMapping("/runda/data-hour")
public class HourlyAverageAirDataController extends BaseController
{
    @Autowired
    private IHourlyAverageAirDataService hourlyAverageAirDataService;

    /**
     * 查询监测小时报表列表
     */
    @PreAuthorize("@ss.hasPermi('runda:data:list')")
    @GetMapping("/list")
    public TableDataInfo list(HourlyAverageAirData hourlyAverageAirData)
    {
        startPage();
        List<HourlyAverageAirData> list = hourlyAverageAirDataService.selectHourlyAverageAirDataList(hourlyAverageAirData);
        return getDataTable(list);
    }

    /**
     * 导出监测小时报表列表
     */
    @PreAuthorize("@ss.hasPermi('runda:data:export')")
    @Log(title = "监测小时报表", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, HourlyAverageAirData hourlyAverageAirData)
    {
        List<HourlyAverageAirData> list = hourlyAverageAirDataService.selectHourlyAverageAirDataList(hourlyAverageAirData);
        ExcelUtil<HourlyAverageAirData> util = new ExcelUtil<HourlyAverageAirData>(HourlyAverageAirData.class);
        util.exportExcel(response, list, "监测小时报表数据");
    }

    /**
     * 获取监测小时报表详细信息
     */
    @PreAuthorize("@ss.hasPermi('runda:data:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(hourlyAverageAirDataService.selectHourlyAverageAirDataById(id));
    }

}
