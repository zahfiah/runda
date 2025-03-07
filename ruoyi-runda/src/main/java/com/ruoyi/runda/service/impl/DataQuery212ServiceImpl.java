package com.ruoyi.runda.service.impl;

import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.runda.domain.DataQuery212;
import com.ruoyi.runda.domain.Device;
import com.ruoyi.runda.mapper.DeviceMapper;
import com.ruoyi.runda.repository.DataQuery212OVRepository;
import com.ruoyi.runda.repository.DataQuery212Repository;
import com.ruoyi.runda.service.DataQuery212Service;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

@Service
public class DataQuery212ServiceImpl implements DataQuery212Service {
    private final List<DataQuery212> latestData = Collections.synchronizedList(new ArrayList<>());

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final Logger logger = LoggerFactory.getLogger(DataQuery212ServiceImpl.class);

    @Autowired
    private DeviceMapper deviceMapper;

    @Autowired
    private DataQuery212OVRepository dataQuery212OVRepository;


    @Override
    public TableDataInfo selectDataQuery212ListByDeviceId(String deviceId, int page, int size) {
        try {
            // 创建分页对象
            Pageable pageable = PageRequest.of(page - 1, size);
            logger.debug("Device ID: {}, Page: {}, Size: {}", deviceId, page, size);
            // 调用 DataQuery212Overwrite 接口的 findByDeviceId 方法，获取转换后的 DataQuery212 数据
            Page<DataQuery212> dataQuery212Page = dataQuery212OVRepository.findByDeviceId(deviceId, pageable);
            // 封装返回结果
            TableDataInfo result = new TableDataInfo();
            result.setCode(0);
            result.setMsg("ok");
            result.setTotal(dataQuery212Page.getTotalElements());
            result.setRows(dataQuery212Page.getContent());

            return result;
        } catch (Exception e) {
            // 异常处理
            TableDataInfo errorResult = new TableDataInfo();
            errorResult.setCode(-1);
            errorResult.setMsg(e.getMessage());
            return errorResult;
        }
    }



    @Cacheable(value = "dataQuery212Cache", key = "#dateStr + '_' + #page + '_' + #size")
    public TableDataInfo getCachedDataQuery212ListByDate(String dateStr, int page, int size) {
        try {
            long startTimestamp = parseDateToTimestamp(dateStr);
            long endTimestamp = startTimestamp + 86400000L; // 24小时
            Page<DataQuery212> dataQuery212Page = dataQuery212OVRepository.findByCreateDateBetween(startTimestamp, endTimestamp, PageRequest.of(page - 1, size));
            return buildTableDataInfo(dataQuery212Page);
        } catch (Exception e) {
            return buildErrorResult(e.getMessage());
        }
    }

    @Override
    public TableDataInfo selectDataQuery212ListByDate(String dateStr, int page, int size) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            dateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai")); // 明确指定时区
            Date startDate = dateFormat.parse(dateStr);
            long startTimestamp = startDate.getTime();
            long endTimestamp = startTimestamp + 24 * 60 * 60 * 1000L - 1;
            Pageable pageable = PageRequest.of(page - 1, size);
            logger.debug("startTimestamp: {}, endTimestamp: {}", startTimestamp, endTimestamp);

            // 查询 AirDataResult 数据
            Page<DataQuery212> dataQuery212Page = dataQuery212OVRepository.findByCreateDateBetween(startTimestamp, endTimestamp, pageable);



            logger.debug("Total number of records found: {}", dataQuery212Page.getTotalElements());
            if (logger.isDebugEnabled()) {
                for (DataQuery212 data : dataQuery212Page.getContent()) {
                    logger.debug("DataQuery212: {}", data);
                }
            }

