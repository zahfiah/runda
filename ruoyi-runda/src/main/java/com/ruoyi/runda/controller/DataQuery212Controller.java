package com.ruoyi.runda.controller;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.runda.domain.DataQuery212;
import com.ruoyi.runda.domain.Device;
import com.ruoyi.runda.service.DataQuery212Service;
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
import java.util.List;


@RestController
@RequestMapping("/runda/query212")
public class DataQuery212Controller extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(DataQuery212Controller.class);

    @Autowired
    private DataQuery212Service dataQuery212Service;


    /**
     * 根据 stationId 获取大气数据查询列表
     *
     * @param deviceId 站点ID
     * @param page 页码，默认为1
     * @param size 每页大小，默认为10
     * @return 表格数据信息
     */
    @GetMapping("/listByDeviceId")
    public TableDataInfo listByDeviceId(
            @RequestParam("deviceId") String deviceId,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {

        // 打印查询条件
        logger.debug("deviceId: {}",deviceId);

        // 直接返回服务层的方法结果
        return dataQuery212Service.selectDataQuery212ListByDeviceId(deviceId, page, size);
    }


    /**
     * 根据 createDate 获取大气数据查询列表
     *
     * @param date 日期字符串，格式为 yyyy-MM-dd
     * @param page 页码，默认为1
     * @param size 每页大小，默认为10
     * @return 表格数据信息
     */
    @GetMapping("/listByDate")
    public TableDataInfo listByDate(
            @RequestParam("date") String date,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {

        // 打印查询条件
        logger.debug("date: {}", date);

        // 直接返回服务层的方法结果
        return dataQuery212Service.selectDataQuery212ListByDate(date, page, size);
    }


    /**
     * 查询日期这天的小时区间数据
     * @param startDateTime
     * @param endDateTime
     * @param page
     * @param size
     * @return
     */
    @GetMapping("/listByDateTimeRange")
    public TableDataInfo listByDateTimeRange(
            @RequestParam("startDateTime") String startDateTime,
            @RequestParam("endDateTime") String endDateTime,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        logger.debug("startDateTime: {}, endDateTime: {}", startDateTime, endDateTime);
        return dataQuery212Service.selectDataQuery212ListByDateTimeRange(startDateTime, endDateTime, page, size);
    }


    @GetMapping("/export")
    public void exportData(@RequestParam String startDateTime, @RequestParam String endDateTime,
                           @RequestParam int page, @RequestParam int size, HttpServletResponse response) throws IOException, ParseException {
        // 查询数据
        TableDataInfo tableDataInfo = dataQuery212Service.selectDataQuery212ListByDateTimeRange(startDateTime, endDateTime, page, size);

        // 提取数据列表
        List<DataQuery212> dataList = (List<DataQuery212>) tableDataInfo.getRows();

        // 导出为Excel
        dataQuery212Service.exportToExcel(response, dataList);
    }
    @GetMapping("/list")
    public List<DataQuery212> listCurrentDayData() {
        logger.debug("Fetching current day data...");
        return dataQuery212Service.fetchLatestData();
    }

    @GetMapping("/listByDateTimeRangeAndDeviceId")
    public TableDataInfo listByDateTimeRangeAndDeviceId(
            @RequestParam("deviceId") String deviceId,
            @RequestParam("startDateTime") String startDateTime,
            @RequestParam("endDateTime") String endDateTime,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {

        return dataQuery212Service.selectDataQuery212ListByDateTimeRangeAndDeviceId(deviceId, startDateTime, endDateTime, page, size);
    }

    @GetMapping("/export2")
    public void exportData( @RequestParam("deviceId") String deviceId,
                            @RequestParam("startDateTime") String startDateTime,
                            @RequestParam("endDateTime") String endDateTime,
                            @RequestParam(value = "page", defaultValue = "1") int page,
                            @RequestParam(value = "size", defaultValue = "10") int size, HttpServletResponse response) throws IOException, ParseException {
        // 查询数据
        TableDataInfo tableDataInfo =dataQuery212Service.selectDataQuery212ListByDateTimeRangeAndDeviceId(deviceId, startDateTime, endDateTime, page, size);

        // 提取数据列表
        List<DataQuery212> dataList = (List<DataQuery212>) tableDataInfo.getRows();

        // 导出为Excel
        dataQuery212Service.exportToExcel(response, dataList);
    }

    @GetMapping("/listByDateAndDeviceId")
    public TableDataInfo listByDateAndDeviceId(
            @RequestParam("deviceId") String deviceId,
            @RequestParam("date") String date,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {

        return dataQuery212Service.selectDataQuery212ListByDateAndDeviceId(deviceId, date, page, size);
    }

    //查询设备id与设备名称
    @GetMapping("/listDeviceIdAndName")
    public List<Device> listDeviceIdAndName(){
        return dataQuery212Service.listDeviceIdAndName();
    }
}



