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
import com.ruoyi.runda.domain.DataCalibration;
import com.ruoyi.runda.service.IDataCalibrationService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 数据校准管理Controller
 * 
 * @author runda
 * @date 2025-01-07
 */
@RestController
@RequestMapping("/runda/calibration")
public class DataCalibrationController extends BaseController
{
    @Autowired
    private IDataCalibrationService dataCalibrationService;

    /**
     * 查询数据校准管理列表
     */
    @PreAuthorize("@ss.hasPermi('runda:calibration:list')")
    @GetMapping("/list")
    public TableDataInfo list(DataCalibration dataCalibration)
    {
        startPage();
        List<DataCalibration> list = dataCalibrationService.selectDataCalibrationList(dataCalibration);
        return getDataTable(list);
    }

    /**
     * 导出数据校准管理列表
     */
    @PreAuthorize("@ss.hasPermi('runda:calibration:export')")
    @Log(title = "数据校准管理", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, DataCalibration dataCalibration)
    {
        List<DataCalibration> list = dataCalibrationService.selectDataCalibrationList(dataCalibration);
        ExcelUtil<DataCalibration> util = new ExcelUtil<DataCalibration>(DataCalibration.class);
        util.exportExcel(response, list, "数据校准管理数据");
    }

    /**
     * 获取数据校准管理详细信息
     */
    @PreAuthorize("@ss.hasPermi('runda:calibration:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(dataCalibrationService.selectDataCalibrationById(id));
    }

    /**
     * 新增数据校准管理
     */
    @PreAuthorize("@ss.hasPermi('runda:calibration:add')")
    @Log(title = "数据校准管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody DataCalibration dataCalibration)
    {
        return toAjax(dataCalibrationService.insertDataCalibration(dataCalibration));
    }

    /**
     * 修改数据校准管理
     */
    @PreAuthorize("@ss.hasPermi('runda:calibration:edit')")
    @Log(title = "数据校准管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody DataCalibration dataCalibration)
    {
        return toAjax(dataCalibrationService.updateDataCalibration(dataCalibration));
    }

    /**
     * 删除数据校准管理
     */
    @PreAuthorize("@ss.hasPermi('runda:calibration:remove')")
    @Log(title = "数据校准管理", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(dataCalibrationService.deleteDataCalibrationByIds(ids));
    }
}
