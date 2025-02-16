package com.ruoyi.runda.service.impl;

import com.ruoyi.runda.domain.DataQuery212;
import com.ruoyi.runda.repository.DataQuery212Repository;
import com.ruoyi.runda.service.DataQuery212Service;
import com.ruoyi.common.core.page.TableDataInfo;
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
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.concurrent.TimeUnit;


@Service
public class DataQuery212ServiceImpl implements DataQuery212Service {
    private final List<DataQuery212> latestData = Collections.synchronizedList(new ArrayList<>());

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final Logger logger = LoggerFactory.getLogger(DataQuery212ServiceImpl.class);
    @Autowired
    private DataQuery212Repository dataQuery212Repository; // 假设你有一个对应的仓库接口
    @Autowired
    private RedisTemplate<Object, Object> redisTemplate; // 更改为 <Object, Object>
    @Override
    public TableDataInfo selectDataQuery212ListByDeviceId(String deviceId, int page, int size) {
        try {
            // 创建 Pageable 对象时使用正确的类
            Pageable pageable = PageRequest.of(page - 1, size); // 注意：Spring Data 是0索引的

            // 打印查询条件
            logger.debug("deviceId: {}", deviceId);

            // 确保 findByStationId 方法签名正确使用 org.springframework.data.domain.Pageable
            Page<DataQuery212> dataPage = dataQuery212Repository.findByDeviceId(deviceId, pageable);

            // 打印查询到的数据条数
            logger.debug("Total number of records found: {}", dataPage.getTotalElements());

            // 打印查询到的数据
            if (logger.isDebugEnabled()) {
                for (DataQuery212 data : dataPage.getContent()) {
                    logger.debug("DataQuery212: {}", data);
                }
            }

            TableDataInfo result = new TableDataInfo();
            result.setCode(0);
            result.setMsg("ok");
            result.setTotal(dataPage.getTotalElements());
            result.setRows(dataPage.getContent());

            return result;
        } catch (Exception e) {
            logger.error("Error while fetching data", e);
            TableDataInfo errorResult = new TableDataInfo();
            errorResult.setCode(-1);
            errorResult.setMsg(e.getMessage());
            return errorResult;
        }
    }

    @Cacheable(value = "dataQuery212Cache", key = "#dateStr + '_' + #page + '_' + #size")
    public TableDataInfo getCachedDataQuery212ListByDate(String dateStr, int page, int size) throws ParseException {
        return selectDataQuery212ListByDate(dateStr, page, size);
    }

