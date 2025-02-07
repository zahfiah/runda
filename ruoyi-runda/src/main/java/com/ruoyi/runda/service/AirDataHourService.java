package com.ruoyi.runda.service;

import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.runda.domain.AirDataHour;
import org.springframework.data.domain.Pageable;

import java.text.ParseException;


public interface AirDataHourService {



    TableDataInfo calculateAverageForSpecificDateTime(String dateTime)throws Exception;


    TableDataInfo calculateDailyHourlyAverage(String date, String deviceId) throws Exception;

    TableDataInfo calculateHourlyAverageForSpecificTime(String dateTime, String deviceId) throws Exception;

    TableDataInfo calculateDailyAveragePm25AndPm10(String date, String deviceId) throws Exception;

    TableDataInfo getCurrentHourAverageForAllDevices() throws Exception;
}