package com.ruoyi.runda.service.impl;

import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.runda.domain.*;
import com.ruoyi.runda.mapper.*;
import com.ruoyi.runda.repository.DeviceDataRepository;
import com.ruoyi.runda.repository.HourlyAverageAirDataRepository;
import com.ruoyi.runda.service.DeviceDataService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
@Slf4j
@Service
public class DeviceDataServiceImpl implements DeviceDataService {

    @Autowired
    private DeviceDataRepository deviceDataRepository;
    @Autowired
    private HourlyAverageAirDataMapper hourlyAverageAirDataMapper;
    @Autowired
    private StationMapper stationMapper;
    @Autowired
    private HourlyAverageAirDataRepository hourlyAverageAirDataRepository;
    @Autowired
    private DeviceMapper deviceMapper;

    @Autowired
    private AlarmInfoMapper alarmInfoMapper;
    @Autowired
    private DataQueryCountryMapper dataQueryCountryMapper;

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
    @Scheduled(cron = "0 2 * * * ?")
//@Scheduled(cron = "0 0/5 * * * ?")
    public  void  getDeviceData() throws Exception {
        LocalDateTime nowDate = LocalDateTime.now();
        // 提取年、月、日和小时
        int year = nowDate.getYear();
        int month = nowDate.getMonthValue();
        int day = nowDate.getDayOfMonth();
        int hour = nowDate.getHour()-1;

        //拼接成字符串形式
        String dateTimeStr = String.format("%d-%02d-%02d %02d:00", year, month, day, hour);
        listDeviceData(dateTimeStr);
    }
    @Override
    public TableDataInfo listDeviceData(String dateTimeStr) {
        try {
            // 解析输入时间为Date对象
            Date endTime = DateUtils.parseDate(dateTimeStr, "yyyy-MM-dd HH:mm");
            // 计算开始时间(1小时前)
            Date startTime = new Date(endTime.getTime() - 3600 * 1000);

            // 转换为时间戳(毫秒)
            long startTimestamp = startTime.getTime();
            long endTimestamp = endTime.getTime();

            // 获取原始数据
            List<AirDataHour> rawData = deviceDataRepository.findByCreateDateBetween(startTimestamp, endTimestamp);
            // 计算小时平均值
            List<AirDataHour> result = calculateHourlyAvg(rawData, dateTimeStr);
            return new TableDataInfo(result, result.size());

        } catch (ParseException e) {
            return null;
        }
    }

