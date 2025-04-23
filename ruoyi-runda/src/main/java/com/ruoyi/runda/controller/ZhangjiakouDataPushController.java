package com.ruoyi.runda.controller;

import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.runda.domain.FilteredAirDataDTO;
import com.ruoyi.runda.domain.HourlyAverageAirData;
import com.ruoyi.runda.domain.HourlyAverageAirDataCopy;
import com.ruoyi.runda.service.DataPushService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@Slf4j
@RequestMapping("/zjk/dataPush")
public class ZhangjiakouDataPushController {

    @Autowired
    private DataPushService dataPushService;

    @PostMapping("/pushData")
//    @Scheduled(cron = "0 20 * * * ?")
    public AjaxResult pushData() {
        try {
            LocalDateTime nowDate = LocalDateTime.now();
            LocalDateTime previousHour = nowDate.minusHours(1);
            String dateTimeStr = previousHour.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:00:00"));

            log.info("查询时间条件: {}", dateTimeStr);
            List<HourlyAverageAirDataCopy> originalDataList = dataPushService.getHourlyAverageAirData(dateTimeStr);
            log.info("原始数据量: {}", originalDataList.size());

            // 过滤数据 - 只保留需要的字段
            List<FilteredAirDataDTO> filteredDataList = originalDataList.stream()
                    .map(data -> {
                        FilteredAirDataDTO filteredData = new FilteredAirDataDTO();
                        filteredData.setSn(data.getSn());
                        filteredData.setAveragePm10(data.getAveragePm10());
                        filteredData.setCreatedAt(data.getCreatedAt());
                        return filteredData;
                    })
                    .collect(Collectors.toList());

            log.info("过滤后数据量: {}", filteredDataList.size());
            filteredDataList.stream()
                    .limit(5)
                    .forEach(data -> log.info("过滤后数据示例 - SN: {}, PM10: {}, 时间: {}",
                            data.getSn(), data.getAveragePm10(), data.getCreatedAt()));


            if (filteredDataList.isEmpty()) {
                return AjaxResult.error("没有可推送的数据");
            }

            boolean pushResult = dataPushService.batchPushAirData(filteredDataList);

            if (pushResult) {
                return AjaxResult.success("数据推送成功", filteredDataList);
            } else {
                return AjaxResult.error("数据推送失败", filteredDataList);
            }
        } catch (Exception e) {
            log.error("数据推送异常", e);
            return AjaxResult.error("数据推送异常: " + e.getMessage());
        }
    }
}