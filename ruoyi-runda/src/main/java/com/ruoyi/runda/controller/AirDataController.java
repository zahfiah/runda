package com.ruoyi.runda.controller;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.runda.domain.AirDataHour;
import com.ruoyi.runda.domain.DataQuery212;
import com.ruoyi.runda.service.AirDataHourService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/runda/air")
public class AirDataController extends BaseController {
    private static final Logger logger = LoggerFactory.getLogger(AirDataController.class);

    @Autowired
    private AirDataHourService airDataHourService;

    @GetMapping("/average-by-hour")
    public TableDataInfo averageByDateTime(@RequestParam String dateTime) throws Exception {
        TableDataInfo tableDataInfo = airDataHourService.calculateAverageForSpecificDateTime(dateTime);
        List<Map<String, Object>> data = (List<Map<String, Object>>) tableDataInfo.getRows();
        airDataHourService.saveToMysql(data);
        return tableDataInfo;
    }

    @GetMapping("/export")
    public void exportData(@RequestParam String dateTime, HttpServletResponse response) throws Exception {
        TableDataInfo tableDataInfo = airDataHourService.calculateAverageForSpecificDateTime(dateTime);
        List<Map<String, Object>> mapList = (List<Map<String, Object>>) tableDataInfo.getRows();
        List<AirDataHour> dataList = convertMapListToAirDataHourList(mapList);
        airDataHourService.exportToExcel(response, dataList);
    }

    @GetMapping("/daily-hourly-average")
    public TableDataInfo dailyHourlyAverage(@RequestParam String date, @RequestParam String deviceId) throws Exception {
        return airDataHourService.calculateDailyHourlyAverage(date, deviceId);
    }

    @GetMapping("/hourly-average-for-specific-time")
    public TableDataInfo hourlyAverageForSpecificTime(@RequestParam String dateTime, @RequestParam String deviceId) throws Exception {
        return airDataHourService.calculateHourlyAverageForSpecificTime(dateTime, deviceId);
    }
    @GetMapping("/export2")
    public void exportData(@RequestParam String dateTime,@RequestParam String deviceId, HttpServletResponse response) throws Exception {
        TableDataInfo tableDataInfo = airDataHourService.calculateHourlyAverageForSpecificTime(dateTime, deviceId);
        List<Map<String, Object>> mapList = (List<Map<String, Object>>) tableDataInfo.getRows();
        List<AirDataHour> dataList = convertMapListToAirDataHourList(mapList);
        airDataHourService.exportToExcel(response, dataList);
    }


    @GetMapping("/daily-average-pm25-and-pm10")
    public TableDataInfo dailyAveragePm25AndPm10(@RequestParam String date) throws Exception {
        return airDataHourService.calculateDailyAveragePm25AndPm10ForAllDevices(date);
    }


    private List<AirDataHour> convertMapListToAirDataHourList(List<Map<String, Object>> mapList) {
        List<AirDataHour> dataList = new ArrayList<>();
        // 定义日期格式
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        for (Map<String, Object> map : mapList) {
            AirDataHour airDataHour = new AirDataHour();

            // 将字符串日期转换为 Date 对象
            String dateTimeStr = String.valueOf(map.get("dateTimeStr"));
            LocalDateTime localDateTime = LocalDateTime.parse(dateTimeStr, formatter);
            Date createDate = java.sql.Timestamp.valueOf(localDateTime);
            airDataHour.setCreateDate(createDate);
            airDataHour.setDeviceId(String.valueOf(map.get("deviceId")));
            airDataHour.setStationId(String.valueOf(map.get("stationId")));
            airDataHour.setRanking((Integer) map.get("ranking"));
            airDataHour.setAqi((Double) map.get("aqi"));
            airDataHour.setPm10((Double) map.get("averagePm10"));
            airDataHour.setPm25((Double) map.get("averagePm25"));
            airDataHour.setSo2Thickness((Double) map.get("averageSo2"));
            airDataHour.setCoThickness((Double) map.get("averageO3"));
            airDataHour.setNo2Thickness((Double) map.get("averageNo2"));
            airDataHour.setPrimaryPollutant(String.valueOf(map.get("primaryPollutant")));
            airDataHour.setQuality(String.valueOf(map.get("quality")));

            dataList.add(airDataHour);
        }
        return dataList;
    }
}