    /**
     * 计算小时平均数据
     * @param rawData 原始数据列表
     * @return 小时平均数据列表
     */
    private List<AirDataHour> calculateHourlyAvg(List<AirDataHour> rawData, String dateStr) {
        // 按设备ID分组
        Map<String, List<AirDataHour>> groupedByDevice = rawData.stream()
                .collect(Collectors.groupingBy(AirDataHour::getDeviceId));
            //打印设备信息的deptId
            groupedByDevice.forEach((deviceId, deviceDataList) -> {
                System.out.println("设备ID：" + deviceId);
                System.out.println("设备数据列表：" + deviceDataList);
            });
        List<AirDataHour> result = new ArrayList<>();

        // 计算每个设备的平均值
        groupedByDevice.forEach((deviceId, deviceDataList) -> {

            if (!deviceDataList.isEmpty()) {
                AirDataHour avgData = new AirDataHour();
                AirDataHour sampleData = deviceDataList.get(0);

                // 设置设备基本信息
                avgData.setId(sampleData.getId());
                avgData.setDeviceId(deviceId);
                avgData.setStationId(sampleData.getStationId());
                avgData.setDeptId(sampleData.getDeptId());
                avgData.setDeviceName(sampleData.getDeviceName());
                avgData.setStationName(sampleData.getStationName());

                try {
                    Date createDate = DateUtils.parseDate(dateStr, "yyyy-MM-dd HH:mm");
                    avgData.setCreateDate(createDate);
                    // 计算24小时滑动平均值并赋值给slidingAvg
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(createDate);
                    calendar.set(Calendar.MINUTE, 59);
                    calendar.set(Calendar.SECOND, 59);
                    calendar.set(Calendar.MILLISECOND, 0);
                    Date modifyDate = calendar.getTime();

                    avgData.setModifyDate(modifyDate);
                    avgData.setSd(sampleData.getSd());
                    avgData.setWd(sampleData.getWd());
                    avgData.setNo2Thickness(0.0);

                    // 计算平均值
                    avgData.setAqi(calculateAverage(deviceDataList, AirDataHour::getAqi));
                    avgData.setPm25(calculateAverage(deviceDataList, AirDataHour::getPm25));
                    avgData.setPm10(calculateAverage(deviceDataList, AirDataHour::getPm10));
                    avgData.setCo3Thickness(calculateAverage(deviceDataList, AirDataHour::getCo3Thickness));
                    avgData.setSo2Thickness(calculateAverage(deviceDataList, AirDataHour::getSo2Thickness));

                    // 得到空气质量等级、主要污染物等数据
                    avgData.setLevel(getAqiLevel(avgData.getAqi()));
                    avgData.setQuality(getAqiQuality(avgData.getAqi()));
                    avgData.setColor(getAqiColor(avgData.getAqi()));
                    avgData.setPrimaryPollutant(getPrimaryPollutant(deviceDataList));

                    // 计算24小时滑动平均值
                    Map<String, Double> slidingAvg = calculate24hSlidingAvgForDevice(deviceId, createDate);
                    avgData.setPm2524h(slidingAvg.get("pm25_24h"));
                    avgData.setPm1024h(slidingAvg.get("pm10_24h"));

                    result.add(avgData);
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        // 在返回结果前进行校准和保存
        List<Map<String, Object>> dataToSave = convertToSaveFormat(result);
        try {
            saveToMysql(dataToSave); // 校准并保存到数据库
        } catch (ParseException e) {
            log.error("数据保存失败", e);
        }

        // 返回校准后的数据（从数据库重新查询确保一致性）
        return reloadCalibratedData(result, dateStr);
    }
    private List<Map<String, Object>> convertToSaveFormat(List<AirDataHour> data) {
        return data.stream().map(item -> {
            Map<String, Object> map = new HashMap<>();
            map.put("device_id", item.getDeviceId());
            map.put("station_id", item.getStationId());
            map.put("dept_id", item.getDeptId());
            map.put("created_at", DateUtils.parseDateToStr("yyyy-MM-dd HH:mm", item.getCreateDate()));
            map.put("updated_at", DateUtils.parseDateToStr("yyyy-MM-dd HH:mm", item.getModifyDate()));
            map.put("average_pm2_5", item.getPm25());
            map.put("average_pm10", item.getPm10());
            map.put("average_aqi", item.getAqi());
            map.put("average_so2", item.getSo2Thickness());
            map.put("average_o3", item.getCo3Thickness());
            map.put("average_no2", item.getNo2Thickness());
            map.put("aqi_level", item.getLevel());
            map.put("aqi_quality", item.getQuality());
            map.put("aqi_color", item.getColor());
            map.put("primary_pollutant", item.getPrimaryPollutant());
            map.put("average_pm2_5_24", item.getPm2524h());
            map.put("average_pm10_24", item.getPm1024h());
            map.put("device_name", item.getDeviceName());
            map.put("station_name", item.getStationName());
            map.put("sd", item.getSd());
            map.put("wd", item.getWd());
            map.put("type", item.getType());

            return map;
        }).collect(Collectors.toList());
    }

    private List<AirDataHour> reloadCalibratedData(List<AirDataHour> originalData, String dateStr) {
        try {
            Date createDate = DateUtils.parseDate(dateStr, "yyyy-MM-dd HH:mm");
            return originalData.stream()
                    .map(item -> {
                        HourlyAverageAirData data = hourlyAverageAirDataRepository
                                .findByDeviceIdAndCreatedAt(item.getDeviceId(), createDate);
                        // 确保返回的对象是 AirDataHour 类型
                        if (data != null) {
                            AirDataHour airDataHour = new AirDataHour();
                            airDataHour.setDeviceId(data.getDeviceId());
                            airDataHour.setStationId(data.getStationId());
                            airDataHour.setDeptId(data.getDeptId());
                            airDataHour.setCreateDate(data.getCreatedAt());
                            airDataHour.setModifyDate(data.getUpdatedAt());
                            airDataHour.setPm25(data.getAveragePm25());
                            airDataHour.setPm10(data.getAveragePm10());
                            airDataHour.setAqi(Double.valueOf(data.getAverageAqi()));
                            airDataHour.setLevel(data.getAqiLevel());
                            airDataHour.setQuality(data.getAqiQuality());
                            airDataHour.setColor(data.getAqiColor());
                            airDataHour.setPrimaryPollutant(data.getPrimaryPollutant());
                            airDataHour.setPm2524h(data.getAveragePm25_24());
                            airDataHour.setPm1024h(data.getAveragePm10_24());
                            airDataHour.setDeviceName(data.getDeviceName());
                            airDataHour.setStationName(data.getStationName());
                            airDataHour.setSd(data.getSd());
                            airDataHour.setWd(data.getWd());
                            airDataHour.setType(data.getType());
                            return airDataHour;
                        }
                        return item; // 如果未找到对应数据，返回原始对象
                    })
                    .collect(Collectors.toList());
        } catch (ParseException e) {
            log.error("重新加载数据失败", e);
            return originalData;
        }
    }

    /**
     * 通用平均值计算方法
     */
    private Double calculateAverage(List<AirDataHour> reports, Function<AirDataHour, Double> getter) {
        if (reports == null || reports.isEmpty()) return 0.0;

        double avg = reports.stream()
                .filter(report -> Objects.nonNull(getter.apply(report)))
                .mapToDouble(getter::apply)
                .average()
                .orElse(0.0);

        DecimalFormat df = new DecimalFormat("#.0");
        return Double.parseDouble(df.format(avg));
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

    // 新增24小时滑动平均计算方法
    private Map<String, Double> calculate24hSlidingAvgForDevice(String deviceId, Date endTime) {
        Map<String, Double> result = new HashMap<>();
        result.put("pm25_24h", 0.0);
        result.put("pm10_24h", 0.0);

        try {
            // 1. 获取最近24条有效小时数据（不要求连续）
            List<Map<String, Object>> hourlyData = hourlyAverageAirDataMapper
                    .getLatest24ValidRecords(deviceId, endTime);

            // 2. 检查数据是否足够
            if (hourlyData == null || hourlyData.size() < 24) {
                log.warn("设备{}在{}前不足24条有效数据，实际{}条",
                        deviceId, endTime, hourlyData != null ? hourlyData.size() : 0);
                return result;
            }

            // 3. 计算PM2.5和PM10的24小时平均值
            double pm25Sum = 0.0;
            double pm10Sum = 0.0;
            int validPm25Count = 0;
            int validPm10Count = 0;

            for (Map<String, Object> data : hourlyData) {
                // PM2.5计算
                if (data.get("averagePm25") != null) {
                    pm25Sum += ((Number) data.get("averagePm25")).doubleValue();
                    validPm25Count++;
                }
                // PM10计算
                if (data.get("averagePm10") != null) {
                    pm10Sum += ((Number) data.get("averagePm10")).doubleValue();
                    validPm10Count++;
                }
            }

            // 4. 计算平均值（至少需要18个有效值）
            if (validPm25Count >= 18) {
                result.put("pm25_24h", roundDouble(pm25Sum / validPm25Count));
            }
            if (validPm10Count >= 18) {
                result.put("pm10_24h", roundDouble(pm10Sum / validPm10Count));
            }

            log.debug("设备{}在{}的24小时滑动平均计算完成: PM2.5={}, PM10={} (基于{}条数据)",
                    deviceId, endTime, result.get("pm25_24h"), result.get("pm10_24h"), hourlyData.size());

        } catch (Exception e) {
            log.error("计算设备{}的24小时滑动平均值时出错", deviceId, e);
        }

        return result;
    }


    // 保留原有的四舍五入方法
    private double roundDouble(double value) {
        return BigDecimal.valueOf(value)
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();
    }


    public void saveToMysql(List<Map<String, Object>> data) throws ParseException {
        SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        dateTimeFormat.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai")); // 明确指定时区

        for (Map<String, Object> row : data) {
            String deviceId = (String) row.get("device_id");
            if (deviceId == null || deviceId.isEmpty()) {
                log.error("Device ID is null or empty: {}", row);
                continue;
            }

            // 确认 stationId 是否存在
            String stationId = (String) row.get("station_id");
            if (stationId == null || stationId.isEmpty()) {
                log.warn("stationId is missing or null for deviceId: {}", deviceId);
                continue; // 如果 stationId 缺失或为空，则跳过当前记录
            }

            // 确认 deptId 是否存在
            String deptId = (String) row.get("dept_id");
            if (deptId == null || deptId.isEmpty()) {
                log.warn("deptId is missing or null for deptId: {}", deptId);
                continue; // 如果 deptId 缺失或为空，则跳过当前记录
            }
            //计算createAt 时间，应该与data中的dateTimeStr一样
            Date createAt = dateTimeFormat.parse((String) row.get("created_at"));

            // 检查数据库中是否已存在相同记录
            boolean exists = hourlyAverageAirDataRepository.existsByDeviceIdAndCreatedAtCustom(deviceId, createAt);
            if (exists) {
                log.info("Record with deviceId {} and createdAt {} already exists. Skipping insertion.", deviceId, createAt);
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
            hourlyAverageAirData.setDeptId(deptId); // 添加 deptId 字段
            hourlyAverageAirData.setWd((String) row.get("wd"));
            hourlyAverageAirData.setSd((String) row.get("sd"));
            hourlyAverageAirData.setAverageAqi(((Number) row.get("average_aqi")).longValue());
            hourlyAverageAirData.setAverageSo2(((Number) row.get("average_so2")).longValue());
            hourlyAverageAirData.setAverageNo2(((Number) row.get("average_no2")).longValue());
            hourlyAverageAirData.setAverageO3(((Number) row.get("average_o3")).longValue());
            hourlyAverageAirData.setAveragePm25((double) ((Number) row.get("average_pm2_5")).longValue());
            hourlyAverageAirData.setAveragePm10((double) ((Number) row.get("average_pm10")).longValue());
            hourlyAverageAirData.setAveragePm25_24((double) ((Number) row.get("average_pm2_5_24")).longValue());
            hourlyAverageAirData.setAveragePm10_24((double) ((Number) row.get("average_pm10_24")).longValue());
            hourlyAverageAirData.setAqiLevel((String) row.get("aqi_level"));
            hourlyAverageAirData.setAqiQuality((String) row.get("aqi_quality"));
            hourlyAverageAirData.setAqiColor((String) row.get("aqi_color"));
            if (row.get("primaryPollutant") == null) {
                hourlyAverageAirData.setPrimaryPollutant("-");
            }else{
                hourlyAverageAirData.setPrimaryPollutant((String) row.get("primaryPollutant"));
            }
            hourlyAverageAirData.setDeviceName((String) row.get("device_name"));
            hourlyAverageAirData.setStationName((String) row.get("station_name"));

            // 检查设备是否存在
            Device existingDevice = deviceMapper.selectDeviceById(Long.valueOf(deviceId));
            if (existingDevice == null) {
                log.error("Device with ID {} does not exist", deviceId);
                continue; // 跳过当前记录
            }


            Long isYunwei = deviceMapper.getIsYunwei(deviceId);

            if (isYunwei==1L || isYunwei==null) {

                // 数据校准逻辑
//                String deptId = (String) row.get("deptId"); // 获取部门ID
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

        //pm_25 比pmCount 大于80时设置type为1
        if(pm2_5>pmCountry+80){
            data.setType(1);
        } else if (pm2_5<pmCountry-60) {
            data.setType(0);
        }


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


        return data;
    }
    private double getRandomFluctuation(double range) {
        Random random = new Random();
        return random.nextDouble() * range * 2 - range; // Generates a value between -range and +range
    }

    private Double round(Double value) {
        return Double.valueOf(Math.round(value));
    }

}