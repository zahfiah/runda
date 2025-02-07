package com.ruoyi.runda.controller;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.runda.service.AirDataHourService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;

@RestController
@RequestMapping("/runda/air")
public class AirDataController extends BaseController {
    private static final Logger logger = LoggerFactory.getLogger(AirDataController.class);

    @Autowired
    private AirDataHourService airDataHourService;


    @GetMapping("/average-by-hour")
    public TableDataInfo averageByDateTime(@RequestParam String dateTime) throws Exception {
        return airDataHourService.calculateAverageForSpecificDateTime(dateTime);
    }

    @GetMapping("/daily-hourly-average")
    public TableDataInfo dailyHourlyAverage(@RequestParam String date, @RequestParam String deviceId) throws Exception {
        return airDataHourService.calculateDailyHourlyAverage(date, deviceId);
    }

    @GetMapping("/hourly-average-for-specific-time")
    public TableDataInfo hourlyAverageForSpecificTime(@RequestParam String dateTime, @RequestParam String deviceId) throws Exception {
        return airDataHourService.calculateHourlyAverageForSpecificTime(dateTime, deviceId);
    }

    @GetMapping("/daily-average-pm25-and-pm10")
    public TableDataInfo dailyAveragePm25AndPm10(@RequestParam String date, @RequestParam String deviceId) throws Exception {
        return airDataHourService.calculateDailyAveragePm25AndPm10(date, deviceId);
    }

    @GetMapping("/current-hour-average-all-devices")
    public TableDataInfo currentHourAverageForAllDevices() throws Exception {
        return airDataHourService.getCurrentHourAverageForAllDevices();
    }
}