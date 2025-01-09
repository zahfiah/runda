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
import com.ruoyi.runda.domain.Station;
import com.ruoyi.runda.service.IStationService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 监测站点管理Controller
 * 
 * @author runda
 * @date 2025-01-07
 */
@RestController
@RequestMapping("/runda/station")
public class StationController extends BaseController
{
    @Autowired
    private IStationService stationService;

    /**
     * 查询监测站点管理列表
     */
    @PreAuthorize("@ss.hasPermi('runda:station:list')")
    @GetMapping("/list")
    public TableDataInfo list(Station station)
    {
        startPage();
        List<Station> list = stationService.selectStationList(station);

        return getDataTable(list);
    }

    /**
     * 导出监测站点管理列表
     */
    @PreAuthorize("@ss.hasPermi('runda:station:export')")
    @Log(title = "监测站点管理", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, Station station)
    {
        List<Station> list = stationService.selectStationList(station);
        ExcelUtil<Station> util = new ExcelUtil<Station>(Station.class);
        util.exportExcel(response, list, "监测站点管理数据");
    }

    /**
     * 获取监测站点管理详细信息
     */
    @PreAuthorize("@ss.hasPermi('runda:station:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(stationService.selectStationById(id));
    }

    /**
     * 新增监测站点管理
     */
    @PreAuthorize("@ss.hasPermi('runda:station:add')")
    @Log(title = "监测站点管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody Station station)
    {
        return toAjax(stationService.insertStation(station));
    }

    /**
     * 修改监测站点管理
     */
    @PreAuthorize("@ss.hasPermi('runda:station:edit')")
    @Log(title = "监测站点管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody Station station)
    {
        return toAjax(stationService.updateStation(station));
    }

    /**
     * 删除监测站点管理
     */
    @PreAuthorize("@ss.hasPermi('runda:station:remove')")
    @Log(title = "监测站点管理", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(stationService.deleteStationByIds(ids));
    }
}
