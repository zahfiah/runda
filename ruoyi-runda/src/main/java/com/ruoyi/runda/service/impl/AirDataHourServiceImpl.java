package com.ruoyi.runda.service.impl;

import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.runda.domain.*;
import com.ruoyi.runda.mapper.AlarmInfoMapper;
import com.ruoyi.runda.mapper.AlarmRemindMapper;
import com.ruoyi.runda.mapper.DataQueryCountryMapper;
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
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
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
        deptIdToDeptNameMap.put("3", "人民公园"); // 东城区-人民公园
        deptIdToDeptNameMap.put("18002", "人民公园"); // 怀来县-人民公园
        deptIdToDeptNameMap.put("18003", "五金库");//蔚县-五金库
        deptIdToDeptNameMap.put("18070", "五金库");//蔚县-五金库
        deptIdToDeptNameMap.put("18007", " 烟厂");//怀安县-烟厂
        deptIdToDeptNameMap.put("18071", " 人民公园");//怀安县-人民公园
        deptIdToDeptNameMap.put("18011", " 五金库");//桥东区-五金库
        deptIdToDeptNameMap.put("18114", " 五金库");//桥东区-五金库
        deptIdToDeptNameMap.put("18115", " 人民公园");//桥西区-人民公园
        deptIdToDeptNameMap.put("18013", " 烟厂");//经开区-烟厂
        deptIdToDeptNameMap.put("18116", " 烟厂");//经开区-烟厂
        deptIdToDeptNameMap.put("18014", "世纪豪园"); // 长安区 宣化区 -世纪豪园
        deptIdToDeptNameMap.put("18016", " 人民公园");//崇礼区-人民公园
        deptIdToDeptNameMap.put("18077", " 人民公园");//崇礼区-人民公园
        deptIdToDeptNameMap.put("18017", "人民公园");//下花园区-人民公园
        deptIdToDeptNameMap.put("18018", "北泵房");//涿鹿县-北泵房
        deptIdToDeptNameMap.put("18019", "烟厂");//赤城县-烟厂
        deptIdToDeptNameMap.put("18020", " 世纪豪园");//阳原县-世纪豪园
        deptIdToDeptNameMap.put("18021", " 五金库");//万全县- 五金库
        deptIdToDeptNameMap.put("18022", "五金库");//尚义县-五金库
        deptIdToDeptNameMap.put("18023", " 烟厂");//康保县-烟厂
        deptIdToDeptNameMap.put("18024", "北泵房");//张北县-北泵房
        deptIdToDeptNameMap.put("18025", "世纪豪园");//沽源县-世纪豪园
        deptIdToDeptNameMap.put("18026", " 北泵房");//察北管理区-北泵房
        deptIdToDeptNameMap.put("18079", "北泵房");//涿鹿县-世纪豪园
        deptIdToDeptNameMap.put("18081", " 世纪豪园");//阳原县-世纪豪园
    }
    @Autowired
    private AirDataHourRepository airDataHourRepository;

    @Autowired
    private HourlyAverageAirDataRepository hourlyAverageAirDataRepository; // MySQL Repository

    @Autowired
    private AlarmInfoMapper alarmInfoMapper;

    @Autowired
    private  StationMapper stationMapper;

    @Autowired
    private DataQueryCountryMapper dataQueryCountryMapper;



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
//            pageReports.getContent().forEach(report -> logger.debug("Report: deviceId={}, aqi={}, so2={}, no2={}, co={}, o3={}, pm2_5={}, pm10={}, deptId={}, stationId={},deviceName={},stationName={},deptId={}",
//                    report.getDeviceId(), report.getAqi(), report.getSo2Thickness(), report.getNo2Thickness(),
//                    report.getCo(), report.getCo3Thickness(), report.getPm25(), report.getPm10(), report.getDeptId(), report.getStationId(),report.getDeviceName(),report.getStationName(),report.getDeptId()));
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


        // 使用校准后的数据来构建返回的 TableDataInfo 对象
        List<Map<String, Object>> calibratedData = data.stream()
                .map(row -> {
                    String deviceId = (String) row.get("deviceId");
                    // 根据 deviceId 和 dateTimeStr 查询校准后的数据
                    HourlyAverageAirData calibratedDataRecord = hourlyAverageAirDataRepository.findByDeviceIdAndCreatedAt(deviceId, dateTime);
                    if (calibratedDataRecord != null) {
                        Map<String, Object> calibratedMap = new HashMap<>();
                        calibratedMap.put("deviceId", calibratedDataRecord.getDeviceId());
                        calibratedMap.put("stationId", calibratedDataRecord.getStationId());
                        calibratedMap.put("deptId", calibratedDataRecord.getDeptId());
                        calibratedMap.put("deviceName", calibratedDataRecord.getDeviceName());
                        calibratedMap.put("stationName", calibratedDataRecord.getStationName());
                        calibratedMap.put("controlStation", deptIdToDeptNameMap.getOrDefault(calibratedDataRecord.getDeptId(), "未知站点"));
                        calibratedMap.put("averageAqi", calibratedDataRecord.getAverageAqi());
                        calibratedMap.put("averageSo2", calibratedDataRecord.getAverageSo2());
                        calibratedMap.put("averageNo2", calibratedDataRecord.getAverageNo2());
                        calibratedMap.put("averageO3", calibratedDataRecord.getAverageO3());
                        calibratedMap.put("averagePm2_5", calibratedDataRecord.getAveragePm25());
                        calibratedMap.put("averagePm10", calibratedDataRecord.getAveragePm10());
                        calibratedMap.put("level", getAqiLevel(Double.valueOf(calibratedDataRecord.getAverageAqi())));
                        calibratedMap.put("quality", getAqiQuality(Double.valueOf(calibratedDataRecord.getAverageAqi())));
                        calibratedMap.put("color", getAqiColor(Double.valueOf(calibratedDataRecord.getAverageAqi())));
                        if (calibratedDataRecord.getAverageAqi() > 50) {
                            calibratedMap.put("primaryPollutant", getPrimaryPollutant(pageReports.getContent().stream()
                                    .filter(report -> report.getDeviceId().equals(deviceId))
                                    .collect(Collectors.toList())));
                        } else {
                            calibratedMap.put("primaryPollutant", "-");
                        }
                        calibratedMap.put("dateTimeStr", dateTimeStr);
                        return calibratedMap;
                    } else {
                        return row; // 如果没有找到校准后的数据，使用原始数据
                    }
                })
                .collect(Collectors.toList());
        TableDataInfo tableDataInfo = new TableDataInfo();
        tableDataInfo.setCode(0); // Assuming success code is 0
        tableDataInfo.setMsg("success");
        tableDataInfo.setRows(calibratedData);
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
        //得到设备的经纬度信息 并保留小数点后两位

        Double longitude = reports.stream()
                .filter(Objects::nonNull)
                .map(AirDataHour::getLongitude)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);
        Double latitude = reports.stream()
                .filter(Objects::nonNull)
                .map(AirDataHour::getLatitude)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);

        // 根据 deptId 获取国控站点名称
        String controlStation = deptIdToDeptNameMap.getOrDefault(deptId, "未知站点");


        Map<String, Object> metrics = new HashMap<>();

        metrics.put("deviceId", deviceId);
        metrics.put("stationId", stationId);
        metrics.put("deptId", deptId);
        metrics.put("deviceName", deviceName);
        metrics.put("stationName", stationName);
        metrics.put("longitude", longitude);
        metrics.put("latitude", latitude);
        metrics.put("controlStation", controlStation);//添加国控站点名称
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
            hourlyAverageAirData.setAveragePm25((double) ((Number) row.get("averagePm2_5")).longValue());
            hourlyAverageAirData.setAveragePm10((double) ((Number) row.get("averagePm10")).longValue());
            hourlyAverageAirData.setAqiLevel((String) row.get("level"));
            hourlyAverageAirData.setAqiQuality((String) row.get("quality"));
            hourlyAverageAirData.setAqiColor((String) row.get("color"));
            hourlyAverageAirData.setPrimaryPollutant((String) row.get("primaryPollutant"));
            hourlyAverageAirData.setDeviceName((String) row.get("deviceName"));
            hourlyAverageAirData.setStationName((String) row.get("stationName"));

            // 数据校准逻辑
            String deptId = (String) row.get("deptId");
            String controlStation = deptIdToDeptNameMap.getOrDefault(deptId, "未知站点");
            if ("世纪豪园".equals(controlStation)) {
                logger.info("正在对世纪豪园的设备进行数据校准");
                // 对世纪豪园的设备进行数据校准
                hourlyAverageAirData = calibrateData(hourlyAverageAirData,controlStation);
            } else if ("五金库".equals(controlStation)) {
                // 对五金库国控站的设备进行数据校准
                hourlyAverageAirData = calibrateData(hourlyAverageAirData,controlStation);
            } else if ("烟厂".equals(controlStation)) {
                // 对烟厂国控站的设备进行数据校准
                hourlyAverageAirData = calibrateData(hourlyAverageAirData,controlStation);
            } else if ("北泵房".equals(controlStation)) {
                // 对北泵房国控站的设备进行数据校准
                hourlyAverageAirData = calibrateData(hourlyAverageAirData,controlStation);
            } else if ("人民公园".equals(controlStation)) {
                // 对人民公园国控站的设备进行数据校准
                hourlyAverageAirData = calibrateData(hourlyAverageAirData,controlStation);
            }

            Double averagePm25 =  hourlyAverageAirData.getAveragePm25();
            //System.out.println("averagePm25: " + averagePm25);
            Double averagePm10 =  hourlyAverageAirData.getAveragePm10();
            if (averagePm25 != null && averagePm10 != null && averagePm25 > 50 && averagePm10 > 50) {
                AlarmInfo alarmInfo = new AlarmInfo();
                alarmInfo.setStationId(Long.valueOf(stationId));
                alarmInfo.setDeviceId(Long.valueOf(deviceId));
                alarmInfo.setDeptId(Long.valueOf((String) row.get("deptId")));
                //根据stationId进行查询station表中的phone信息并把phone插入到alarmInfo表中
                // 根据stationId获取电话号码
                String phone = stationMapper.getPhoneByStationId(stationId);
                String userName = stationMapper.getNameByStationId(stationId);
                if (phone != null) {
                    alarmInfo.setPhoneNumber(phone);
                    alarmInfo.setUserName(userName);
                } else {

                    alarmInfo.setPhoneNumber(null);
                }

                alarmInfo.setStationName(hourlyAverageAirData.getStationName());
                alarmInfo.setDeviceName(hourlyAverageAirData.getDeviceName());
                alarmInfo.setAlarmType(2L);
                alarmInfo.setCreateDate(updateAt);
                alarmInfo.setStatus("设备高值");
                alarmInfo.setSmsMessage("设备" + deviceId + "数据高值，请及时处理");
                alarmInfoMapper.insertAlarmInfo(alarmInfo);
            } else if (averagePm25 != null && averagePm10 != null && averagePm25 < 30 && averagePm10 < 30) {
                AlarmInfo alarmInfo = new AlarmInfo();
                alarmInfo.setStationId(Long.valueOf(stationId));
                alarmInfo.setDeviceId(Long.valueOf(deviceId));
                alarmInfo.setDeptId(Long.valueOf((String) row.get("deptId")));
                //根据stationId进行查询station表中的phone信息并把phone插入到alarmInfo表中
                // 根据stationId获取电话号码
                String phone = stationMapper.getPhoneByStationId(stationId);
                String userName = stationMapper.getNameByStationId(stationId);
                if (phone != null) {
                    alarmInfo.setPhoneNumber(phone);
                    alarmInfo.setUserName(userName);
                } else {

                    alarmInfo.setPhoneNumber(null);
                }
                alarmInfo.setStationName(hourlyAverageAirData.getStationName());
                alarmInfo.setDeviceName(hourlyAverageAirData.getDeviceName());
                alarmInfo.setAlarmType(2L);
                alarmInfo.setCreateDate(updateAt);
                alarmInfo.setStatus("设备低值");
                alarmInfo.setSmsMessage("设备" + deviceId + "数据低值，请及时处理");
                alarmInfoMapper.insertAlarmInfo(alarmInfo);
            }

            hourlyAverageAirDataRepository.save(hourlyAverageAirData);
        }
    }
    private HourlyAverageAirData calibrateData(HourlyAverageAirData data, String controlStation) {
        // 实现具体的数据校准逻辑
        Double pm2_5 = data.getAveragePm25();
        logger.info("PM2.5 before calibration: {}", pm2_5);

        Double pm10 = data.getAveragePm10();
        logger.info("PM10 before calibration: {}", pm10);

        DataQueryCountry queryCountry = dataQueryCountryMapper.selectDataQueryCountryByName(controlStation);

        if (queryCountry == null) {
            logger.warn("Control station {} not found in the database", controlStation);
            return data;
        }

        Double pmCountry = queryCountry.getPm();
        logger.info("PM country value: {}", pmCountry);

        Double pm10Country = queryCountry.getPm10();
        logger.info("PM10 country value: {}", pm10Country);

        // Calculate differences
        Double countPm = pm2_5 - pmCountry;
        logger.info("Difference for PM2.5: {}", countPm);

        Double countPm10 = pm10 - pm10Country;
        logger.info("Difference for PM10: {}", countPm10);

        // Define a random fluctuation range (e.g., ±5% of the difference)
        double fluctuationRange = 0.05;

        // Adjust PM2.5
        if (countPm > 0) {
            pm2_5 -= countPm * (1 - getRandomFluctuation(fluctuationRange));
        } else {
            pm2_5 += Math.abs(countPm) * (1 - getRandomFluctuation(fluctuationRange));
        }
        pm2_5 = round(pm2_5); // 保留小数点后一位
        data.setAveragePm25(pm2_5);
        logger.info("PM2.5 after calibration: {}", pm2_5);

        // Adjust PM10
        if (countPm10 > 0) {
            pm10 -= countPm10 * (1 - getRandomFluctuation(fluctuationRange));
        } else {
            pm10 += Math.abs(countPm10) * (1 - getRandomFluctuation(fluctuationRange));
        }
        pm10 = round(pm10); // 保留小数点后一位
        data.setAveragePm10(pm10);
        logger.info("PM10 after calibration: {}", pm10);

        return data;
    }


    private double getRandomFluctuation(double range) {
        Random random = new Random();
        return random.nextDouble() * range * 2 - range; // Generates a value between -range and +range
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

        // 查询数据库中的校准数据
        List<HourlyAverageAirData> hourlyAverages = hourlyAverageAirDataRepository.findByDateAndDeviceId(date, deviceId);

        if (hourlyAverages.isEmpty()) {
            logger.warn("No data found for the specified device and date range.");
            return createEmptyTableDataInfo();
        } else {
            logger.info("Found {} records in total", hourlyAverages.size());
        }

        // 使用新的日期格式化器来包含小时信息
        SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        dateTimeFormat.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai")); // 明确指定时区

        // 将结果转换为TableDataInfo类型
        List<Map<String, Object>> data = hourlyAverages.stream()
                .map(avgData -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("hour", avgData.getCreatedAt().getHours());
                    map.put("deviceId", avgData.getDeviceId());
                    map.put("stationId", avgData.getStationId());
                    map.put("deptId", avgData.getDeptId());
                    map.put("averageAqi", avgData.getAverageAqi());
                    map.put("averageSo2", avgData.getAverageSo2());
                    map.put("averageNo2", avgData.getAverageNo2());
                    map.put("averageO3", avgData.getAverageO3());
                    map.put("averagePm2_5", avgData.getAveragePm25());
                    map.put("averagePm10", avgData.getAveragePm10());
                    map.put("level", avgData.getAqiLevel());
                    map.put("quality", avgData.getAqiQuality());
                    map.put("color", avgData.getAqiColor());
                    map.put("primaryPollutant", avgData.getPrimaryPollutant());
                    map.put("deviceName", avgData.getDeviceName());
                    map.put("stationName", avgData.getStationName());
                    // 使用新的日期格式化器来包含小时信息
                    map.put("dateTimeStr", dateTimeFormat.format(avgData.getCreatedAt()));
                    return map;
                })
                .collect(Collectors.toList());

        TableDataInfo tableDataInfo = new TableDataInfo();
        tableDataInfo.setCode(0); // Assuming success code is 0
        tableDataInfo.setMsg("success");
        tableDataInfo.setRows(data);
        tableDataInfo.setTotal(hourlyAverages.size());
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

        // 查询数据库中的校准数据
        HourlyAverageAirData hourlyAverage = hourlyAverageAirDataRepository.findByDeviceIdAndCreatedAt(deviceId, dateTime);

        if (hourlyAverage == null) {
            logger.warn("No data found for the specified device and time range.");
            return createEmptyTableDataInfo();
        } else {
            logger.info("Found record for device {} at {}", deviceId, dateTimeStr);
        }

        // 将结果转换为TableDataInfo类型
        Map<String, Object> metrics = new HashMap<>();
        metrics.put("dateTimeStr", dateTimeStr);
        metrics.put("deviceId", hourlyAverage.getDeviceId());
        metrics.put("stationId", hourlyAverage.getStationId());
        metrics.put("deptId", hourlyAverage.getDeptId());
        metrics.put("averageAqi", hourlyAverage.getAverageAqi());
        metrics.put("averageSo2", hourlyAverage.getAverageSo2());
        metrics.put("averageNo2", hourlyAverage.getAverageNo2());
        metrics.put("averageO3", hourlyAverage.getAverageO3());
        metrics.put("averagePm2_5", hourlyAverage.getAveragePm25());
        metrics.put("averagePm10", hourlyAverage.getAveragePm10());
        metrics.put("level", hourlyAverage.getAqiLevel());
        metrics.put("quality", hourlyAverage.getAqiQuality());
        metrics.put("color", hourlyAverage.getAqiColor());
        metrics.put("primaryPollutant", hourlyAverage.getPrimaryPollutant());
        metrics.put("deviceName", hourlyAverage.getDeviceName());
        metrics.put("stationName", hourlyAverage.getStationName());
        metrics.put("endTime", dateTimeFormat.format(hourlyAverage.getUpdatedAt()));

        List<Map<String, Object>> data = Collections.singletonList(metrics);
        TableDataInfo tableDataInfo = new TableDataInfo();
        tableDataInfo.setCode(0); // Assuming success code is 0
        tableDataInfo.setMsg("success");
        tableDataInfo.setRows(data);
        tableDataInfo.setTotal(1);

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
//            hourlyData.forEach(report -> logger.debug("Report: deviceId={}, aqi={}, so2={}, no2={}, co={}, o3={}, pm2_5={}, pm10={}, deptId={},stationId={}",
//                    report.getDeviceId(), report.getAqi(), report.getSo2Thickness(), report.getNo2Thickness(),
//                    report.getCo(), report.getCo3Thickness(), report.getPm25(), report.getPm10(), report.getDeptId(), report.getStationId()));
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