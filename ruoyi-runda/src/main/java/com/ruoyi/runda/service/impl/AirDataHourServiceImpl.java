package com.ruoyi.runda.service.impl;

import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.runda.domain.*;
import com.ruoyi.runda.mapper.*;
import com.ruoyi.runda.repository.AirDataHourRepository;
import com.ruoyi.runda.repository.DataQuery212OVRepository;
import com.ruoyi.runda.repository.DeviceDataRepository;
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
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class AirDataHourServiceImpl implements AirDataHourService {

// 在类顶部添加以下成员变量
//    private final ConcurrentHashMap<String, LinkedBlockingDeque<Double>> pm25Windows = new ConcurrentHashMap<>();
//    private final ConcurrentHashMap<String, LinkedBlockingDeque<Double>> pm10Windows = new ConcurrentHashMap<>();
//    private final ConcurrentHashMap<String, Double> pm25SumMap = new ConcurrentHashMap<>();
//    private final ConcurrentHashMap<String, Double> pm10SumMap = new ConcurrentHashMap<>();
//
//    private final Map<String, NavigableMap<Long, Double>> pm25History = new ConcurrentHashMap<>();
//    private final Map<String, NavigableMap<Long, Double>> pm10History = new ConcurrentHashMap<>();
//    private final Map  pm25Locks = new ConcurrentHashMap();


    private static final Logger logger = LoggerFactory.getLogger(AirDataHourServiceImpl.class);
    // 定义并初始化 SimpleDateFormat 对象
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    static {
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai")); // 明确指定时区
    }

    // 定义并初始化部门ID到部门名称的映射
    private static final Map<String, String> deptIdToDeptNameMap = new HashMap<>();

    static {
        deptIdToDeptNameMap.put("3", "五金库"); // 东城区-五金库
        deptIdToDeptNameMap.put("18002", "怀来北辰佳苑"); // 怀来县-怀来北辰佳苑
        deptIdToDeptNameMap.put("18003", "蔚县职教中心");//蔚县-蔚县玉皇阁西
        deptIdToDeptNameMap.put("18070", "蔚县职教中心");//蔚县-蔚县玉皇阁西
        deptIdToDeptNameMap.put("18007", "怀安环境分局");//怀安县-怀安环境分局
        deptIdToDeptNameMap.put("18071", "怀安环境分局");//怀安县-怀安环境分局
        deptIdToDeptNameMap.put("18011", "世纪豪园");//桥东区-世纪豪园
        deptIdToDeptNameMap.put("18114", "世纪豪园");//桥东区-世纪豪园
        deptIdToDeptNameMap.put("18072", "世纪豪园");
        deptIdToDeptNameMap.put("18115", "人民公园");//桥西区-人民公园
        deptIdToDeptNameMap.put("18013", "烟厂");//经开区-烟厂
        deptIdToDeptNameMap.put("18116", "烟厂");//经开区-烟厂
        deptIdToDeptNameMap.put("18014", "宣化军营凤凰城"); // 长安区 宣化区 -宣化军营凤凰城
        deptIdToDeptNameMap.put("18016", "崇礼梦特芳丹酒店");//崇礼区-崇礼梦特芳丹酒店
        deptIdToDeptNameMap.put("18077", "崇礼梦特芳丹酒店");//崇礼区-崇礼梦特芳丹酒店
        deptIdToDeptNameMap.put("18017", "下花园环境分局");//下花园区-下花园环境分局
        deptIdToDeptNameMap.put("18018", "涿鹿县政府");//涿鹿县-涿鹿县政府
        deptIdToDeptNameMap.put("18019", "赤城北山");//赤城县-赤城北山
        deptIdToDeptNameMap.put("18020", "阳原人民政府");//阳原县-阳原人民政府
        deptIdToDeptNameMap.put("18021", "万全环境分局");//万全县- 万全环境分局
        deptIdToDeptNameMap.put("18022", "尚义第二中学");//尚义县-尚义第二中学
        deptIdToDeptNameMap.put("18023", "康保环境分局");//康保县-康保环境分局
        deptIdToDeptNameMap.put("18024", "张北环境分局");//张北县-张北环境分局
        deptIdToDeptNameMap.put("18025", "沽源县人民政府办公楼");//沽源县-沽源县人民政府办公楼
        deptIdToDeptNameMap.put("18026", "沽源第一中学");//察北管理区-沽源第一中学
        deptIdToDeptNameMap.put("18079", "涿鹿县政府");//涿鹿县-涿鹿县政府
        deptIdToDeptNameMap.put("18081", "阳原人民政府");//阳原县-阳原人民政府
        deptIdToDeptNameMap.put("18114 ", "世纪豪园");//阳原县-阳原人民政府

    }
    @Autowired
    private AirDataHourRepository airDataHourRepository;

    @Autowired
    private HourlyAverageAirDataRepository hourlyAverageAirDataRepository; // MySQL Repository

    @Autowired
    private AlarmInfoMapper alarmInfoMapper;


     @Autowired
     private HourlyAverageAirDataMapper hourlyAverageAirDataMapper;

    @Autowired
    private  StationMapper stationMapper;

    @Autowired
    private DataQueryCountryMapper dataQueryCountryMapper;
    @Autowired
    private DataQuery212OVRepository dataQuery212OVRepository;

    @Autowired
   private DeviceMapper deviceMapper;




    // 在方法开头增加时间参数转换

    //调用calculateAverageForSpecificDateTime方法
    @Scheduled(cron = "0 5 * * * ?")
    //每5分钟执行
//    @Scheduled(cron = "0 0/5 * * * ?")
    public  void  getAirData() throws Exception {
        LocalDateTime nowDate = LocalDateTime.now();
        // 提取年、月、日和小时
        int year = nowDate.getYear();
        int month = nowDate.getMonthValue();
        int day = nowDate.getDayOfMonth();
        int hour = nowDate.getHour()-1;

        //拼接成字符串形式
        String dateTimeStr = String.format("%d-%02d-%02d %02d:00", year, month, day, hour);
        calculateAverageForSpecificDateTime(dateTimeStr);
    }

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
        Page<DataQuery212> dataPage = dataQuery212OVRepository.findByCreateDateBetween(startDateMillis, endDateMillis, pageable);
        // 合并 DataQuery212 和 AirDataHour 数据
        List<AirDataHour> combinedData = new ArrayList<>();
        combinedData.addAll(dataPage.getContent().stream().map(dataQuery212 -> {
            AirDataHour airDataHour = new AirDataHour();
            airDataHour.setDeviceId(dataQuery212.getDeviceId());
            airDataHour.setStationId(String.valueOf(dataQuery212.getStationId()));
            airDataHour.setDeptId(String.valueOf(dataQuery212.getDeptId()));
            airDataHour.setDeviceName(dataQuery212.getDeviceName());
            airDataHour.setStationName(dataQuery212.getStationName());
            airDataHour.setAqi(dataQuery212.getAqi());
            airDataHour.setSo2Thickness(dataQuery212.getSo2Thickness());
            airDataHour.setNo2Thickness(dataQuery212.getNo2Thickness());
            airDataHour.setCoThickness(dataQuery212.getCoThickness());
            airDataHour.setCo3Thickness(dataQuery212.getCo3Thickness());
            airDataHour.setPm25(dataQuery212.getPm2_5());
            airDataHour.setPm10(dataQuery212.getPm10());
            airDataHour.setWd(String.valueOf(dataQuery212.getTemperature()));
            airDataHour.setSd(String.valueOf(dataQuery212.getHumidity()));
            return airDataHour;
        }).collect(Collectors.toList()));
        logger.info("Combined data size: {}", combinedData.size());
        if (combinedData.isEmpty()) {
            logger.info("No data found for the specified date and time.");
            // 如果没有数据，则通过hourlyAverageAirDataRepository.findByDateTime 方法查询数据库本身是否有存在信息如果有增返回数据并能正常展示
            List<HourlyAverageAirData> hourlyAverageAirDataList = hourlyAverageAirDataRepository.findByDateTime(dateTime);
            if (!hourlyAverageAirDataList.isEmpty()) {
                // 处理数据
                List<Map<String, Object>> data = hourlyAverageAirDataList.stream()
                        .map(avgData -> {
                            Map<String, Object> map = new HashMap<>();
                            map.put("deviceId", avgData.getDeviceId());
                            map.put("stationId", avgData.getStationId());
                            map.put("deptId", avgData.getDeptId());
                            map.put("deviceName", avgData.getDeviceName());
                            map.put("stationName", avgData.getStationName());
                            map.put("tempture",avgData.getWd());
                            map.put("humidity",avgData.getSd());
                            map.put("controlStation", deptIdToDeptNameMap.getOrDefault(avgData.getDeptId(), "未知站点"));
                            map.put("averagePm25_24", avgData.getAveragePm25_24()); //pm2.524
                            map.put("averagePm10_24", avgData.getAveragePm10_24()); //pm10.24
                            map.put("averageAqi", avgData.getAverageAqi());
                            map.put("averageSo2", avgData.getAverageSo2());
                            map.put("averageNo2", avgData.getAverageNo2());
                            map.put("averageO3", avgData.getAverageO3());
                            map.put("averagePm2_5", avgData.getAveragePm25());
                            map.put("averagePm10", avgData.getAveragePm10());
                            map.put("level", getAqiLevel(Double.valueOf(avgData.getAverageAqi())));
                            map.put("quality", getAqiQuality(Double.valueOf(avgData.getAverageAqi())));
                            map.put("color", getAqiColor(Double.valueOf(avgData.getAverageAqi())));

                            if (avgData.getAverageAqi() > 50) {
                                map.put("primaryPollutant", getPrimaryPollutant(combinedData.stream()
                                        .filter(report -> report.getDeviceId().equals(avgData.getDeviceId()))
                                        .collect(Collectors.toList())));
                            } else {
                                map.put("primaryPollutant", "-");
                            }
                            map.put("dateTimeStr", dateTimeStr);
                            return map;
                        })
                        .collect(Collectors.toList());

                TableDataInfo resultTableDataInfo = new TableDataInfo();
                resultTableDataInfo.setCode(0); // Assuming success code is 0
                resultTableDataInfo.setMsg("success");
                resultTableDataInfo.setRows(data);
                resultTableDataInfo.setTotal(hourlyAverageAirDataList.size());
                return resultTableDataInfo;
            } else {
                return createEmptyTableDataInfo();
            }
        } else {
            logger.info("Found {} records in total", combinedData.size());
        }


        // 计算每个设备指定日期时间内的各项指标平均值
        Map<String, Map<String, Object>> averages = combinedData.stream()
                .collect(Collectors.groupingBy(AirDataHour::getDeviceId))
                .entrySet().stream()
                .map(entry -> calculateMetrics(entry, dateTime))  // 传入endTime参数
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
            //打印出 有多少个设备
        logger.info("Number of devices: {}", averages.size());


        // 根据AQI平均值进行排名（AQI值越低，排名越高）
        List<Map.Entry<String, Map<String, Object>>> rankedList = averages.entrySet().stream()
                .sorted(Comparator.comparingDouble(entry -> round((Double) entry.getValue().get("averageAqi")))) // 按AQI升序排列
                .map(entry -> {
                    entry.getValue().put("rank", entry.getValue().getOrDefault("rank", 0));
                    return entry;
                })
                .collect(Collectors.toList());

//        for (int i = 0; i < rankedList.size(); i++) {
//            rankedList.get(i).getValue().put("rank", i + 1);
//        }

        // 将结果转换为TableDataInfo类型
        List<Map<String, Object>> data = rankedList.stream()
                .map(entry -> {
                    Map<String, Object> map = new HashMap<>();
                    map.putAll(entry.getValue());
                    map.put("dateTimeStr", dateTimeStr);
                    return map;
                })
                .collect(Collectors.toList());
        logger.info("Number of data1: {}", data.size());

        // 将平均数据保存到MySQL数据库中
        saveToMysql(data);
        //打印data的条数 及对应的deviceId
        // 使用校准后的数据来构建返回的 TableDataInfo 对象
        List<Map<String, Object>> calibratedData = data.stream()
                .map(row -> {
                    String deviceId = (String) row.get("deviceId");

                    if (deviceId == null || deviceId.isEmpty()) {
                        // 如果 deviceId 为空，跳过当前记录
                        return null;
                    }
                    // 根据 deviceId 和 dateTimeStr 查询校准后的数据
                    HourlyAverageAirData calibratedDataRecord = hourlyAverageAirDataRepository.findByDeviceIdAndCreatedAt(deviceId, dateTime);
                    if (calibratedDataRecord != null) {
                        Map<String, Object> calibratedMap = new HashMap<>();
                        calibratedMap.put("deviceId", calibratedDataRecord.getDeviceId());
                        calibratedMap.put("stationId", calibratedDataRecord.getStationId());
                        calibratedMap.put("deptId", calibratedDataRecord.getDeptId());
                        calibratedMap.put("tempture", calibratedDataRecord.getWd());
                        calibratedMap.put("humidity", calibratedDataRecord.getSd());
                        calibratedMap.put("deviceName", calibratedDataRecord.getDeviceName());
                        calibratedMap.put("stationName", calibratedDataRecord.getStationName());
                        calibratedMap.put("controlStation", deptIdToDeptNameMap.getOrDefault(calibratedDataRecord.getDeptId(), "未知站点"));
                        calibratedMap.put("averagePm25_24",calibratedDataRecord.getAveragePm25_24());
                        calibratedMap.put("averagePm10_24", calibratedDataRecord.getAveragePm10_24());
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
                            calibratedMap.put("primaryPollutant", getPrimaryPollutant(combinedData.stream()
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
        tableDataInfo.setTotal(combinedData.size());
        return tableDataInfo;
    }







    private Map.Entry<String, Map<String, Object>> calculateMetrics(Map.Entry<String, List<AirDataHour>> entry, Date endTime){
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

        //得到温度 湿度信息 并保留整数
        String wd = reports.stream()
                .filter(Objects::nonNull)
                .map(AirDataHour::getWd)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);
        String sd = reports.stream()
                .filter(Objects::nonNull)
                .map(AirDataHour::getSd)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);

        // 根据 deptId 获取国控站点名称
        String controlStation = deptIdToDeptNameMap.getOrDefault(deptId, "未知站点");




        Map<String, Double> slidingAvg = calculate24HourSlidingAverage(reports, endTime);



        Map<String, Object> metrics = new HashMap<>();

        metrics.put("deviceId", deviceId);
        metrics.put("stationId", stationId);
        metrics.put("deptId", deptId);
        metrics.put("wd", wd);
        metrics.put("sd",sd);
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
        metrics.put("averagePm25_24", slidingAvg.get("pm2_5_24h"));
        metrics.put("averagePm10_24", slidingAvg.get("pm10_24h"));
        Double averageAqi =  (Double) metrics.put("averageAqi",slidingAvg.get("pm2_5_24h"));//aqi同pm25
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

    // 新增方法：计算24小时滑动平均值
    // 新增方法：基于内存计算的24小时滑动平均
    private Map<String, Double> calculate24HourSlidingAverage(List<AirDataHour> reports, Date endTime) {
        Date startTime = new Date(endTime.getTime() - 1000L * 60 * 60 * 24);//获取当前时间 前24小时
        Map<String, Double> result = new HashMap<>();
        List<Map<String, Object>> avg =  hourlyAverageAirDataMapper.calculateDailyHourlyAverage(reports.get(0).getDeviceId(),startTime,endTime);
        if (avg == null || avg.isEmpty() || avg.get(0) == null) {
            result.put("pm2_5_24h", Double.NaN);
            result.put("pm10_24h", Double.NaN);
            return result;
        }else{
           Double avg25_24 = roundDouble((Double) avg.get(0).get("avg25"));
           Double avg10_24 = roundDouble((Double) avg.get(0).get("avg10"));


           result.put("pm2_5_24h",avg25_24 );
           result.put("pm10_24h", avg10_24);
//           logger.info("deviceId:{},pm2_5_24h:{},pm10_24h{}",reports.get(0).getDeviceId(),avg25_24,avg10_24);
       }

        return result;
    }
//    private Map<String, Double> calculate24HourSlidingAverage(List<AirDataHour> reports, Date endTime) {
//         //获取设备ID（假设当前分组处理的是单个设备）
//         String deviceId = reports.get(0).getDeviceId();
//        // 获取时间范围（上海时区）
////
//        Date firstTime = new Date(endTime.getTime() - 1000L * 60 * 60 * 1);//获取当前时间 前1小时
//
//        HourlyAverageAirData hourlyAverageAirData = hourlyAverageAirDataRepository.findByDeviceIdAndCreatedAt(deviceId,firstTime);
//        Map<String, Double> result = new HashMap<>();
//        if (hourlyAverageAirData != null) {
//
//            result.put("pm2_5_24h", hourlyAverageAirData.getAveragePm25_24().doubleValue()*24);
//            result.put("pm10_24h", hourlyAverageAirData.getAveragePm10_24().doubleValue()*24);
//        }
//        Date secondTime = new Date(endTime.getTime() - 1000L * 60 * 60 * 25); //获取当前时间 前25小时
//        DataQuery212 dataQuery212 = dataQuery212OVRepository.findByDeviceIdAndCreateDate(deviceId,secondTime);
//        if (dataQuery212 != null) {
//            result.put("pm2_5_24h", result.get("pm2_5_24h")-dataQuery212.getPm2_5());
//            result.put("pm10_24h", result.get("pm10_24h")-dataQuery212.getPm10());
//        }
//        DataQuery212 current= dataQuery212OVRepository.findByDeviceIdAndCreateDate(deviceId,endTime);
//        if (current != null) {
//            result.put("pm2_5_24h", (result.get("pm2_5_24h")+current.getPm2_5())/24);
//            result.put("pm10_24h", (result.get("pm10_24h")+current.getPm10())/24);
//        }
//        return result;
//
//    }
//    public Map<String, Double> calculate24HourSlidingAverage(List<AirDataHour> reports, Date endTime) {
//        // 获取设备ID（假设当前分组处理的是单个设备）
//        String deviceId = reports.get(0).getDeviceId();
//
//        // 获取时间范围（上海时区）
//        Calendar endCalendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Shanghai"));
//        endCalendar.setTime(endTime);
//        long endMillis = endCalendar.getTimeInMillis();
//        long startMillis = endMillis - 86_400_000L; // 24小时前
//
//        // 1. 查询原始数据（按时间升序排列）
//        Page<DataQuery212> allData = dataQuery212OVRepository.findByDeviceIdAndCreateDateBetween(
//                deviceId,
//                startMillis,
//                endMillis,
//                PageRequest.of(0, Integer.MAX_VALUE, Sort.by(Sort.Direction.ASC, "createTime")) // 明确按时间升序
//        );
//        List<DataQuery212> dataList = allData.getContent();
//        logger.info("原始数据条数：" + dataList.size());
//        // 2. 构建滑动窗口队列
//        LinkedList<DataQuery212> window = new LinkedList<>();
//        Map<String, Double> result = new HashMap<>();
//        int count = 0;
//        for (DataQuery212 current : dataList) {
//            count++;
//            if(count%12==0){ //取每隔一小时取一次滑动平均（一个小时12条数据
//                window.addLast(current);
//            }
//        }
//        logger.info("滑动窗口大小:{}", window.size());
//        if (window.size() >= 24) {
//            double pm25Sum = window.stream().mapToDouble(DataQuery212::getPm2_5).sum();
//            double pm10Sum = window.stream().mapToDouble(DataQuery212::getPm10).sum();
//
//            result.put("pm2_5_24h", roundDouble(pm25Sum / window.size()));
//            result.put("pm10_24h", roundDouble(pm10Sum / window.size()));
//        }// 4. 当窗口有足够数据时计算（至少覆盖24小时）
//        // 处理边界情况（数据不足24小时）
//        if (result.isEmpty() && !window.isEmpty()) {
//            double pm25Avg = window.stream().mapToDouble(DataQuery212::getPm2_5).average().orElse(0);
//            double pm10Avg = window.stream().mapToDouble(DataQuery212::getPm10).average().orElse(0);
//            result.put("pm2_5_24h", roundDouble(pm25Avg));
//            result.put("pm10_24h", roundDouble(pm10Avg));
//        }
//
//        return result;
//    }

    // 辅助方法：四舍五入保留两位小数
    private double roundDouble(double value) {
        return BigDecimal.valueOf(value)
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();
    }




//    public Map<String, Double> calculate24HourSlidingAverage(String deviceId, Date endTime) {
//        // 创建上海时区Calendar对象
//        Calendar endCalendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Shanghai"));
//        endCalendar.setTime(endTime);
//
//        // 克隆结束日历对象来计算开始时间
//        Calendar startCalendar = (Calendar) endCalendar.clone();
//        startCalendar.add(Calendar.HOUR_OF_DAY, -24); // 减去24小时
//
//        // 获取时间戳数值
//        long startMillis = startCalendar.getTimeInMillis();
//        long endMillis = endCalendar.getTimeInMillis();
//
//        Map<String, Double> result = dataQuery212OVRepository.calculate24HAverages(
//                deviceId,
//                startMillis,
//                endMillis
//        );
//
//        return Collections.unmodifiableMap(new HashMap<String, Double>() {{
//            put("pm2_5_24h", formatDouble(result.getOrDefault("avgPm25", 0.0)));
//            put("pm10_24h", formatDouble(result.getOrDefault("avgPm10", 0.0)));
//        }});
//    }


    private double formatDouble(double value) {
        return Double.isNaN(value) ? 0.0d :
                new BigDecimal(value).setScale(2, RoundingMode.HALF_UP).doubleValue();
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
        logger.info("Saving {} records to MySQL...", data.size());

        // 用于统计跳过的记录
        Set<String> skippedDeviceIds = new HashSet<>();
        Set<String> skippedStationIds = new HashSet<>();
        int skippedDueToDeviceId = 0;
        int skippedDueToStationId = 0;
        int skippedDueToExists = 0;
        int skippedDueToDeviceNotExist = 0;


        for (Map<String, Object> row : data) {
            String deviceId = (String) row.get("deviceId");

            if (deviceId == null || deviceId.isEmpty()) {
                logger.error("Device ID is null or empty: {}", row);
                skippedDeviceIds.add("null_or_empty");
                skippedDueToDeviceId++;
                continue;
            }

            // 确认 stationId 是否存在
            String stationId = (String) row.get("stationId");
            if (stationId == null || stationId.isEmpty()) {
                logger.warn("stationId is missing or null for stationId: {}", stationId);
                skippedStationIds.add("null_or_empty");
                skippedDeviceIds.add(deviceId);
                skippedDueToStationId++;
                continue; // 如果 stationId 缺失或为空，则跳过当前记录
            }
            //计算createAt 时间，应该与data中的dateTimeStr一样
            Date createAt = dateTimeFormat.parse((String) row.get("dateTimeStr"));

            // 检查数据库中是否已存在相同记录
            boolean exists = hourlyAverageAirDataRepository.existsByDeviceIdAndCreatedAtCustom(deviceId, createAt);
            if (exists) {
                logger.info("Record with deviceId {} and createdAt {} already exists. Skipping insertion.", deviceId, createAt);
                skippedDeviceIds.add(deviceId);
                skippedStationIds.add(stationId);
                skippedDueToExists++;
                continue;
            }


            // 计算 updateAt 时间，比 createAt 晚 59 分钟
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(createAt);
            calendar.add(Calendar.MINUTE, 59);
            Date updateAt = calendar.getTime();
            HourlyAverageAirData hourlyAverageAirData = new HourlyAverageAirData();
            hourlyAverageAirData.setType(2);
            hourlyAverageAirData.setDeviceId(deviceId);
            hourlyAverageAirData.setCreatedAt(createAt); // 设置 create_at 字段
            hourlyAverageAirData.setUpdatedAt(updateAt); // 设置 update_at 字段
            hourlyAverageAirData.setStationId(stationId); // 添加 stationId 字段
            hourlyAverageAirData.setWd((String) row.get("wd"));
            hourlyAverageAirData.setSd((String) row.get("sd"));
            hourlyAverageAirData.setDeptId((String) row.get("deptId"));
            hourlyAverageAirData.setAverageAqi(((Number) row.get("averageAqi")).longValue());
            hourlyAverageAirData.setAverageSo2(((Number) row.get("averageSo2")).longValue());
            hourlyAverageAirData.setAverageNo2(((Number) row.get("averageNo2")).longValue());
            hourlyAverageAirData.setAverageO3(((Number) row.get("averageO3")).longValue());
            hourlyAverageAirData.setAveragePm25((double) ((Number) row.get("averagePm2_5")).longValue());
            hourlyAverageAirData.setAveragePm10((double) ((Number) row.get("averagePm10")).longValue());
            hourlyAverageAirData.setAveragePm25_24((double) ((Number) row.get("averagePm25_24")).longValue());
            hourlyAverageAirData.setAveragePm10_24((double) ((Number) row.get("averagePm10_24")).longValue());
            hourlyAverageAirData.setAqiLevel((String) row.get("level"));
            hourlyAverageAirData.setAqiQuality((String) row.get("quality"));
            hourlyAverageAirData.setAqiColor((String) row.get("color"));
            hourlyAverageAirData.setPrimaryPollutant((String) row.get("primaryPollutant"));
            hourlyAverageAirData.setDeviceName((String) row.get("deviceName"));
            hourlyAverageAirData.setStationName((String) row.get("stationName"));

            // 检查设备是否存在
            Device existingDevice = deviceMapper.selectDeviceById(Long.valueOf(deviceId));
            if (existingDevice == null) {
                logger.error("Device with ID {} does not exist", deviceId);
                skippedDeviceIds.add(deviceId);
                skippedStationIds.add(stationId);
                skippedDueToDeviceNotExist++;
                continue; // 跳过当前记录
            }


            Long isYunwei = deviceMapper.getIsYunwei(deviceId);

            if (isYunwei==1L || isYunwei==null) {

            // 数据校准逻辑
            String deptId = (String) row.get("deptId"); // 获取部门ID
            if (deptId == null) {
                throw new IllegalArgumentException("deptId不能为空");
            }

                String controlStation = deptIdToDeptNameMap.getOrDefault(deptId, "世纪豪园");

                // 定义需要校准的站点集合
                Set<String> calibrationStations = Set.of(
                        "五金库", "怀来北辰佳苑", "蔚县职教中心", "怀安环境分局", "世纪豪园",
                        "人民公园", "烟厂", "宣化军营凤凰城", "崇礼梦特芳丹酒店", "下花园环境分局",
                        "涿鹿县政府", "赤城北山", "阳原人民政府", "万全环境分局", "尚义第二中学",
                        "康保环境分局", "张北环境分局", "沽源县人民政府办公楼", "沽源第一中学",
                        "青少年活动中心", "阳原职教中心", "赤城御福庄园", "万全第三初级中学", "下花园区医院"
                );

//             判断是否需要校准
                if (calibrationStations.contains(controlStation)) {
                    hourlyAverageAirData = calibrateData(hourlyAverageAirData, controlStation);
                }
                //0->低值 1->高值 2->正常
                if (hourlyAverageAirData.getType() == 0) {
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
                } else if (hourlyAverageAirData.getType() == 1) {
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
                }
            }
            hourlyAverageAirDataRepository.save(hourlyAverageAirData);
        }
        // 记录跳过的统计信息
        logger.info("Skipped records summary:");
        logger.info("Total skipped records: {}",
                skippedDueToDeviceId + skippedDueToStationId + skippedDueToExists + skippedDueToDeviceNotExist);
        logger.info("Skipped due to missing/empty deviceId: {} (deviceIds: {})",
                skippedDueToDeviceId, skippedDeviceIds);
        logger.info("Skipped due to missing/empty stationId: {} (stationIds: {})",
                skippedDueToStationId, skippedStationIds);
        logger.info("Skipped due to existing records: {} (deviceIds: {})",
                skippedDueToExists, skippedDeviceIds);
        logger.info("Skipped due to device not exist: {} (deviceIds: {})",
                skippedDueToDeviceNotExist, skippedDeviceIds);
    }
    private HourlyAverageAirData calibrateData(HourlyAverageAirData data, String controlStation) {

        Double pm2_5 = data.getAveragePm25();
//        logger.info("PM2.5 before calibration: {}", pm2_5);

        Double pm10 = data.getAveragePm10();
//        logger.info("PM10 before calibration: {}", pm10);
//        Date date =data.getCreatedAt();
        List<DataQueryCountry> queryCountries = dataQueryCountryMapper.selectDataQueryCountryByName(controlStation);

        DataQueryCountry queryCountry = queryCountries.stream()
                .findFirst()
                .orElse(null);

        if (queryCountry == null) {
//            logger.warn("Control station {} not found in the database", controlStation);
            return data;
        }

        Double pmCountry = queryCountry.getPm();


        Double pm10Country = queryCountry.getPm10();


        // Calculate differences
        Double countPm = pm2_5 - pmCountry;


        Double countPm10 = pm10 - pm10Country;


        // Define a random fluctuation range (e.g., ±5% of the difference)
        double fluctuationRange = 0.15;

        // Adjust PM2.5
        if (countPm > 0) {
            pm2_5 -= countPm * (1 - getRandomFluctuation(fluctuationRange));
        } else {
            pm2_5 += Math.abs(countPm) * (1 - getRandomFluctuation(fluctuationRange));
        }
        pm2_5 = round(pm2_5); // 保留小数点后一位
        if(pm2_5>0){
            data.setAveragePm25(pm2_5);
        }else {
            data.setAveragePm25(pmCountry);
        }

//        logger.info("PM2.5 after calibration: {}", pm2_5);

        // Adjust PM10
        if (countPm10 > 0) {
            pm10 -= countPm10 * (1 - getRandomFluctuation(fluctuationRange));
        } else {
            pm10 += Math.abs(countPm10) * (1 - getRandomFluctuation(fluctuationRange));
        }
        pm10 = round(pm10); // 保留小数点后一位
        if(pm10>0){
            data.setAveragePm10(pm10);
        }else {
            data.setAveragePm10(pm10Country);
        }

        // 将设置type的逻辑移到校准之后，使用校准后的pm2_5值进行比较
        if(pm2_5 > pmCountry + 60){
            data.setType(1);
        } else if (pm2_5 < pmCountry - 60) {
            data.setType(0);
        }

        return data;
    }



    private double getRandomFluctuation(double range) {
        Random random = new Random();
        return random.nextDouble() * range * 2 - range; // Generates a value between -range and +range
    }



    @Override
    public void exportToExcel(HttpServletResponse response, List<HourlyAverageAirData> dataList) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("AirHourData");

        // 创建标题行
        Row headerRow = sheet.createRow(0);
        String[] headers = {"deviceId", "deviceName", "averageAqi", "averageSo2", "averageO3", "averageNo2", "averagePm2_5", "averagePm10", "primaryPollutan", "level", "quality", "color", "dateTimeStr","averagePm10_24","averagePm2.5_24"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
        }

        // 填充数据
        int rowNum = 1;
        for (HourlyAverageAirData data : dataList) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(data.getDeviceId());
            row.createCell(1).setCellValue(data.getDeviceName());
            row.createCell(3).setCellValue(data.getAverageAqi() != null ? String.valueOf(data.getAverageAqi()) : "");
            row.createCell(4).setCellValue(data.getAverageSo2() != null ? String.valueOf(data.getAverageSo2()) : "");
            row.createCell(5).setCellValue(data.getAverageO3() != null ? String.valueOf(data.getAverageO3()) : "");
            row.createCell(6).setCellValue(data.getAverageNo2() != null ? String.valueOf(data.getAverageNo2()) : "");
            row.createCell(7).setCellValue(data.getAveragePm25() != null ? String.valueOf(data.getAveragePm25()) : "");
            row.createCell(8).setCellValue(data.getAveragePm10() != null ? String.valueOf(data.getAveragePm10()) : "");
            row.createCell(9).setCellValue(data.getPrimaryPollutant() != null ? data.getPrimaryPollutant().toString() : "");
            row.createCell(10).setCellValue(data.getAqiLevel() != null ? data.getAqiLevel().toString() : "");
            row.createCell(11).setCellValue(data.getAqiQuality() != null ? data.getAqiQuality().toString() : "");
            row.createCell(12).setCellValue(data.getAqiColor() != null ? data.getAqiColor().toString() : "");
            row.createCell(13).setCellValue(data.getCreatedAt() != null ? String.valueOf(data.getCreatedAt()) : "");
            row.createCell(14).setCellValue(data.getAveragePm10_24() != null ? data.getAveragePm10_24().toString() : "");
            row.createCell(15).setCellValue(data.getAveragePm25_24() != null ? data.getAveragePm25_24().toString() : "");
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
    public List<HourlyAverageAirData> selectDataList(Date beginDate, Date endDate) throws ParseException {

        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Shanghai"));
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        dateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
//        SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
//        dateTimeFormat.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai")); // 明确指定时区

        // 调用 Repository 方法，传入 Date 类型参数
        return hourlyAverageAirDataRepository.findByDate(beginDate,endDate);
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
        metrics.put("averagePm2_5_24h",hourlyAverage.getAveragePm25_24());
        metrics.put("averagePm10_24h",hourlyAverage.getAveragePm10_24());

        List<Map<String, Object>> data = Collections.singletonList(metrics);
        TableDataInfo tableDataInfo = new TableDataInfo();
        tableDataInfo.setCode(0); // Assuming success code is 0
        tableDataInfo.setMsg("success");
        tableDataInfo.setRows(data);
        tableDataInfo.setTotal(1);

        logger.debug("Exiting calculateHourlyAverageForSpecificTime method");
        return tableDataInfo;
    }


}