package com.ruoyi.runda.service.impl;

import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.runda.domain.AirDataHour;
import com.ruoyi.runda.repository.AirDataHourRepository;
import com.ruoyi.runda.service.AirDataHourService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class AirDataHourServiceImpl implements AirDataHourService {

    private static final Logger logger = LoggerFactory.getLogger(AirDataHourServiceImpl.class);
    // 定义并初始化 SimpleDateFormat 对象
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    static {
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai")); // 明确指定时区
    }
    @Autowired
    private AirDataHourRepository airDataHourRepository;


    @Override
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
            pageReports.getContent().forEach(report -> logger.debug("Report: deviceId={}, aqi={}, so2={}, no2={}, co={}, o3={}, pm2_5={}, pm10={}, deptId={}",
                    report.getDeviceId(), report.getAqi(), report.getSo2Thickness(), report.getNo2Thickness(),
                    report.getCo(), report.getCo3Thickness(), report.getPm25(), report.getPm10(), report.getDeptId()));
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

        // 添加查询时间信息和 deptId 信息
        for (Map.Entry<String, Map<String, Object>> entry : rankedList) {
            entry.getValue().put("queryTime", dateTimeStr); // 添加查询时间
            entry.getValue().put("deptId", ((AirDataHour) pageReports.getContent().stream()
                    .filter(report -> report.getDeviceId().equals(entry.getKey()))
                    .findFirst().orElse(new AirDataHour())).getDeptId()); // 添加 deptId
        }

        // 将结果转换为TableDataInfo类型
        List<Map<String, Object>> data = rankedList.stream()
                .map(entry -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("deviceId", entry.getKey());
                    map.putAll(entry.getValue());
                    return map;
                })
                .collect(Collectors.toList());

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

        metrics.put("primaryPollutant", getPrimaryPollutant(reports));

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
            hourlyData.forEach(report -> logger.debug("Report: deviceId={}, aqi={}, so2={}, no2={}, co={}, o3={}, pm2_5={}, pm10={}, deptId={}",
                    report.getDeviceId(), report.getAqi(), report.getSo2Thickness(), report.getNo2Thickness(),
                    report.getCo(), report.getCo3Thickness(), report.getPm25(), report.getPm10(), report.getDeptId()));
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
                    metrics.put("deptId", reports.get(0).getDeptId()); // 假设同一小时内所有数据的 deptId 相同
                    return new AbstractMap.SimpleEntry<>(hour, metrics);
                })
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        // 将结果转换为TableDataInfo类型
        List<Map<String, Object>> data = hourlyAverages.values().stream()
                .map(metrics -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("hour", metrics.get("hour"));
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

        metrics.put("primaryPollutant", getPrimaryPollutant(reports));

        return metrics;
    }

    /**
     * 新增的方法：根据具体的小时查询数据
     */
    public TableDataInfo calculateHourlyAverageForSpecificTime(String dateTimeStr, String deviceId) throws Exception {
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
        endCalendar.add(Calendar.HOUR_OF_DAY, 1); // 增加一小时
        endCalendar.set(Calendar.MINUTE, 59);
        endCalendar.set(Calendar.SECOND, 59);
        endCalendar.set(Calendar.MILLISECOND, 999);

        long startDateMillis = startCalendar.getTimeInMillis();
        long endDateMillis = endCalendar.getTimeInMillis();

        logger.info("Fetching data between timestamps: {} and {}", startDateMillis, endDateMillis);

        List<AirDataHour> hourlyData = airDataHourRepository.findByCreateDateBetweenAndDeviceId(startDateMillis, endDateMillis, deviceId);

        if (hourlyData.isEmpty()) {
            logger.warn("No data found for the specified device and time range.");
            return createEmptyTableDataInfo();
        } else {
            logger.info("Found {} records in total", hourlyData.size());
            // 打印每条记录的 deviceId 和 aqi
            hourlyData.forEach(report -> logger.debug("Report: deviceId={}, aqi={}, so2={}, no2={}, co={}, o3={}, pm2_5={}, pm10={}, deptId={}",
                    report.getDeviceId(), report.getAqi(), report.getSo2Thickness(), report.getNo2Thickness(),
                    report.getCo(), report.getCo3Thickness(), report.getPm25(), report.getPm10(), report.getDeptId()));
        }

        // 计算各项指标平均值
        Map<String, Object> metrics = calculateMetrics(hourlyData);
        metrics.put("startTime", dateTimeStr);
        metrics.put("endTime", dateTimeFormat.format(endCalendar.getTime()));
        metrics.put("deptId", hourlyData.get(0).getDeptId()); // 获取第一个记录的 deptId

        // 将结果转换为TableDataInfo类型
        List<Map<String, Object>> data = Collections.singletonList(metrics);

        TableDataInfo tableDataInfo = new TableDataInfo();
        tableDataInfo.setCode(0); // Assuming success code is 0
        tableDataInfo.setMsg("success");
        tableDataInfo.setRows(data);
        tableDataInfo.setTotal(hourlyData.size());
        return tableDataInfo;
    }

    @Override
    public TableDataInfo calculateDailyAveragePm25AndPm10(String dateStr, String deviceId) throws Exception {
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
            hourlyData.forEach(report -> logger.debug("Report: deviceId={}, aqi={}, so2={}, no2={}, co={}, o3={}, pm2_5={}, pm10={}, deptId={}",
                    report.getDeviceId(), report.getAqi(), report.getSo2Thickness(), report.getNo2Thickness(),
                    report.getCo(), report.getCo3Thickness(), report.getPm25(), report.getPm10(), report.getDeptId()));
        }

        // 计算整个时间段内 pm2.5 和 pm10 的平均值
        double averagePm25 = calculateAverage(hourlyData, AirDataHour::getPm25);
        double averagePm10 = calculateAverage(hourlyData, AirDataHour::getPm10);

        // 创建结果对象
        Map<String, Object> result = new HashMap<>();
        result.put("date", dateStr);
        result.put("deviceId", deviceId);
        result.put("averagePm2_5", round(averagePm25));
        result.put("averagePm10", round(averagePm10));
        result.put("deptId", hourlyData.get(0).getDeptId()); // 获取第一个记录的 deptId

        // 将结果转换为TableDataInfo类型
        List<Map<String, Object>> data = Collections.singletonList(result);

        TableDataInfo tableDataInfo = new TableDataInfo();
        tableDataInfo.setCode(0); // Assuming success code is 0
        tableDataInfo.setMsg("success");
        tableDataInfo.setRows(data);
        tableDataInfo.setTotal(hourlyData.size());
        return tableDataInfo;
    }


    @Override
    public TableDataInfo getCurrentHourAverageForAllDevices() {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Shanghai"));
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, -1);

        long startDateMillis = calendar.getTimeInMillis();
        calendar.add(Calendar.HOUR_OF_DAY, 1);
        long endDateMillis = calendar.getTimeInMillis();

        logger.info("Fetching current hour data between timestamps: {} and {}", startDateMillis, endDateMillis);

        List<AirDataHour> hourlyData = airDataHourRepository.findByCreateDateBetween(startDateMillis, endDateMillis);

        if (hourlyData.isEmpty()) {
            logger.warn("No data found for the current hour.");
            return createEmptyTableDataInfo();
        } else {
            logger.info("Found {} records in total", hourlyData.size());
            // 打印每条记录的 deviceId 和 aqi
            hourlyData.forEach(report -> logger.debug("Report: deviceId={}, aqi={}, so2={}, no2={}, co={}, o3={}, pm2_5={}, pm10={}, deptId={}",
                    report.getDeviceId(), report.getAqi(), report.getSo2Thickness(), report.getNo2Thickness(),
                    report.getCo(), report.getCo3Thickness(), report.getPm25(), report.getPm10(), report.getDeptId()));
        }

        // 计算每个设备在当前小时的各项指标平均值
        Map<String, Map<String, Object>> averages = hourlyData.stream()
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

        // 添加查询时间信息和 deptId 信息
        for (Map.Entry<String, Map<String, Object>> entry : rankedList) {
            entry.getValue().put("deptId", ((AirDataHour) hourlyData.stream()
                    .filter(report -> report.getDeviceId().equals(entry.getKey()))
                    .findFirst().orElse(new AirDataHour())).getDeptId()); // 添加 deptId
        }

        // 将结果转换为TableDataInfo类型
        List<Map<String, Object>> data = rankedList.stream()
                .map(entry -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("deviceId", entry.getKey());
                    map.putAll(entry.getValue());
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


}