package com.ruoyi.runda.controller;

import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.runda.domain.HourlyAverageAirData;
import com.ruoyi.runda.service.DataPushService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/zjk/dataPush")
public class ZhangjiakouDataPushController {

    @Autowired
    private DataPushService dataPushService;

    /**
     * 推送小时平均空气数据到指定接口
     * @return 包含推送结果和数据的AjaxResult
     */
    @PostMapping("/pushData")
    public AjaxResult pushData() {
        try {
            // 1. 获取小时平均空气数据
            List<HourlyAverageAirData> dataList = dataPushService.getHourlyAverageAirData();

            if (dataList == null || dataList.isEmpty()) {
                return AjaxResult.error("没有可推送的数据");
            }

            // 2. 批量推送数据到接口
            boolean pushResult = dataPushService.batchPushAirData(dataList);

            // 3. 返回结果
            if (pushResult) {
                return AjaxResult.success("数据推送成功", dataList);
            } else {
                return AjaxResult.error("数据推送失败", dataList);
            }
        } catch (Exception e) {
            return AjaxResult.error("数据推送异常: " + e.getMessage());
        }
    }
}