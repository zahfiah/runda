package com.ruoyi.runda.controller;


import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.runda.service.DeviceDataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/runda/deviceData")
public class DeviceDataController  extends BaseController{
    @Autowired
    private DeviceDataService deviceDataService;

    /**
     * 原始设备数据信息
     * @param date
     * @return
     */
    @GetMapping("/listDeviceData")
    public TableDataInfo listDeviceData(@RequestParam("date") String date) {
        return deviceDataService.listDeviceData(date);
    }
}
