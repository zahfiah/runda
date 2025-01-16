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
import com.ruoyi.runda.domain.Device;
import com.ruoyi.runda.service.IDeviceService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 监测设备管理设备Controller
 * 
 * @author runda
 * @date 2025-01-11
 */
@RestController
@RequestMapping("/runda/device")
public class DeviceController extends BaseController
{
    @Autowired
    private IDeviceService deviceService;

    /**
     * 查询监测设备管理设备列表
     */
    @PreAuthorize("@ss.hasPermi('runda:device:list')")
    @GetMapping("/list")
    public TableDataInfo list(Device device)
    {
        startPage();
        List<Device> list = deviceService.selectDeviceList(device);
        return getDataTable(list);
    }

    /**
     * 导出监测设备管理设备列表
     */
    @PreAuthorize("@ss.hasPermi('runda:device:export')")
    @Log(title = "监测设备管理设备", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, Device device)
    {
        List<Device> list = deviceService.selectDeviceList(device);
        ExcelUtil<Device> util = new ExcelUtil<Device>(Device.class);
        util.exportExcel(response, list, "监测设备管理设备数据");
    }

    /**
     * 获取监测设备管理设备详细信息
     */
    @PreAuthorize("@ss.hasPermi('runda:device:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(deviceService.selectDeviceById(id));
    }

    /**
     * 新增监测设备管理设备
     */
    @PreAuthorize("@ss.hasPermi('runda:device:add')")
    @Log(title = "监测设备管理设备", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody Device device)
    {
        return toAjax(deviceService.insertDevice(device));
    }

    /**
     * 修改监测设备管理设备
     */
    @PreAuthorize("@ss.hasPermi('runda:device:edit')")
    @Log(title = "监测设备管理设备", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody Device device)
    {
        return toAjax(deviceService.updateDevice(device));
    }

    /**
     * 删除监测设备管理设备
     */
    @PreAuthorize("@ss.hasPermi('runda:device:remove')")
    @Log(title = "监测设备管理设备", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(deviceService.deleteDeviceByIds(ids));
    }
}
