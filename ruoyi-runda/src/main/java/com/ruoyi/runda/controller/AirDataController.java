package com.ruoyi.runda.controller;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.runda.domain.AirDataHour;
import com.ruoyi.runda.domain.DataQuery212;
import com.ruoyi.runda.domain.Device;
import com.ruoyi.runda.domain.HourlyAverageAirData;
import com.ruoyi.runda.repository.HourlyAverageAirDataRepository;
import com.ruoyi.runda.service.AirDataHourService;
import com.ruoyi.runda.service.IHourlyAverageAirDataService;
import com.ruoyi.runda.service.impl.HourlyAverageAirDataServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
@RequestMapping("/runda/air")
public class AirDataController extends BaseController {
    private static final Logger logger = LoggerFactory.getLogger(AirDataController.class);

    @Autowired
    private AirDataHourService airDataHourService;
    @Autowired
    private HourlyAverageAirDataRepository hourlyAverageAirDataRepository;
    @GetMapping("/average-by-hour")
    public TableDataInfo averageByDateTime(@RequestParam String dateTime) throws Exception {
        TableDataInfo tableDataInfo = airDataHourService.calculateAverageForSpecificDateTime(dateTime);
        List<Map<String, Object>> data = (List<Map<String, Object>>) tableDataInfo.getRows();
        return tableDataInfo;
    }

    @PostMapping("/export")
    public void exportData(@RequestParam Date beginTime,@RequestParam Date endTime, HttpServletResponse response) throws Exception {
        TimeZone shanghaiTimeZone = TimeZone.getTimeZone("Asia/Shanghai");
        TimeZone.setDefault(shanghaiTimeZone);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        dateFormat.setTimeZone(shanghaiTimeZone);


        List<HourlyAverageAirData> dataList = hourlyAverageAirDataRepository.findByDate(beginTime, endTime);

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
    @PostMapping("/export2")
    public void exportData(@RequestParam Date date,@RequestParam String deviceId, HttpServletResponse response) throws Exception {
        List<HourlyAverageAirData> dataList = hourlyAverageAirDataRepository.findByDateAndDeviceId(date, deviceId);
        airDataHourService.exportToExcel(response, dataList);
    }

    @GetMapping("/list-hour-data")
    public AjaxResult list(@RequestParam Date beginTime, @RequestParam Date endTime) throws ParseException {
        List<HourlyAverageAirData> list = airDataHourService.selectDataList(beginTime,endTime);
        return success(list);
    }

}