            TableDataInfo result = new TableDataInfo();
            result.setCode(0);
            result.setMsg("ok");
            result.setTotal(dataQuery212Page.getTotalElements());
            result.setRows(dataQuery212Page.getContent());
            return result;
        } catch (ParseException e) {
            logger.error("Error parsing date", e);
            TableDataInfo errorResult = new TableDataInfo();
            errorResult.setCode(-1);
            errorResult.setMsg("Invalid date format. Please use yyyy-MM-dd.");
            return errorResult;
        } catch (Exception e) {
            logger.error("Error while fetching data", e);
            TableDataInfo errorResult = new TableDataInfo();
            errorResult.setCode(-1);
            errorResult.setMsg(e.getMessage());
            return errorResult;
        }
    }


    public void exportToExcel(HttpServletResponse response, List<DataQuery212> dataList) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Data");

        // 创建标题行
        Row headerRow = sheet.createRow(0);
        String[] headers = {"DeviceId", "DeviceName", "StationId", "StationName", "sn", "temperature", "humidity", "windSpeed", "windDirectionString",
                "pressure", "dust", "pm10", "longitude", "latitude", "aqi", "primaryPollutant", "CreateDate", "DeptId"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
        }

        // 填充数据
        int rowNum = 1;
        for (DataQuery212 data : dataList) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(data.getDeviceId());
            row.createCell(1).setCellValue(data.getDeviceName());
            row.createCell(2).setCellValue(data.getStationId());
            row.createCell(3).setCellValue(data.getStationName() != null ? data.getStationName() : "");
            row.createCell(4).setCellValue(data.getSn() != null ? data.getSn() : "");
            row.createCell(5).setCellValue(data.getTemperature() != null ? data.getTemperature().toString() : "");
            row.createCell(6).setCellValue(data.getHumidity() != null ? data.getHumidity().toString() : "");
            row.createCell(7).setCellValue(data.getWindSpeed() != null ? data.getWindSpeed().toString() : "");
            row.createCell(8).setCellValue(data.getWindDirectionString() != null ? data.getWindDirectionString() : "");
            row.createCell(9).setCellValue(data.getPressure() != null ? data.getPressure().toString() : "");
            row.createCell(10).setCellValue(data.getPm2_5() != null ? data.getPm2_5().toString() : "");
            row.createCell(11).setCellValue(data.getPm10() != null ? data.getPm10().toString() : "");
            row.createCell(12).setCellValue(data.getLongitude() != null ? data.getLongitude().toString() : "");
            row.createCell(13).setCellValue(data.getLatitude() != null ? data.getLatitude().toString() : "");
            row.createCell(14).setCellValue(data.getAqi() != null ? data.getAqi().toString() : "");
            row.createCell(15).setCellValue(data.getPrimaryPollutant() != null ? data.getPrimaryPollutant() : "");
            row.createCell(16).setCellValue(data.getCreateDate() != null ? data.getCreateDate().toString() : "");
            row.createCell(17).setCellValue(data.getDeptId());
        }

        // 自动调整列宽
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }

        // 设置响应头信息
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=data.xlsx");

        // 将工作簿写入响应输出流
        try (OutputStream outputStream = response.getOutputStream()) {
            workbook.write(outputStream);
        }
        workbook.close();
    }

    @Override
    public List<Device> listDeviceIdAndName() {
        return deviceMapper.selectIdAndName();
    }

    @Override
    public TableDataInfo selectDataQuery212ListByDateAndDeviceId(String deviceId, String date, int page, int size) {
        try {
            // 检查 page 和 size 的有效性
            if (page <= 0 || size <= 0) {
                return createErrorResult("Invalid page or size parameters");
            }

            // 使用线程安全的日期时间类
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd").withZone(ZoneId.of("Asia/Shanghai"));
            LocalDate localDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            ZonedDateTime startOfDay = localDate.atStartOfDay(ZoneId.of("Asia/Shanghai"));
            ZonedDateTime endOfDay = localDate.plusDays(1).atStartOfDay(ZoneId.of("Asia/Shanghai"));

            long startTimestamp = startOfDay.toInstant().toEpochMilli();
            long endTimestamp = endOfDay.toInstant().toEpochMilli();


            Pageable pageable = PageRequest.of(page - 1, size);
            logger.debug("deviceId: {}, startTimestamp: {}, endTimestamp: {}", deviceId, startTimestamp, endTimestamp);

            Page<DataQuery212> dataQuery212Page = dataQuery212OVRepository.findByDeviceIdAndCreateDateBetween(deviceId, startTimestamp, endTimestamp, pageable);

            // 打印查询到的数据条数
            logger.debug("Total number of records found: {}", dataQuery212Page.getTotalElements());

            // 打印查询到的数据
            if (logger.isDebugEnabled()) {
                for (DataQuery212 data : dataQuery212Page.getContent()) {
                    logger.debug("DataQuery212: {}", data);
                }
            }

            TableDataInfo result = new TableDataInfo();
            result.setCode(0);
            result.setMsg("ok");
            result.setTotal(dataQuery212Page.getTotalElements());
            result.setRows(dataQuery212Page.getContent());

            return result;
        } catch (DateTimeParseException e) {
            logger.error("Error parsing date", e);
            return createErrorResult("Invalid date format. Please use yyyy-MM-dd.");
        } catch (Exception e) {
            logger.error("Error while fetching data", e);
            return createErrorResult(e.getMessage());
        }

    }

    private TableDataInfo createErrorResult(String msg) {
        TableDataInfo errorResult = new TableDataInfo();
        errorResult.setCode(-1);
        errorResult.setMsg(msg);
        return errorResult;
    }
    @Override
    public TableDataInfo selectDataQuery212ListByDateTimeRange(String startDateTimeStr, String endDateTimeStr, int page, int size) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            dateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai")); // 明确指定时区
            Date startDate = dateFormat.parse(startDateTimeStr);
            Date endDate = dateFormat.parse(endDateTimeStr);

            long startTimestamp = startDate.getTime();
            long endTimestamp = endDate.getTime();

            Pageable pageable = PageRequest.of(page - 1, size);
            logger.debug("startTimestamp: {}, endTimestamp: {}", startTimestamp, endTimestamp);


            Page<DataQuery212> dataQuery212Page = dataQuery212OVRepository.findByCreateDateBetween(startTimestamp, endTimestamp, pageable);



            logger.debug("Total number of records found: {}", dataQuery212Page.getTotalElements());
            if (logger.isDebugEnabled()) {
                for (DataQuery212 data : dataQuery212Page.getContent()) {
                    logger.debug("DataQuery212: {}", data);
                }
            }

            TableDataInfo result = new TableDataInfo();
            result.setCode(0);
            result.setMsg("ok");
            result.setTotal(dataQuery212Page.getTotalElements());
            result.setRows(dataQuery212Page.getContent());
            return result;
        } catch (ParseException e) {
            logger.error("Error parsing date time", e);
            TableDataInfo errorResult = new TableDataInfo();
            errorResult.setCode(-1);
            errorResult.setMsg("Invalid date time format. Please use yyyy-MM-dd HH:mm.");
            return errorResult;
        } catch (Exception e) {
            logger.error("Error while fetching data", e);
            TableDataInfo errorResult = new TableDataInfo();
            errorResult.setCode(-1);
            errorResult.setMsg(e.getMessage());
            return errorResult;
        }
    }


    @Override
    public List<DataQuery212> fetchLatestData() {
        try {
            Instant now = Instant.now();
            Instant fiveMinutesAgo = now.minus(Duration.ofMinutes(5));

            int pageSize = 10;
            int pageNumber = 0;

            List<DataQuery212> newData = new ArrayList<>();

            while (true) {
                Pageable pageable = PageRequest.of(pageNumber, pageSize);
                Page<DataQuery212> dataQuery212Page = dataQuery212OVRepository.findByCreateDateBetween(fiveMinutesAgo.toEpochMilli(), now.toEpochMilli(), pageable);


                if (!dataQuery212Page.hasNext()) {
                    break;
                }
                pageNumber++;
            }

            if (newData.isEmpty()) {
                logger.info("No new data fetched.");
            } else {
                logger.info("Fetched {} new records.", newData.size());
                latestData.addAll(newData);
            }

            return new ArrayList<>(latestData);
        } catch (Exception e) {
            logger.error("Error fetching latest data", e);
            return Collections.emptyList();
        }
    }




    @Scheduled(fixedRate = 300000)
    public void scheduledFetchAndAppendData() {
        try {
            logger.info("Starting scheduled fetch and append data task...");
            fetchLatestData();
            logger.info("Total number of records fetched: {}", latestData.size());
        } catch (Exception e) {
            logger.error("Error during scheduled task", e);
        }
    }

    @Override
    public TableDataInfo selectDataQuery212ListByDateTimeRangeAndDeviceId(
            String deviceId,
            String startDateTimeStr,
            String endDateTimeStr,
            int page,
            int size) {

        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            dateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai")); // 明确指定时区
            Date startDate = dateFormat.parse(startDateTimeStr);
            Date endDate = dateFormat.parse(endDateTimeStr);

            long startTimestamp = startDate.getTime();
            long endTimestamp = endDate.getTime();

            Pageable pageable = PageRequest.of(page - 1, size);
            logger.debug("deviceId: {}, startTimestamp: {}, endTimestamp: {}", deviceId, startTimestamp, endTimestamp);


            Page<DataQuery212> dataQuery212Page = dataQuery212OVRepository.findByDeviceIdAndCreateDateBetween(deviceId, startTimestamp, endTimestamp, pageable);

            // 打印查询到的数据条数
            logger.debug("Total number of records found: {}", dataQuery212Page.getTotalElements());

            // 打印查询到的数据
            if (logger.isDebugEnabled()) {
                for (DataQuery212 data : dataQuery212Page.getContent()) {
                    logger.debug("DataQuery212: {}", data);
                }
            }

            TableDataInfo result = new TableDataInfo();
            result.setCode(0);
            result.setMsg("ok");
            result.setTotal(dataQuery212Page.getTotalElements());
            result.setRows(dataQuery212Page.getContent());

            return result;
        } catch (ParseException e) {
            logger.error("Error parsing date", e);
            TableDataInfo errorResult = new TableDataInfo();
            errorResult.setCode(-1);
            errorResult.setMsg("Invalid date format. Please use yyyy-MM-dd HH:mm.");
            return errorResult;
        } catch (Exception e) {
            logger.error("Error while fetching data", e);
            TableDataInfo errorResult = new TableDataInfo();
            errorResult.setCode(-1);
            errorResult.setMsg(e.getMessage());
            return errorResult;
        }
    }

    // 构建成功响应
    private TableDataInfo buildTableDataInfo(Page<DataQuery212> dataQuery212Page) {
        TableDataInfo result = new TableDataInfo();
        result.setCode(0);
        result.setMsg("ok");
        result.setTotal(dataQuery212Page.getTotalElements());
        result.setRows(dataQuery212Page.getContent());
        return result;
    }

    // 构建错误响应
    private TableDataInfo buildErrorResult(String errorMessage) {
        TableDataInfo result = new TableDataInfo();
        result.setCode(-1);
        result.setMsg(errorMessage);
        return result;
    }

    // 将日期字符串解析为时间戳
    private long parseDateToTimestamp(String dateStr) {
        // 实现日期解析逻辑
        return 0L;
    }
}




