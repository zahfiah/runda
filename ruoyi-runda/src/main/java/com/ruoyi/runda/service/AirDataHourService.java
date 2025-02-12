package com.ruoyi.runda.service;

import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.runda.domain.AirDataHour;
import org.springframework.data.domain.Pageable;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import java.util.Map;


public interface AirDataHourService {



    TableDataInfo calculateAverageForSpecificDateTime(String dateTime)throws Exception;


    TableDataInfo calculateDailyHourlyAverage(String date, String deviceId) throws Exception;

    TableDataInfo calculateHourlyAverageForSpecificTime(String dateTime, String deviceId) throws Exception;


    void saveToMysql(List<Map<String, Object>> data) throws ParseException;

    void exportToExcel(HttpServletResponse response, List<AirDataHour> dataList) throws IOException;

    TableDataInfo calculateDailyAveragePm25AndPm10ForAllDevices(String date) throws Exception;
}