    @Override
    public TableDataInfo selectDataQuery212ListByDate(String dateStr, int page, int size) {
        try {
            // 解析日期字符串
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date startDate = dateFormat.parse(dateStr);
            long startTimestamp = startDate.getTime();

            // 计算结束时间（当天的最后一毫秒）
            long endTimestamp = startTimestamp + 24 * 60 * 60 * 1000L - 1;

            // 创建 Pageable 对象时使用正确的类
            Pageable pageable = PageRequest.of(page - 1, size); // 注意：Spring Data 是0索引的

            // 打印查询条件
            logger.debug("startTimestamp: {}, endTimestamp: {}", startTimestamp, endTimestamp);

            // 确保 findByCreateDateBetween 方法签名正确使用 org.springframework.data.domain.Pageable
            Page<DataQuery212> dataPage = dataQuery212Repository.findByCreateDateBetween(startTimestamp, endTimestamp, pageable);

            // 打印查询到的数据条数
            logger.debug("Total number of records found: {}", dataPage.getTotalElements());

            // 打印查询到的数据
            if (logger.isDebugEnabled()) {
                for (DataQuery212 data : dataPage.getContent()) {
                    logger.debug("DataQuery212: {}", data);
                }
            }

            TableDataInfo result = new TableDataInfo();
            result.setCode(0);
            result.setMsg("ok");
            result.setTotal(dataPage.getTotalElements());
            result.setRows(dataPage.getContent());

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
        String[] headers = {"DeviceId", "DeviceName","StationId","StationName", "sn","temperature","humidity","windSpeed","windDirectionString",
                "pressure","dust","pm10","longitude","latitude","aqi","primaryPollutant","CreateDate","DeptId"};
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
            row.createCell(10).setCellValue(data.getDust() != null ? data.getDust().toString() : "");
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
    public TableDataInfo selectDataQuery212ListByDateTimeRange(String startDateTimeStr, String endDateTimeStr, int page, int size) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            Date startDate = dateFormat.parse(startDateTimeStr);
            Date endDate = dateFormat.parse(endDateTimeStr);

            long startTimestamp = startDate.getTime();
            long endTimestamp = endDate.getTime();

            // 创建 Pageable 对象时使用正确的类
            Pageable pageable = PageRequest.of(page - 1, size); // 注意：Spring Data 是0索引的

            // 打印查询条件
            logger.debug("startTimestamp: {}, endTimestamp: {}", startTimestamp, endTimestamp);

            // 确保 findByCreateDateBetween 方法签名正确使用 org.springframework.data.domain.Pageable
            Page<DataQuery212> dataPage = dataQuery212Repository.findByCreateDateBetween(startTimestamp, endTimestamp, pageable);

            // 打印查询到的数据条数
            logger.debug("Total number of records found: {}", dataPage.getTotalElements());

            // 打印查询到的数据
            if (logger.isDebugEnabled()) {
                for (DataQuery212 data : dataPage.getContent()) {
                    logger.debug("DataQuery212: {}", data);
                }
            }

            TableDataInfo result = new TableDataInfo();
            result.setCode(0);
            result.setMsg("ok");
            result.setTotal(dataPage.getTotalElements());
            result.setRows(dataPage.getContent());

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

            // 设置每页大小
            int pageSize = 10; // 每页10条数据
            int pageNumber = 0; // 从第一页开始

            List<DataQuery212> newData = new ArrayList<>();

            while (true) {
                // 创建 Pageable 对象
                Pageable pageable = PageRequest.of(pageNumber, pageSize);

                // 查询最近5分钟的数据
                Page<DataQuery212> newDataPage = dataQuery212Repository.findByCreateDateBetween(fiveMinutesAgo.toEpochMilli(), now.toEpochMilli(), pageable);
                newData.addAll(newDataPage.getContent());

                // 检查是否有更多数据
                if (!newDataPage.hasNext()) {
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

            return new ArrayList<>(latestData); // 返回一个新的列表副本，避免外部修改
        } catch (Exception e) {
            logger.error("Error fetching latest data", e);
            return Collections.emptyList(); // 返回空列表以避免脏数据
        }
    }

    @Scheduled(fixedRate = 300000) // 每5分钟执行一次
    public void scheduledFetchAndAppendData() {
        try {
            logger.info("Starting scheduled fetch and append data task...");
            fetchLatestData();
            logger.info("Total number of records fetched: {}", latestData.size());
        } catch (Exception e) {
            logger.error("Error during scheduled task", e);
        }
    }

    public TableDataInfo selectDataQuery212ListByDateTimeRangeAndDeviceId(
            String deviceId,
            String startDateTimeStr,
            String endDateTimeStr,
            int page,
            int size) {

        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            Date startDate = dateFormat.parse(startDateTimeStr);
            Date endDate = dateFormat.parse(endDateTimeStr);

            long startTimestamp = startDate.getTime();
            long endTimestamp = endDate.getTime();

            // 创建 Pageable 对象时使用正确的类
            Pageable pageable = PageRequest.of(page - 1, size); // 注意：Spring Data 是0索引的

            // 打印查询条件
            logger.debug("deviceId: {}, startTimestamp: {}, endTimestamp: {}", deviceId, startTimestamp, endTimestamp);

            // 调用新增的组合查询方法
            Page<DataQuery212> dataPage = dataQuery212Repository.findByDeviceIdAndCreateDateBetween(deviceId, startTimestamp, endTimestamp, pageable);

            // 打印查询到的数据条数
            logger.debug("Total number of records found: {}", dataPage.getTotalElements());

            // 打印查询到的数据
            if (logger.isDebugEnabled()) {
                for (DataQuery212 data : dataPage.getContent()) {
                    logger.debug("DataQuery212: {}", data);
                }
            }

            TableDataInfo result = new TableDataInfo();
            result.setCode(0);
            result.setMsg("ok");
            result.setTotal(dataPage.getTotalElements());
            result.setRows(dataPage.getContent());

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

    public TableDataInfo selectDataQuery212ListByDateAndDeviceId(
            String deviceId,
            String dateStr,
            int page,
            int size) {

        try {
            // 解析日期字符串
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date startDate = dateFormat.parse(dateStr);
            long startTimestamp = startDate.getTime();

            // 计算结束时间（当天的最后一毫秒）
            long endTimestamp = startTimestamp + 24 * 60 * 60 * 1000L - 1;

            // 创建 Pageable 对象时使用正确的类
            Pageable pageable = PageRequest.of(page - 1, size); // 注意：Spring Data 是0索引的

            // 打印查询条件
            logger.debug("deviceId: {}, startTimestamp: {}, endTimestamp: {}", deviceId, startTimestamp, endTimestamp);

            // 调用新增的组合查询方法
            Page<DataQuery212> dataPage = dataQuery212Repository.findByDeviceIdAndCreateDateBetween(deviceId, startTimestamp, endTimestamp, pageable);

            // 打印查询到的数据条数
            logger.debug("Total number of records found: {}", dataPage.getTotalElements());

            // 打印查询到的数据
            if (logger.isDebugEnabled()) {
                for (DataQuery212 data : dataPage.getContent()) {
                    logger.debug("DataQuery212: {}", data);
                }
            }

            TableDataInfo result = new TableDataInfo();
            result.setCode(0);
            result.setMsg("ok");
            result.setTotal(dataPage.getTotalElements());
            result.setRows(dataPage.getContent());

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
}




