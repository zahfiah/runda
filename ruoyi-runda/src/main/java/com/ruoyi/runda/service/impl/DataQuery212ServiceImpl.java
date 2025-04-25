package com.ruoyi.runda.service.impl;

import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.runda.domain.AirDataHour;
import com.ruoyi.runda.domain.DataQuery212;
import com.ruoyi.runda.domain.Device;
import com.ruoyi.runda.mapper.DeviceMapper;
import com.ruoyi.runda.repository.DataQuery212OVRepository;
import com.ruoyi.runda.repository.DataQuery212Repository;
import com.ruoyi.runda.repository.DeviceDataRepository;
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
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class DataQuery212ServiceImpl implements DataQuery212Service {
    private final List<DataQuery212> latestData = Collections.synchronizedList(new ArrayList<>());

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final Logger logger = LoggerFactory.getLogger(DataQuery212ServiceImpl.class);

    @Autowired
    private DeviceMapper deviceMapper;

    @Autowired
    private DataQuery212OVRepository dataQuery212OVRepository;

    @Autowired
    private DeviceDataRepository deviceDataRepository;
    @Override
    public TableDataInfo selectDataQuery212ListByDeviceId(String deviceId, int page, int size) {
        try {


            // 创建分页对象
            Pageable pageable = PageRequest.of(page - 1, size);

            String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

            // 使用线程安全的日期时间类
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate localDate = LocalDate.parse(date, formatter);
            ZoneId zoneId = ZoneId.systemDefault();
            Date startDate = Date.from(localDate.atStartOfDay(zoneId).toInstant());
            Date endDate = Date.from(localDate.atTime(23, 59, 59, 999_999_999).atZone(zoneId).toInstant());

            long startTimestamp = startDate.getTime();
            long endTimestamp = endDate.getTime();

            logger.debug("startTimestamp: {}, endTimestamp: {}", startTimestamp, endTimestamp);

            // 查询 AirDataResult 数据
            Page<DataQuery212> dataQuery212Page = dataQuery212OVRepository.findByCreateDateBetween(startTimestamp, endTimestamp, pageable);

            // 封装返回结果
            return buildTableDataInfo(dataQuery212Page);
        } catch (DateTimeParseException e) {
            logger.error("Error parsing date", e);
            return buildErrorResult("Invalid date format. Please use yyyy-MM-dd.");
        } catch (Exception e) {
            logger.error("Error while fetching data", e);
            return buildErrorResult(e.getMessage());
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
            // 设置时区为 Asia/Shanghai (UTC+8)
            TimeZone.setDefault(TimeZone.getTimeZone("Asia/Shanghai"));
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            dateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));

            // 解析日期字符串
            Date startDate = dateFormat.parse(dateStr);
            long startTimestamp = startDate.getTime();
            long endTimestamp = startTimestamp + 24 * 60 * 60 * 1000L - 1; // 当天 23:59:59

            // 获取当前中国时间戳
            ZoneId zoneId = ZoneId.of("Asia/Shanghai");
            ZonedDateTime zonedDateTime = ZonedDateTime.now(zoneId);
            long currentTimestamp = zonedDateTime.toInstant().toEpochMilli();

            // 如果结束时间大于当前时间，则使用当前时间作为结束时间
            if (endTimestamp > currentTimestamp) {
                endTimestamp = currentTimestamp;
            }

            Pageable pageable = PageRequest.of(page - 1, size); // 分页参数（页码从 0 开始）
            logger.debug("startTimestamp (UTC+8): {}, endTimestamp (UTC+8): {}", startTimestamp, endTimestamp);

            // 查询 AirDataResult 数据
            Page<DataQuery212> dataQuery212Page = dataQuery212OVRepository.findByCreateDateBetween(startTimestamp, endTimestamp, pageable);
            logger.debug("Total number of records found: {}", dataQuery212Page.getTotalElements());


            // 过滤未来数据
            List<DataQuery212> filteredData = dataQuery212Page.getContent().stream()
                    .filter(data -> {
                        long dataTimestamp = data.getDate().getTime(); // 假设 Date 是时间戳字段
                        return dataTimestamp <= currentTimestamp; // 只保留小于等于当前时间的数据
                    })
                    .collect(Collectors.toList());

            logger.debug("Number of records after filtering future data: {}", filteredData.size());

            // 重新封装分页数据

            Page<DataQuery212> filteredPage = new PageImpl<>(
                    filteredData, // 过滤后的数据
                    pageable, // 分页参数
                    dataQuery212Page.getTotalElements() // 使用原始查询的总条数
            );

            TableDataInfo result = new TableDataInfo();
            result.setCode(0);
            result.setMsg("ok");
            result.setTotal(filteredPage.getTotalElements()); // 返回原始查询的总条数
            result.setRows(filteredPage.getContent()); // 返回过滤后的当前页数据
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

    //转换方法提取为独立方法
    private DataQuery212 convertAirDataHour(AirDataHour airDataHour) {
        DataQuery212 dataQuery212 = new DataQuery212();
        // 字段映射
        dataQuery212.setAqi(airDataHour.getAqi());
        dataQuery212.setDeviceId(airDataHour.getDeviceId());
        dataQuery212.setStationId(Long.valueOf(airDataHour.getStationId()));
        dataQuery212.setDeviceName(airDataHour.getDeviceName());
        dataQuery212.setStationName(airDataHour.getStationName());
        dataQuery212.setSo2Thickness(airDataHour.getSo2Thickness());
        dataQuery212.setNo2Thickness(airDataHour.getNo2Thickness());
        dataQuery212.setCo3Thickness(airDataHour.getCo3Thickness());
        dataQuery212.setPm2_5(airDataHour.getPm25());
        dataQuery212.setPm10(airDataHour.getPm10());
        dataQuery212.setNoise(airDataHour.getNoise());
        dataQuery212.setTemperature(Double.valueOf(airDataHour.getWd()));
        dataQuery212.setHumidity(Double.valueOf(airDataHour.getSd()));
        dataQuery212.setWindSpeed(Double.valueOf(airDataHour.getWindSpeed()));
        dataQuery212.setWindDirectionString(airDataHour.getWindDirectionString());
        dataQuery212.setPressure(airDataHour.getPressure());
        // 如果有时间字段需要设置
        dataQuery212.setDate(airDataHour.getCreateDate());
        return dataQuery212;
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
        // 使用线程安全的日期时间类
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        // 时区
        ZoneId zoneId = ZoneId.of("Asia/Shanghai");

        try {
            LocalDate localDate = LocalDate.parse(date, formatter);
            ZonedDateTime startDateTime = localDate.atStartOfDay(zoneId);
            ZonedDateTime endDateTime = localDate.plusDays(1).atStartOfDay(zoneId);

            long startTimestamp = startDateTime.toInstant().toEpochMilli();
            long endTimestamp = endDateTime.toInstant().toEpochMilli();

            Pageable pageable = PageRequest.of(page - 1, size);
            logger.debug("deviceId: {}, startTimestamp: {}, endTimestamp: {}", deviceId, startTimestamp, endTimestamp);

            Page<DataQuery212> dataQuery212Page = dataQuery212OVRepository.findByDeviceIdAndCreateDateBetween(deviceId, startTimestamp, endTimestamp, pageable);
            List<AirDataHour> airDataHours = deviceDataRepository.findByDeviceIdAndCreateDateBetween(deviceId, startTimestamp, endTimestamp,pageable);
            //对查询到的数据进行转换
            List<DataQuery212> dataQuery212List = airDataHours.stream()
                    .map(airDataHour -> convertAirDataHour(airDataHour))
                    .collect(Collectors.toList());
            // 合并两个数据源的结果
            List<DataQuery212> combinedList = new ArrayList<>();
            combinedList.addAll(dataQuery212Page.getContent());  // 第一个查询结果
            combinedList.addAll(dataQuery212List);              // 第二个查询结果

            //  计算总记录数（假设两个查询不重复）
            long totalElements = dataQuery212Page.getTotalElements() + dataQuery212List.size();
            // 5. 返回合并后的分页数据
            Page<DataQuery212> combinedPage = new PageImpl<>(combinedList, pageable, totalElements);

            TableDataInfo result = new TableDataInfo();
            result.setCode(0);
            result.setMsg("ok");
            result.setTotal(combinedPage.getTotalElements());
            result.setRows(combinedPage.getContent());

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


//    @Override
//    public List<DataQuery212> fetchLatestData() {
//        try {
//            Instant now = Instant.now();
//            Instant fiveMinutesAgo = now.minus(Duration.ofMinutes(5));
//
//            int pageSize = 10;
//            int pageNumber = 0;
//
//            List<DataQuery212> newData = new ArrayList<>();
//
//            while (true) {
//                Pageable pageable = PageRequest.of(pageNumber, pageSize);
//                Page<DataQuery212> dataQuery212Page = dataQuery212OVRepository.findByCreateDateBetween(fiveMinutesAgo.toEpochMilli(), now.toEpochMilli(), pageable);
//
//
//                if (!dataQuery212Page.hasNext()) {
//                    break;
//                }
//                pageNumber++;
//            }
//
//            if (newData.isEmpty()) {
//                logger.info("No new data fetched.");
//            } else {
//                logger.info("Fetched {} new records.", newData.size());
//                latestData.addAll(newData);
//            }
//
//            return new ArrayList<>(latestData);
//        } catch (Exception e) {
//            logger.error("Error fetching latest data", e);
//            return Collections.emptyList();
//        }
//    }




//    @Scheduled(fixedRate = 300000)
//    public void scheduledFetchAndAppendData() {
//        try {
//            logger.info("Starting scheduled fetch and append data task...");
//            fetchLatestData();
//            logger.info("Total number of records fetched: {}", latestData.size());
//        } catch (Exception e) {
//            logger.error("Error during scheduled task", e);
//        }
//    }

    @Override
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

            Pageable pageable = PageRequest.of(page - 1, size);
            logger.debug("deviceId: {}, startTimestamp: {}, endTimestamp: {}", deviceId, startTimestamp, endTimestamp);


            Page<DataQuery212> dataQuery212Page = dataQuery212OVRepository.findByDeviceIdAndCreateDateBetween(deviceId, startTimestamp, endTimestamp, pageable);

            List<AirDataHour> airDataHours = deviceDataRepository.findByDeviceIdAndCreateDateBetween(deviceId, startTimestamp, endTimestamp,pageable);
            //对查询到的数据进行转换
            List<DataQuery212> dataQuery212List = airDataHours.stream()
                    .map(airDataHour -> convertAirDataHour(airDataHour))
                    .collect(Collectors.toList());
            // 合并两个数据源的结果
            List<DataQuery212> combinedList = new ArrayList<>();
            combinedList.addAll(dataQuery212Page.getContent());  // 第一个查询结果
            combinedList.addAll(dataQuery212List);              // 第二个查询结果

            //  计算总记录数（假设两个查询不重复）
            long totalElements = dataQuery212Page.getTotalElements() + dataQuery212List.size();
            // 5. 返回合并后的分页数据
            Page<DataQuery212> combinedPage = new PageImpl<>(combinedList, pageable, totalElements);
            // 打印查询到的数据条数
            logger.debug("Total number of records found: {}", dataQuery212Page.getTotalElements());



            TableDataInfo result = new TableDataInfo();
            result.setCode(0);
            result.setMsg("ok");
            result.setTotal(combinedPage.getTotalElements());
            result.setRows(combinedPage.getContent());

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

    public List<DataQuery212> selectRecentUnpushedDataByRegion() throws ParseException {
        LocalDateTime nowDate = LocalDateTime.now();
        // 获取10分钟前的时间
        LocalDateTime tenMinutesAgo = nowDate.minusMinutes(10);

        // 提取年、月、日、小时和分钟
        int yearStart = tenMinutesAgo.getYear();
        int monthStart = tenMinutesAgo.getMonthValue();
        int dayStart = tenMinutesAgo.getDayOfMonth();
        int hourStart = tenMinutesAgo.getHour();
        int minuteStart = tenMinutesAgo.getMinute();

        int yearEnd = nowDate.getYear();
        int monthEnd = nowDate.getMonthValue();
        int dayEnd = nowDate.getDayOfMonth();
        int hourEnd = nowDate.getHour();
        int minuteEnd = nowDate.getMinute();

        // 拼接成字符串形式
        String dateTimeStart = String.format("%d-%02d-%02d %02d:%02d", yearStart, monthStart, dayStart, hourStart, minuteStart);
        String dateTimeEnd = String.format("%d-%02d-%02d %02d:%02d", yearEnd, monthEnd, dayEnd, hourEnd, minuteEnd);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        dateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
        Date startDate = dateFormat.parse(dateTimeStart);
        Date endDate = dateFormat.parse(dateTimeEnd);

        long startTimestamp = startDate.getTime();
        long endTimestamp = endDate.getTime();

        // 添加 Pageable 参数
        PageRequest pageable = PageRequest.of(0, Integer.MAX_VALUE); // 使用默认分页参数，可以根据需求调整
        Page<DataQuery212> data = dataQuery212OVRepository.findByCreateDateBetween(startTimestamp, endTimestamp, pageable);

        // 将 Page<DataQuery212> 转换为 List<DataQuery212>
        List<DataQuery212> dataList = data.getContent();
        return dataList;
    }


}




