package com.ruoyi.runda.service;


import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;

public interface DeviceDataService {
    /**
     * 原始设备数据信息
     * @param date
     * @return
     */
    TableDataInfo listDeviceData(String date);
}
