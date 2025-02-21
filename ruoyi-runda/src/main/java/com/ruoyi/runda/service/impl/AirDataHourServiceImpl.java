package com.ruoyi.runda.service.impl;

import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.runda.domain.*;
import com.ruoyi.runda.mapper.AlarmInfoMapper;
import com.ruoyi.runda.mapper.AlarmRemindMapper;
import com.ruoyi.runda.mapper.StationMapper;
import com.ruoyi.runda.repository.AirDataHourRepository;
import com.ruoyi.runda.repository.HourlyAverageAirDataRepository;
import com.ruoyi.runda.service.AirDataHourService;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class AirDataHourServiceImpl implements AirDataHourService {
@Autowired

    private static final Logger logger = LoggerFactory.getLogger(AirDataHourServiceImpl.class);
    // 定义并初始化 SimpleDateFormat 对象
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    static {
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai")); // 明确指定时区
    }

    // 定义并初始化部门ID到部门名称的映射
    private static final Map<String, String> deptIdToDeptNameMap = new HashMap<>();

    static {
        deptIdToDeptNameMap.put("18002", "张家口怀来");
        // 添加其他部门ID和部门名称的对应关系
        deptIdToDeptNameMap.put("18003", "张家口经开区");
        // 可以继续添加更多部门ID和名称
    }
    @Autowired
    private AirDataHourRepository airDataHourRepository;

    @Autowired
    private HourlyAverageAirDataRepository hourlyAverageAirDataRepository; // MySQL Repository

    @Autowired
    private AlarmInfoMapper alarmInfoMapper;

    @Autowired
    private  StationMapper stationMapper;

    @Override
    @Transactional
    public TableDataInfo calculateAverageForSpecificDateTime(String dateTimeStr) throws Exception {
        SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        dateTimeFormat.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai")); // 明确指定时区

        Date dateTime;
        try {
            dateTime = dateTimeFormat.parse(dateTimeStr);
        } catch (ParseException e) {
            logger.error("Error parsing date: {}", e.getMessage());
            throw new Exception("Invalid date format", e);
        }

        Calendar startCalendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Shanghai"));
        startCalendar.setTime(dateTime);
        startCalendar.set(Calendar.MINUTE, 0);
        startCalendar.set(Calendar.SECOND, 0);
        startCalendar.set(Calendar.MILLISECOND, 0);

        Calendar endCalendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Shanghai"));
        endCalendar.setTime(dateTime);
        endCalendar.set(Calendar.MINUTE, 59);
        endCalendar.set(Calendar.SECOND, 59);
        endCalendar.set(Calendar.MILLISECOND, 999);

        long startDateMillis = startCalendar.getTimeInMillis();
        long endDateMillis = endCalendar.getTimeInMillis();

        logger.info("Fetching data between timestamps: {} and {}", startDateMillis, endDateMillis);

        // 添加 Pageable 参数
        PageRequest pageable = PageRequest.of(0, Integer.MAX_VALUE); // 使用默认分页参数，可以根据需求调整
        Page<AirDataHour> pageReports = airDataHourRepository.findCustomByCreateDateBetweenTimestamps(startDateMillis, endDateMillis, pageable);

        if (pageReports.isEmpty()) {
            logger.warn("No data found for the specified date time range.");
            return createEmptyTableDataInfo();
        } else {
            logger.info("Found {} records in total", pageReports.getTotalElements());
            // 打印每条记录的 deviceId 和 aqi
            pageReports.getContent().forEach(report -> logger.debug("Report: deviceId={}, aqi={}, so2={}, no2={}, co={}, o3={}, pm2_5={}, pm10={}, deptId={}, stationId={},deviceName={},stationName={},deptId={}",
                    report.getDeviceId(), report.getAqi(), report.getSo2Thickness(), report.getNo2Thickness(),
                    report.getCo(), report.getCo3Thickness(), report.getPm25(), report.getPm10(), report.getDeptId(), report.getStationId(),report.getDeviceName(),report.getStationName(),report.getDeptId()));
        }


        // 计算每个设备指定日期时间内的各项指标平均值
        Map<String, Map<String, Object>> averages = pageReports.getContent().stream()
                .collect(Collectors.groupingBy(AirDataHour::getDeviceId))
                .entrySet().stream()
                .map(this::calculateMetrics)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        // 根据AQI平均值进行排名（AQI值越低，排名越高）
        List<Map.Entry<String, Map<String, Object>>> rankedList = averages.entrySet().stream()
                .sorted(Comparator.comparingDouble(entry -> round((Double) entry.getValue().get("averageAqi")))) // 按AQI升序排列
                .map(entry -> {
                    entry.getValue().put("rank", entry.getValue().getOrDefault("rank", 0));
                    return entry;
                })
                .collect(Collectors.toList());

        for (int i = 0; i < rankedList.size(); i++) {
            rankedList.get(i).getValue().put("rank", i + 1);
        }

        // 将结果转换为TableDataInfo类型
        List<Map<String, Object>> data = rankedList.stream()
                .map(entry -> {
                    Map<String, Object> map = new HashMap<>();
                    map.putAll(entry.getValue());
                    map.put("dateTimeStr", dateTimeStr);
                    return map;
                })
                .collect(Collectors.toList());
        // 添加日志信息以确认 stationId 是否存在
//        data.forEach(row -> logger.debug("Row data: {}", row));


        // 将平均数据保存到MySQL数据库中
        saveToMysql(data);


        TableDataInfo tableDataInfo = new TableDataInfo();
        tableDataInfo.setCode(0); // Assuming success code is 0
        tableDataInfo.setMsg("success");
        tableDataInfo.setRows(data);
        tableDataInfo.setTotal(pageReports.getTotalElements());
        return tableDataInfo;
    }

    private Map.Entry<String, Map<String, Object>> calculateMetrics(Map.Entry<String, List<AirDataHour>> entry) {
        String deviceId = entry.getKey();
        List<AirDataHour> reports = entry.getValue();

        // 使用 Optional 处理可能为 null 的 stationId
        String stationId = reports.stream()
                .filter(Objects::nonNull)
                .map(AirDataHour::getStationId)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);
        // 使用 Optional 处理可能为 null 的 deviceName
        String deviceName = reports.stream()
                .filter(Objects::nonNull)
                .map(AirDataHour::getDeviceName)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);

        // 使用 Optional 处理可能为 null 的 stationName
        String stationName = reports.stream()
                .filter(Objects::nonNull)
                .map(AirDataHour::getStationName)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);
        //使用 Optional 处理可能为 null 的 deptId
        String deptId = reports.stream()
                .filter(Objects::nonNull)
                .map(AirDataHour::getDeptId)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);


        Map<String, Object> metrics = new HashMap<>();

        metrics.put("deviceId", deviceId);
        metrics.put("stationId", stationId);
        metrics.put("deptId", deptId);
        metrics.put("deviceName", deviceName);
        metrics.put("stationName", stationName);
        metrics.put("averageAqi", round(calculateAverage(reports, AirDataHour::getAqi)));
        metrics.put("averageSo2", round(calculateAverage(reports, AirDataHour::getSo2Thickness)));
        metrics.put("averageNo2", round(calculateAverage(reports, AirDataHour::getNo2Thickness)));
        metrics.put("averageCo", round(calculateAverage(reports, AirDataHour::getCo)));
        metrics.put("averageO3", round(calculateAverage(reports, AirDataHour::getCo3Thickness)));
        metrics.put("averagePm2_5", round(calculateAverage(reports, AirDataHour::getPm25)));
        metrics.put("averagePm10", round(calculateAverage(reports, AirDataHour::getPm10)));
        Double averageAqi = (Double) metrics.get("averageAqi");
        metrics.put("level", getAqiLevel(averageAqi));
        metrics.put("quality", getAqiQuality(averageAqi));
        metrics.put("color", getAqiColor(averageAqi));
        if (averageAqi > 50) {
            metrics.put("primaryPollutant", getPrimaryPollutant(reports));
        } else {
            metrics.put("primaryPollutant", "-");
        }

        return new AbstractMap.SimpleEntry<>(deviceId, metrics);
    }

    private Double calculateAverage(List<AirDataHour> reports, Function<AirDataHour, Double> getter) {
        return reports.stream()
                .filter(report -> Objects.nonNull(getter.apply(report)))
                .mapToDouble(getter::apply)
                .average()
                .orElse(Double.NaN);
    }

    private Double round(Double value) {
        return Double.valueOf(Math.round(value));
    }

    private String getAqiLevel(Double aqi) {
        if (aqi == null) return "未知";
        if (aqi >= 0 && aqi <= 50) return "一级";
        if (aqi > 50 && aqi <= 100) return "二级";
        if (aqi > 100 && aqi <= 150) return "三级";
        if (aqi > 150 && aqi <= 200) return "四级";
        if (aqi > 200 && aqi <= 300) return "五级";
        if (aqi > 300) return "六级";
        return "未知";
    }

    private String getAqiQuality(Double aqi) {
        if (aqi == null) return "未知";
        if (aqi >= 0 && aqi <= 50) return "优";
        if (aqi > 50 && aqi <= 100) return "良";
        if (aqi > 100 && aqi <= 150) return "轻度污染";
        if (aqi > 150 && aqi <= 200) return "中度污染";
        if (aqi > 200 && aqi <= 300) return "重度污染";
        if (aqi > 300) return "严重污染";
        return "未知";
    }

    private String getAqiColor(Double aqi) {
        if (aqi == null) return "未知";
        if (aqi >= 0 && aqi <= 50) return "绿色";
        if (aqi > 50 && aqi <= 100) return "黄色";
        if (aqi > 100 && aqi <= 150) return "橙色";
        if (aqi > 150 && aqi <= 200) return "红色";
        if (aqi > 200 && aqi <= 300) return "紫色";
        if (aqi > 300) return "褐色";
        return "未知";
    }

    private String getPrimaryPollutant(List<AirDataHour> reports) {
        if (reports == null || reports.isEmpty()) return "未知";

        double maxSo2 = calculateMax(reports, AirDataHour::getSo2Thickness);
        double maxNo2 = calculateMax(reports, AirDataHour::getNo2Thickness);
        double maxCo = calculateMax(reports, AirDataHour::getCo);
        double maxO3 = calculateMax(reports, AirDataHour::getCo3Thickness);
        double maxPm25 = calculateMax(reports, AirDataHour::getPm25);
        double maxPm10 = calculateMax(reports, AirDataHour::getPm10);

        if (maxSo2 >= maxNo2 && maxSo2 >= maxCo && maxSo2 >= maxO3 && maxSo2 >= maxPm25 && maxSo2 >= maxPm10) return "SO2";
        if (maxNo2 >= maxSo2 && maxNo2 >= maxCo && maxNo2 >= maxO3 && maxNo2 >= maxPm25 && maxNo2 >= maxPm10) return "NO2";
        if (maxCo >= maxSo2 && maxCo >= maxNo2 && maxCo >= maxO3 && maxCo >= maxPm25 && maxCo >= maxPm10) return "CO";
        if (maxO3 >= maxSo2 && maxO3 >= maxNo2 && maxO3 >= maxCo && maxO3 >= maxPm25 && maxO3 >= maxPm10) return "O3";
        if (maxPm25 >= maxSo2 && maxPm25 >= maxNo2 && maxPm25 >= maxCo && maxPm25 >= maxO3 && maxPm25 >= maxPm10) return "PM2.5";
        if (maxPm10 >= maxSo2 && maxPm10 >= maxNo2 && maxPm10 >= maxCo && maxPm10 >= maxO3 && maxPm10 >= maxPm25) return "PM10";

        return "未知";
    }

    private double calculateMax(List<AirDataHour> reports, Function<AirDataHour, Double> getter) {
        return reports.stream()
                .filter(report -> Objects.nonNull(getter.apply(report)))
                .mapToDouble(getter::apply)
                .max()
                .orElse(0);
    }
    public void saveToMysql(List<Map<String, Object>> data) throws ParseException {
        SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        dateTimeFormat.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai")); // 明确指定时区

        for (Map<String, Object> row : data) {
            String deviceId = (String) row.get("deviceId");
            if (deviceId == null || deviceId.isEmpty()) {
                logger.error("Device ID is null or empty: {}", row);
                continue;
            }

            // 确认 stationId 是否存在
            String stationId = (String) row.get("stationId");
            if (stationId == null || stationId.isEmpty()) {
                logger.warn("stationId is missing or null for deviceId: {}", deviceId);
                continue; // 如果 stationId 缺失或为空，则跳过当前记录
            }
            //计算createAt 时间，应该与data中的dateTimeStr一样
            Date createAt = dateTimeFormat.parse((String) row.get("dateTimeStr"));

            // 检查数据库中是否已存在相同记录
            boolean exists = hourlyAverageAirDataRepository.existsByDeviceIdAndCreatedAtCustom(deviceId, createAt);
            if (exists) {
                logger.info("Record with deviceId {} and createdAt {} already exists. Skipping insertion.", deviceId, createAt);
                continue;
            }

            // 计算 updateAt 时间，比 createAt 晚 59 分钟
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(createAt);
            calendar.add(Calendar.MINUTE, 59);
            Date updateAt = calendar.getTime();

            HourlyAverageAirData hourlyAverageAirData = new HourlyAverageAirData();


            hourlyAverageAirData.setDeviceId(deviceId);
            hourlyAverageAirData.setCreatedAt(createAt); // 设置 create_at 字段
            hourlyAverageAirData.setUpdatedAt(updateAt); // 设置 update_at 字段
            hourlyAverageAirData.setStationId(stationId); // 添加 stationId 字段
            hourlyAverageAirData.setDeptId((String) row.get("deptId"));
            hourlyAverageAirData.setAverageAqi(((Number) row.get("averageAqi")).longValue());
            hourlyAverageAirData.setAverageSo2(((Number) row.get("averageSo2")).longValue());
            hourlyAverageAirData.setAverageNo2(((Number) row.get("averageNo2")).longValue());
            hourlyAverageAirData.setAverageO3(((Number) row.get("averageO3")).longValue());
            hourlyAverageAirData.setAveragePm25(((Number) row.get("averagePm2_5")).longValue());
            hourlyAverageAirData.setAveragePm10(((Number) row.get("averagePm10")).longValue());
            hourlyAverageAirData.setAqiLevel((String) row.get("level"));
            hourlyAverageAirData.setAqiQuality((String) row.get("quality"));
            hourlyAverageAirData.setAqiColor((String) row.get("color"));
            hourlyAverageAirData.setPrimaryPollutant((String) row.get("primaryPollutant"));
            hourlyAverageAirData.setDeviceName((String) row.get("deviceName"));
            hourlyAverageAirData.setStationName((String) row.get("stationName"));

            Long averagePm25 =  hourlyAverageAirData.getAveragePm25();
            System.out.println("averagePm25: " + averagePm25);
            Long averagePm10 =  hourlyAverageAirData.getAveragePm10();
            if (averagePm25 != null && averagePm10 != null && averagePm25 > 30 && averagePm10 > 30) {
                AlarmInfo alarmInfo = new AlarmInfo();
                alarmInfo.setStationId(Long.valueOf(stationId));
                alarmInfo.setDeviceId(Long.valueOf(deviceId));
                alarmInfo.setDeptId(Long.valueOf((String) row.get("deptId")));
//                String deptId = (String) row.get("deptId");
//                // 使用映射获取部门名称
//                String deptName = deptIdToDeptNameMap.get(deptId);
//                if (deptName != null) {
//                    alarmInfo.setDeptName(deptName);
//                } else {
//                    logger.warn("No department name found for deptId: {}", deptId);
//                }

                //根据stationId进行查询station表中的phone信息并把phone插入到alarmInfo表中
                // 根据stationId获取电话号码
                String phone = stationMapper.getPhoneByStationId(stationId);
                if (phone != null) {
                    alarmInfo.setPhoneNumber(phone);
                } else {

                    alarmInfo.setPhoneNumber(null);
                }

                alarmInfo.setStationName(hourlyAverageAirData.getStationName());
                alarmInfo.setDeviceName(hourlyAverageAirData.getDeviceName());
                alarmInfo.setAlarmType(2L);
                alarmInfo.setCreateDate(updateAt);
                alarmInfo.setStatus("设备高值");
                alarmInfo.setSmsMessage("设备" + deviceId + "数据异常，请及时处理");
                alarmInfoMapper.insertAlarmInfo(alarmInfo);
            }

            // 打印调试信息以确认 deptId 的值
            logger.debug("Saving record with deviceId={}, stationId={}, createAt={}, updateAt={}",
                    hourlyAverageAirData.getDeviceId(),
                    hourlyAverageAirData.getStationId(),
                    hourlyAverageAirData.getCreatedAt(),
                    hourlyAverageAirData.getUpdatedAt());
            hourlyAverageAirDataRepository.save(hourlyAverageAirData);
        }
    }


    @Override
    public void exportToExcel(HttpServletResponse response, List<AirDataHour> dataList) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("AirHourData");

        // 创建标题行
        Row headerRow = sheet.createRow(0);
        String[] headers = {"deviceId", "stationId", "rank", "averageAqi", "averageSo2", "averageO3", "averageNo2", "averagePm2_5", "averagePm10", "primaryPollutan", "level", "quality", "color", "dateTimeStr"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
        }

        // 填充数据
        int rowNum = 1;
        for (AirDataHour data : dataList) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(data.getDeviceId());
            row.createCell(1).setCellValue(data.getStationId());
            row.createCell(2).setCellValue(data.getRanking() != null ? String.valueOf(data.getRanking()) : "");
            row.createCell(3).setCellValue(data.getAqi() != null ? String.valueOf(data.getAqi()) : "");
            row.createCell(4).setCellValue(data.getSo2Thickness() != null ? String.valueOf(data.getSo2Thickness()) : "");
            row.createCell(5).setCellValue(data.getCo3Thickness() != null ? String.valueOf(data.getCo3Thickness()) : "");
            row.createCell(6).setCellValue(data.getNo2Thickness() != null ? String.valueOf(data.getNo2Thickness()) : "");
            row.createCell(7).setCellValue(data.getPm25() != null ? String.valueOf(data.getPm25()) : "");
            row.createCell(8).setCellValue(data.getPm10() != null ? String.valueOf(data.getPm10()) : "");
            row.createCell(9).setCellValue(data.getPrimaryPollutant() != null ? data.getPrimaryPollutant().toString() : "");
            row.createCell(10).setCellValue(data.getLevel() != null ? data.getLevel().toString() : "");
            row.createCell(11).setCellValue(data.getQuality() != null ? data.getQuality().toString() : "");
            row.createCell(12).setCellValue(data.getColor() != null ? data.getColor().toString() : "");
            row.createCell(13).setCellValue(data.getCreateDate() != null ? String.valueOf(data.getCreateDate()) : "");
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
    public TableDataInfo calculateDailyHourlyAverage(String dateStr, String deviceId) throws Exception {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai")); // 明确指定时区

        Date date;
        try {
            date = dateFormat.parse(dateStr);
        } catch (ParseException e) {
            logger.error("Error parsing date: {}", e.getMessage());
            throw new Exception("Invalid date format", e);
        }

        Calendar startOfDay = Calendar.getInstance(TimeZone.getTimeZone("Asia/Shanghai"));
        startOfDay.setTime(date);
        startOfDay.set(Calendar.HOUR_OF_DAY, 0);
        startOfDay.set(Calendar.MINUTE, 0);
        startOfDay.set(Calendar.SECOND, 0);
        startOfDay.set(Calendar.MILLISECOND, 0);

        Calendar endOfDay = Calendar.getInstance(TimeZone.getTimeZone("Asia/Shanghai"));
        endOfDay.setTime(date);
        endOfDay.set(Calendar.HOUR_OF_DAY, 23);
        endOfDay.set(Calendar.MINUTE, 59);
        endOfDay.set(Calendar.SECOND, 59);
        endOfDay.set(Calendar.MILLISECOND, 999);

        long startDateMillis = startOfDay.getTimeInMillis();
        long endDateMillis = endOfDay.getTimeInMillis();

        logger.info("Fetching daily data between timestamps: {} and {}", startDateMillis, endDateMillis);

        List<AirDataHour> hourlyData = airDataHourRepository.findByCreateDateBetweenAndDeviceId(startDateMillis, endDateMillis, deviceId);

        if (hourlyData.isEmpty()) {
            logger.warn("No data found for the specified device and date range.");
            return createEmptyTableDataInfo();
        } else {
            logger.info("Found {} records in total", hourlyData.size());
            // 打印每条记录的 deviceId 和 aqi
            hourlyData.forEach(report -> logger.debug("Report: deviceId={}, aqi={}, so2={}, no2={}, co={}, o3={}, pm2_5={}, pm10={}, deptId={},stationId={},deviceName={},stationName={}",
                    report.getDeviceId(), report.getAqi(), report.getSo2Thickness(), report.getNo2Thickness(),
                    report.getCo(), report.getCo3Thickness(), report.getPm25(), report.getPm10(), report.getDeptId(),report.getStationId(),report.getDeviceName(),report.getStationName()));
        }

        // 分组按小时计算平均值，并保留 deptId
        Map<Integer, Map<String, Object>> hourlyAverages = hourlyData.stream()
                .collect(Collectors.groupingBy(hourlyDataPoint -> {
                    Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("Asia/Shanghai"));
                    cal.setTimeInMillis(hourlyDataPoint.getCreateDate().getTime());

                    return cal.get(Calendar.HOUR_OF_DAY);
                }))
                .entrySet().stream()
                .map(entry -> {
                    int hour = entry.getKey();
                    List<AirDataHour> reports = entry.getValue();
                    Map<String, Object> metrics = calculateMetrics(reports);
                    metrics.put("hour", hour);
                    metrics.put("deviceName", reports.get(0).getDeviceName());
                    metrics.put("stationName", reports.get(0).getStationName());
                    //得到deviceId
                    metrics.put("deviceId", reports.get(0).getDeviceId());
                    // 得到stationId
                    metrics.put("stationId", reports.get(0).getStationId());
                    metrics.put("deptId", reports.get(0).getDeptId()); // 假设同一小时内所有数据的 deptId 相同
                    return new AbstractMap.SimpleEntry<>(hour, metrics);
                })
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        // 将结果转换为TableDataInfo类型
        List<Map<String, Object>> data = hourlyAverages.values().stream()
                .map(metrics -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("hour", metrics.get("hour"));
                    map.put("deviceId", metrics.get("deviceId"));
                    map.put("stationId", metrics.get("stationId"));
                    map.put("deptId", metrics.get("deptId"));
                    map.putAll(metrics);
                    return map;
                })
                .collect(Collectors.toList());

        TableDataInfo tableDataInfo = new TableDataInfo();
        tableDataInfo.setCode(0); // Assuming success code is 0
        tableDataInfo.setMsg("success");
        tableDataInfo.setRows(data);
        tableDataInfo.setTotal(hourlyData.size());
        return tableDataInfo;
    }

    private TableDataInfo createEmptyTableDataInfo() {
        TableDataInfo tableDataInfo = new TableDataInfo();
        tableDataInfo.setCode(0); // Assuming success code is 0
        tableDataInfo.setMsg("No data found");
        tableDataInfo.setRows(Collections.emptyList());
        tableDataInfo.setTotal(0);
        return tableDataInfo;
    }

    private Map<String, Object> calculateMetrics(List<AirDataHour> reports) {
        Map<String, Object> metrics = new HashMap<>();
        metrics.put("averageAqi", round(calculateAverage(reports, AirDataHour::getAqi)));
        metrics.put("averageSo2", round(calculateAverage(reports, AirDataHour::getSo2Thickness)));
        metrics.put("averageNo2", round(calculateAverage(reports, AirDataHour::getNo2Thickness)));
        metrics.put("averageCo", round(calculateAverage(reports, AirDataHour::getCo)));
        metrics.put("averageO3", round(calculateAverage(reports, AirDataHour::getCo3Thickness)));
        metrics.put("averagePm2_5", round(calculateAverage(reports, AirDataHour::getPm25)));
        metrics.put("averagePm10", round(calculateAverage(reports, AirDataHour::getPm10)));

        Double averageAqi = (Double) metrics.get("averageAqi");
        metrics.put("level", getAqiLevel(averageAqi));
        metrics.put("quality", getAqiQuality(averageAqi));
        metrics.put("color", getAqiColor(averageAqi));
        if(averageAqi>50) {
            metrics.put("primaryPollutant", getPrimaryPollutant(reports));
        }else {
            metrics.put("primaryPollutant", "-");
        }
        return metrics;
    }

    /**
     * 新增的方法：根据具体的小时查询数据
     */
    @Override
    public TableDataInfo calculateHourlyAverageForSpecificTime(String dateTimeStr, String deviceId) throws Exception {
        logger.debug("Entering calculateHourlyAverageForSpecificTime method");

        // 解析日期时间字符串
        SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        dateTimeFormat.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai")); // 明确指定时区

        Date dateTime;
        try {
            dateTime = dateTimeFormat.parse(dateTimeStr);
        } catch (ParseException e) {
            logger.error("Error parsing date: {}", e.getMessage());
            throw new Exception("Invalid date format", e);
        }

        // 计算开始时间和结束时间
        Calendar startCalendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Shanghai"));
        startCalendar.setTime(dateTime);
        startCalendar.set(Calendar.MINUTE, 0);
        startCalendar.set(Calendar.SECOND, 0);
        startCalendar.set(Calendar.MILLISECOND, 0);

        Calendar endCalendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Shanghai"));
        endCalendar.setTime(dateTime);
        endCalendar.add(Calendar.HOUR_OF_DAY, 0); // 增加一小时
        endCalendar.set(Calendar.MINUTE, 59);
        endCalendar.set(Calendar.SECOND, 59);
        endCalendar.set(Calendar.MILLISECOND, 999);

        long startDateMillis = startCalendar.getTimeInMillis();
        long endDateMillis = endCalendar.getTimeInMillis();

        logger.info("Fetching data between timestamps: {} and {}", startDateMillis, endDateMillis);

        // 查询数据库中的数据
        List<AirDataHour> hourlyData = airDataHourRepository.findByCreateDateBetweenAndDeviceId(startDateMillis, endDateMillis, deviceId);

        if (hourlyData.isEmpty()) {
            logger.warn("No data found for the specified device and time range.");
            return createEmptyTableDataInfo();
        } else {
            logger.info("Found {} records in total", hourlyData.size());
            // 打印每条记录的 deviceId 和 aqi
            hourlyData.forEach(report -> logger.debug("Report: deviceId={}, aqi={}, so2={}, no2={}, co={}, o3={}, pm2_5={}, pm10={}, deptId={}, stationId={}",
                    report.getDeviceId(), report.getAqi(), report.getSo2Thickness(), report.getNo2Thickness(),
                    report.getCo(), report.getCo3Thickness(), report.getPm25(), report.getPm10(), report.getDeptId(), report.getStationId()));
        }

        // 计算各项指标平均值
        Map<String, Object> metrics = calculateMetrics(hourlyData);
        metrics.put("dateTimeStr", dateTimeStr);
        metrics.put("endTime", dateTimeFormat.format(endCalendar.getTime()));
        metrics.put("deviceId", deviceId);
        metrics.put("stationId", hourlyData.get(0).getStationId());
        metrics.put("deptId", hourlyData.get(0).getDeptId()); // 获取第一个记录的 deptId

        // 将结果转换为TableDataInfo类型
        List<Map<String, Object>> data = Collections.singletonList(metrics);
        TableDataInfo tableDataInfo = new TableDataInfo();
        tableDataInfo.setCode(0); // Assuming success code is 0
        tableDataInfo.setMsg("success");
        tableDataInfo.setRows(data);
        tableDataInfo.setTotal(hourlyData.size());

        logger.debug("Exiting calculateHourlyAverageForSpecificTime method");
        return tableDataInfo;
    }



    @Override
    public TableDataInfo calculateDailyAveragePm25AndPm10ForAllDevices(String dateStr) throws Exception {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai")); // 明确指定时区

        Date date;
        try {
            date = dateFormat.parse(dateStr);
        } catch (ParseException e) {
            logger.error("Error parsing date: {}", e.getMessage());
            throw new Exception("Invalid date format", e);
        }

        Calendar startOfDay = Calendar.getInstance(TimeZone.getTimeZone("Asia/Shanghai"));
        startOfDay.setTime(date);
        startOfDay.set(Calendar.HOUR_OF_DAY, 0);
        startOfDay.set(Calendar.MINUTE, 0);
        startOfDay.set(Calendar.SECOND, 0);
        startOfDay.set(Calendar.MILLISECOND, 0);

        Calendar endOfDay = Calendar.getInstance(TimeZone.getTimeZone("Asia/Shanghai"));
        endOfDay.setTime(date);
        endOfDay.set(Calendar.HOUR_OF_DAY, 23);
        endOfDay.set(Calendar.MINUTE, 59);
        endOfDay.set(Calendar.SECOND, 59);
        endOfDay.set(Calendar.MILLISECOND, 999);

        long startDateMillis = startOfDay.getTimeInMillis();
        long endDateMillis = endOfDay.getTimeInMillis();

        logger.info("Fetching daily data between timestamps: {} and {}", startDateMillis, endDateMillis);

        List<AirDataHour> hourlyData = airDataHourRepository.findByCreateDateBetween2(startDateMillis, endDateMillis);

        if (hourlyData.isEmpty()) {
            logger.warn("No data found for the specified date range.");
            return createEmptyTableDataInfo();
        } else {
            logger.info("Found {} records in total", hourlyData.size());
            // 打印每条记录的 deviceId 和 aqi
            hourlyData.forEach(report -> logger.debug("Report: deviceId={}, aqi={}, so2={}, no2={}, co={}, o3={}, pm2_5={}, pm10={}, deptId={},stationId={}",
                    report.getDeviceId(), report.getAqi(), report.getSo2Thickness(), report.getNo2Thickness(),
                    report.getCo(), report.getCo3Thickness(), report.getPm25(), report.getPm10(), report.getDeptId(), report.getStationId()));
        }

        // 按设备ID分组计算平均值
        Map<String, List<AirDataHour>> groupedData = hourlyData.stream()
                .collect(Collectors.groupingBy(AirDataHour::getDeviceId));

        List<Map<String, Object>> results = new ArrayList<>();
        for (Map.Entry<String, List<AirDataHour>> entry : groupedData.entrySet()) {
            String deviceId = entry.getKey();
            List<AirDataHour> reports = entry.getValue();

            double averagePm25 = calculateAverage(reports, AirDataHour::getPm25);
            double averagePm10 = calculateAverage(reports, AirDataHour::getPm10);

            // 创建结果对象
            Map<String, Object> result = new HashMap<>();
            result.put("date", dateStr);
            result.put("deviceId", deviceId);
            result.put("averagePm2_5_24", round(averagePm25));
            result.put("averagePm10_24", round(averagePm10));
            result.put("deptId", reports.get(0).getDeptId()); // 获取第一个记录的 deptId
            result.put("stationId", reports.get(0).getStationId()); // 获取第一个记录的 stationId

            results.add(result);
        }

        // 查询已存在的 HourlyAverageAirData 记录
        Set<String> deviceIds = groupedData.keySet();
        List<HourlyAverageAirData> existingRecords = hourlyAverageAirDataRepository.findByDateAndDeviceIds(date, deviceIds);

        // 更新记录
        for (Map<String, Object> result : results) {
            String deviceId = (String) result.get("deviceId");
            HourlyAverageAirData existingRecord = existingRecords.stream()
                    .filter(record -> deviceId.equals(record.getDeviceId()))
                    .findFirst()
                    .orElse(null);

            if (existingRecord != null) {
                existingRecord.setAveragePm25_24(((Number) result.get("averagePm2_5_24")).longValue());
                existingRecord.setAveragePm10_24(((Number) result.get("averagePm10_24")).longValue());
                hourlyAverageAirDataRepository.save(existingRecord);
            } else {
                logger.warn("No existing record found for deviceId: {}", deviceId);
            }
        }

        // 将结果转换为TableDataInfo类型
        TableDataInfo tableDataInfo = new TableDataInfo();
        tableDataInfo.setCode(0); // Assuming success code is 0
        tableDataInfo.setMsg("success");
        tableDataInfo.setRows(results);
        tableDataInfo.setTotal(hourlyData.size());

        return tableDataInfo;
    }


}