package com.ruoyi.runda.service;

import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.runda.domain.DataQuery212;
import com.ruoyi.runda.domain.Device;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public interface DataQuery212Service {

    TableDataInfo selectDataQuery212ListByDeviceId(String deviceId, int page, int size);


    TableDataInfo selectDataQuery212ListByDate(String date, int page, int size);

    TableDataInfo selectDataQuery212ListByDateTimeRange(String startDateTimeStr, String endDateTimeStr, int page, int size);

    List<DataQuery212> fetchLatestData();

    TableDataInfo selectDataQuery212ListByDateTimeRangeAndDeviceId(String deviceId, String startDateTime, String endDateTime, int page, int size);

    TableDataInfo selectDataQuery212ListByDateAndDeviceId(String deviceId, String date, int page, int size);

    void exportToExcel(HttpServletResponse response, List<DataQuery212> dataList) throws IOException;

    List<Device> listDeviceIdAndName();
}
