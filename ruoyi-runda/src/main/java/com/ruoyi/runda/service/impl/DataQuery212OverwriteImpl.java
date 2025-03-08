package com.ruoyi.runda.service.impl;

import com.github.pagehelper.util.StringUtil;
import com.ruoyi.runda.domain.AirDataResult;
import com.ruoyi.runda.domain.DataQuery212;
import com.ruoyi.runda.repository.DataQuery212OVRepository;
import com.ruoyi.runda.repository.DataQuery212Repository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import java.util.*;
import java.util.stream.Collectors;

@Repository
@Slf4j
public class DataQuery212OverwriteImpl implements DataQuery212OVRepository {

    @Autowired
    private DataQuery212Repository dataQuery212Repository;

    @Override
    public Page<DataQuery212> findByDeviceId(String deviceId, Pageable pageable) {
        try {
            Page<AirDataResult> airDataResultPage = dataQuery212Repository.findByDeviceId(deviceId, pageable);
            log.info("Fetched {} records for deviceId: {}", airDataResultPage.getTotalElements(), deviceId);

            return convertToDataQuery212Page(airDataResultPage, pageable);
        } catch (Exception e) {
            log.error("Error fetching data for deviceId: {}", deviceId, e);
            throw new RuntimeException("Failed to fetch data for deviceId: " + deviceId, e);
        }
    }

    @Override
    public Page<DataQuery212> findByCreateDateBetween(long startTimestamp, long endTimestamp, Pageable pageable) {
        try {
            Page<AirDataResult> airDataResultPage = dataQuery212Repository.findByCreateDateBetween(startTimestamp, endTimestamp, pageable);
            log.info("Fetched {} records between {} and {}", airDataResultPage.getTotalElements(), startTimestamp, endTimestamp);
            return convertToDataQuery212Page(airDataResultPage, pageable);
        } catch (Exception e) {
            log.error("Error fetching data between {} and {}", startTimestamp, endTimestamp, e);
            throw new RuntimeException("Failed to fetch data for the given time range", e);
        }
    }

    @Override
    public Page<DataQuery212> findByDeviceIdAndCreateDateBetween(String deviceId, long startTimestamp, long endTimestamp, Pageable pageable) {
        try {
            Page<AirDataResult> airDataResultPage = dataQuery212Repository.findByDeviceIdAndCreateDateBetween(deviceId, startTimestamp, endTimestamp, pageable);
            log.info("Fetched {} records for deviceId {} between {} and {}", airDataResultPage.getTotalElements(), deviceId, startTimestamp, endTimestamp);
            return convertToDataQuery212Page(airDataResultPage, pageable);
        } catch (Exception e) {
            log.error("Error fetching data for deviceId {} between {} and {}", deviceId, startTimestamp, endTimestamp, e);
            throw new RuntimeException("Failed to fetch data for deviceId and time range", e);
        }
    }

    // 将 AirDataResult 分页数据转换为 DataQuery212 分页数据
    private Page<DataQuery212> convertToDataQuery212Page(Page<AirDataResult> airDataResultPage, Pageable pageable) {
        List<DataQuery212> dataQuery212List = airDataResultPage.getContent().stream()
                .map(this::convertAirDataResultToDataQuery212)
                .collect(Collectors.toList());
        getAqiDeviceDataExtends(dataQuery212List);
        return new PageImpl<>(dataQuery212List, pageable, airDataResultPage.getTotalElements());
    }

    // 将单个 AirDataResult 转换为 DataQuery212
    private DataQuery212 convertAirDataResultToDataQuery212(AirDataResult airDataResult) {
        DataQuery212 dataQuery212 = new DataQuery212();
        dataQuery212.setId(airDataResult.getId());
        dataQuery212.setSn(airDataResult.getMn());
        dataQuery212.setDate(airDataResult.getDataTime());
        dataQuery212.setDeptId(airDataResult.getDeptId());
        dataQuery212.setStationId(airDataResult.getStationId());
        dataQuery212.setDeviceId(airDataResult.getDeviceId());
        dataQuery212.setDeviceName(airDataResult.getDeviceName());
        dataQuery212.setStationName(airDataResult.getStationName());
        dataQuery212.setLatitude(airDataResult.getLatitude());
        dataQuery212.setLongitude(airDataResult.getLongitude());

        // 处理 cp 字段
        Map<String, String> cpMap = parseCp(airDataResult.getCp());
        DataQuery212 processedResult = processCp(cpMap, dataQuery212);

        log.debug("Converted AirDataResult to DataQuery212: {}", processedResult);
        return processedResult;
    }

    // 解析 cp 字段
    private Map<String, String> parseCp(Object cp) {
        if (cp instanceof Map) {
            return (Map<String, String>) cp;
        } else if (cp instanceof String) {
            Map<String, String> cpMap = parseCpString((String) cp);
            log.debug("Parsed cp string: {}", cpMap);
            return cpMap;
        } else {
            log.warn("Unsupported cp type: {}", cp.getClass());
            return new HashMap<>();
        }
    }


    // 解析 cp 字符串
    private Map<String, String> parseCpString(String cpString) {
        Map<String, String> cpMap = new HashMap<>();
        if (cpString == null || cpString.isEmpty()) {
            return cpMap;
        }
        cpString = cpString.substring(1, cpString.length() - 1).trim();
        String[] pairs = cpString.split(", ");
        for (String pair : pairs) {
            String[] keyValue = pair.split("=");
            if (keyValue.length == 2) {
                cpMap.put(keyValue[0].trim(), keyValue[1].trim());
            }
        }
        log.debug("Parsed cp map: {}", cpMap);
        return cpMap;
    }

    private DataQuery212 processCp(Map<String, String> cp, DataQuery212 result) {
        String rtd = "-Rtd";
        String tem = AirFactorCodingEnum.A01001.getCode() + rtd;
        String hum = AirFactorCodingEnum.A01002.getCode() + rtd;
        String pre = AirFactorCodingEnum.A01006.getCode() + rtd;
        String windSpeed = AirFactorCodingEnum.A01007.getCode() + rtd;
        String windDirection = AirFactorCodingEnum.A01008.getCode() + rtd;

        String pm10 = AirFactorCodingEnum.A34002.getCode() + rtd;
        String pm25 = AirFactorCodingEnum.A34004.getCode() + rtd;
        String tsp = AirFactorCodingEnum.A34001.getCode() + rtd;
        String no = AirFactorCodingEnum.A21003.getCode() + rtd;
        String no2 = AirFactorCodingEnum.A21004.getCode() + rtd;
        String so2 = AirFactorCodingEnum.A21026.getCode() + rtd;
        String co = AirFactorCodingEnum.A21005.getCode() + rtd;
        String c6h6 = AirFactorCodingEnum.A25002.getCode() + rtd;
        String noiseLa = NoiseFactorCodingEnum.LA.getCode() + rtd;
        String o3 = AirFactorCodingEnum.O3.getCode() + rtd;
        String o3Szzt = AirFactorCodingEnum.A05024.getCode() + rtd;

        String dataTimeTag = "DataTime";
        String dateTimeSet = cp.get(dataTimeTag);
        if (StringUtil.isEmpty(dateTimeSet)) {
            result.setCreateDate(new Date());
        } else {
            try {
                long dataTime = Long.parseLong(dateTimeSet);
                result.setCreateDate(new Date(dataTime));
            } catch (NumberFormatException e) {
                log.error("Failed to parse dataTime: {}", dateTimeSet, e);
                result.setCreateDate(new Date());
            }
        }

        result.setTemperature(parseDouble(cp.get(tem)));
        result.setHumidity(parseDouble(cp.get(hum)));
        result.setPressure(parseDouble(cp.get(pre)));
        result.setWindSpeed(parseDouble(cp.get(windSpeed)));
        result.setWindDirection(parseDouble(cp.get(windDirection)));
        result.setPm10(parseDouble(cp.get(pm10)));
        result.setPm2_5(Double.valueOf(cp.get(pm25)));
        result.setTsp(parseDouble(cp.get(tsp)));
        result.setNoThickness(parseDouble(cp.get(no)));
        result.setNo2Thickness(parseDouble(cp.get(no2)));
        result.setSo2Thickness(parseDouble(cp.get(so2)));
        result.setCoThickness(parseDouble(cp.get(co)));
        result.setCo3Thickness(parseDouble(cp.get(o3)));

        if (cp.get(o3) == null) {
            result.setCo3Thickness(parseDouble(cp.get(o3Szzt)));
        }

        result.setNoise(parseDouble(cp.get(noiseLa)));

        log.debug("Processed cp to DataQuery212: {}", result);
        return result;
    }



    /**
     * 安全地将字符串解析为 Double
     */
    private Double parseDouble(String value) {
        if (value == null || value.isEmpty()) {
            return 0.0;
        }
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            log.error("Failed to parse double value: {}", value, e);
            return 0.0;
        }
    }
    enum AirFactorCodingEnum {
        A00000("a00000","废气"),
        A01001("a01001","温度"),
        A01002("a01002","湿度"),
        A01006("a01006","气压"),
        A01007("a01007","风速"),
        A01008("a01008","风向"),
        A01010("a01010","林戈曼黑度"),
        A01011("a01011","烟气流速"),
        A01012("a01012","烟气温度"),
        A01013("a01013","烟气压力"),
        A01014("a01014","烟气湿度"),
        A01015("a01015","制冷温度"),
        A01016("a01016","烟道截面积"),
        A01017("a01017","烟气动压"),
        A01901("a01901","垃圾焚烧炉膛内焚烧平均温度"),
        A01902("a01902","垃圾焚烧炉膛内DCS温度"),
        A05001("a05001","二氧化碳"),
        A05002("a05002","甲烷"),
        A05008("a05008","三氯一氟甲烷"),
        A05009("a05009","二氯二氟甲烷"),
        A05013("a05013","三氯三氟乙烷"),
        A05024("a05024","臭氧"),
        A19001("a01201","氧气含量"),
        A20007("a20007","砷"),
        A20016("a20016","铍及其化合物"),
        A20025("a20025","镉及其化合物"),
        A20026("a20026","镉"),
        A20043("a20043","铅及其化合物"),
        A20044("a20044","铅"),
        A20057("a20057","汞及其化合物"),
        A20058("a20058","汞"),
        A20063("a20063","镍及其化合物"),
        A20091("a20091","锡及其化合物"),
        A21001("a21001","氨(氨气)"),
        A21002("a21002","氮氧化物"),
        A21003("a21003","一氧化氮"),
        A21004("a21004","二氧化氮"),
        A21005("a21005","一氧化碳"),
        A21017("a21017","氰化物"),
        A21018("a21018","氟化物"),
        A21022("a21022","氯气"),
        A21024("a21024","氯化氢"),
        A21026("a21026","二氧化硫"),
        A21028("a21028","硫化氢"),
        A23001("a23001","酚类"),
        A24003("a24003","二氯甲烷"),
        A24004("a24004","三氯甲烷"),
        A24005("a24005","四氯甲烷"),
        A24006("a24006","二溴一氯甲烷"),
        A24007("a24007","一溴二氯甲烷"),
        A24008("a24008","溴甲烷"),
        A24009("a24009","三溴甲烷"),
        A24015("a24015","氯乙烷"),
        A24016("a24016","1,1-二氯乙烷"),
        A24017("a24017","1,2-二氯乙烷"),
        A24018("a24018","1,1,1-三氯乙烷"),
        A24019("a24019","1,1,2-三氯乙烷"),
        A24020("a24020","1,1,2,2-四氯乙烷"),
        A24027("a24027","1,2-二氯丙烷"),
        A24034("a24034","1,2-二溴乙烷"),
        A24036("a24036","环己烷"),
        A24042("a24042","正己烷"),
        A24043("a24043","正庚烷"),
        A24046("a24046","氯乙烯"),
        A24047("a24047","1,1-二氯乙烯"),
        A24049("a24049","三氯乙烯"),
        A24050("a24050","四氯乙烯"),
        A24053("a24053","丙烯"),
        A24054("a24054","1,3二氯乙烯"),
        A24072("a24072","1,4-二恶烷"),
        A24078("a24078","1,3-丁二烯"),
        A24087("a24087","碳氢化合物"),
        A24088("a24088","非甲烷总烃"),
        A24099("a24099","氯甲烷"),
        A24110("a24110","反式-1,2-二氯乙烯"),
        A24111("a24111","顺式-1,2-二氯乙烯"),
        A24112("a24112","反式-1,3-二氯丙烯"),
        A24113("a24113","六氯-1,3-丁二烯"),
        A25002("a25002","苯"),
        A25003("a25003","甲苯"),
        A25004("a25004","乙苯"),
        A25005("a25005","二甲苯"),
        A25006("a25006","1,2-二甲基苯"),
        A25007("a25007","1,3-二甲基苯"),
        A25008("a25008","1,4-二甲基苯"),
        A25010("a25010","氯苯"),
        A25011("a25011","1,2-二氯苯"),
        A25012("a25012","1,3-二氯苯"),
        A25013("a25013","1,4-二氯苯"),
        A25014("a25014","1-乙基-4-甲基苯"),
        A25015("a25015","1,2,4-三氯苯"),
        A25019("a25019","1,2,4-三甲基苯"),
        A25020("a25020","1,2,3-三甲基苯"),
        A25021("a25021","1,2,5-三甲基苯"),
        A25023("a25023","硝基苯"),
        A25038("a25038","乙烯基苯"),
        A25044("a25044","苯并[a]芘"),
        A25072("a25072","四氢呋喃"),
        A26001("a26001","苯胺类"),
        A29017("a29017","乙酸乙酯"),
        A29026("a29026","乙酸乙烯酯"),
        A30001("a30001","甲醇"),
        A30008("a30008","异丙醇"),
        A30022("a30022","硫醇"),
        A31001("a31001","甲醛"),
        A31002("a31002","乙醛"),
        A31024("a31024","丙酮"),
        A31025("a31025","2-丁酮"),
        A31030("a31030","甲基异丁基甲酮"),
        A34001("a34001","总悬浮颗粒物TSP"),
        A34002("a34002","可吸入颗粒物PM10"),
        A34004("a34004","细微颗粒物PM2.5"),
        A34005("a34005","亚微米颗粒物PM10"),
        A34011("a34011","降尘"),
        A34013("a34013","烟尘"),
        A34017("a34017","炭黑尘"),
        A34038("a34039","沥青烟"),
        A34039("a34039","硫酸雾"),
        A34040("a34040","铬酸雾"),
        A99010("a99010","丙烯腈"),
        A99049("a99049","光气"),
        A99051("a99051","二硫化碳"),
        O3("O3","臭氧");
        private String code;
        private String description;

        AirFactorCodingEnum(String code,String description){
            this.code = code;
            this.description = description;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        /**
         * 通过编码获取中文描述
         * @param code
         * @return
         */
        public static String getValue(String code){
            for(com.ruoyi.runda.utils.AirFactorCodingEnum attributeType: com.ruoyi.runda.utils.AirFactorCodingEnum.values()){
                if(attributeType.getCode().equals(code)){
                    return attributeType.getDescription();
                }
            }
            return null;
        }
    }

   enum NoiseFactorCodingEnum {
        LA("LA","A权声级"),
        L5("L5","累积百分声级L5"),
        L10("L10","累积百分声级L10"),
        L50("L50","累积百分声级L50"),
        L90("L90","累积百分声级L90"),
        L95("L95","累积百分声级L95"),
        LEQ("Leq","等效声级"),
        LDN("Ldn","昼夜等效声级"),
        LD("Ld","烟气温度"),
        LN("Ln","烟气压力"),
        LMX("LMx","烟气湿度"),
        LMN("LMn","制冷温度");
        private String code;
        private String description;

        NoiseFactorCodingEnum(String code,String description){
            this.code = code;
            this.description = description;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        /**
         * 通过编码获取中文描述
         * @param code
         * @return
         */
        public static String getValue(String code){
            for(com.ruoyi.runda.utils.NoiseFactorCodingEnum attributeType: com.ruoyi.runda.utils.NoiseFactorCodingEnum.values()){
                if(attributeType.getCode().equals(code)){
                    return attributeType.getDescription();
                }
            }
            return null;
        }
    }
    public final static List<PollutantLimits> list=new ArrayList<PollutantLimits>();
    static{
        PollutantLimits p1=new PollutantLimits(0,0,0,0,0,0,0,0,0,0,0);
        PollutantLimits p2=new PollutantLimits(50,50,150,40,100,50,2,5,160,100,35);
        PollutantLimits p3=new PollutantLimits(100,150,500,80,200,150,4,10,200,160,75);
        PollutantLimits p4=new PollutantLimits(150,475,650,180,700,250,14,35,300,215,115);
        PollutantLimits p5=new PollutantLimits(200,800,800,280,1200,350,24,60,400,265,150);
        PollutantLimits p6=new PollutantLimits(300,1600,null,565,2340,420,36,90,800,800,250);
        PollutantLimits p7=new PollutantLimits(400,2100,null,750,3090,500,48,120,1000,null,350);
        PollutantLimits p8=new PollutantLimits(500,2620,null,940,3840,600,60,150,1200,null,500);
        list.add(p1);list.add(p2);list.add(p3);list.add(p4);
        list.add(p5);list.add(p6);list.add(p7);list.add(p8);
    }

    public static void getAqiDeviceDataExtends(List<DataQuery212> data) {
        if (data == null || data.isEmpty() || list == null || list.isEmpty()) {
            return;
        }
        for (DataQuery212 devicedate : data) {
            //臭氧8小时浓度
            Integer o38 =  0;//已算
            //臭氧1小时浓度
            Double o3 = devicedate.getCo3Thickness();//已算
            //一氧化碳浓度
            Double co = devicedate.getCoThickness();  //已算
            //pm10 浓度
            Double pm10 = devicedate.getPm10();      //已算
            //pm25浓度
            Double pm25 = devicedate.getPm2_5();      //已算
            //二氧化硫1小时浓度
            Double so2 = devicedate.getSo2Thickness();   //已算
            //二氧化硫24小时浓度
            Integer so2_24h = 0;//so2 24小时浓度
            //二氧化氮浓度
            Double no2 = devicedate.getNo2Thickness();    //已算

            Double so2iaqi = 0d, so2_24iaqi = 0d, no2iaqi = 0d, coiaqi = 0d, pm25iaqi = 0d, pm10iaqi = 0d, o3iaqi = 0d, o31iaqi = 0d;
            Double maxV = 0d;//最大的aqi值
            String primaryPollutant = "";//首要污染物
            String excessivePollutant = "";//超标污染污
            int size = list.size();
            for (int i = 1; i < size; i++) {
                PollutantLimits limitMax = list.get(size - 1);
                PollutantLimits limitMin = list.get(size - 2);
                PollutantLimits limits = list.get(i);
                PollutantLimits prelimits = list.get(i - 1);
                if (so2_24h != 0 && so2_24iaqi == 0) {
                    if (so2_24h > limitMax.getSo224()) {
                        limits = limitMax;
                        prelimits = limitMin;
                        so2_24iaqi = (limits.getIaqi() - prelimits.getIaqi()) / (limits.getSo224() * 1.00 - prelimits.getSo224() * 1.00) * (so2_24h - prelimits.getSo224()) + prelimits.getIaqi();
                    }
                    if (limits.getSo224() >= so2_24h) {
                        so2_24iaqi = (limits.getIaqi() - prelimits.getIaqi()) / (limits.getSo224() * 1.00 - prelimits.getSo224() * 1.00) * (so2_24h - prelimits.getSo224()) + prelimits.getIaqi();
                    }
                    if (so2 > 800) {
                        if (so2_24iaqi > maxV) {
                            primaryPollutant = "SO2,";
                            maxV = so2_24iaqi;
                        } else if (so2_24iaqi == maxV) {
                            primaryPollutant += "SO2,";
                        }
                        if (so2_24iaqi > 100) {
                            excessivePollutant += "SO2,";
                        }
                    }
                }
                if (so2 != 0 && so2 <= 800 && limits.getSo21() != null && so2iaqi == 0) {
                    so2iaqi = (limits.getIaqi() - prelimits.getIaqi()) / (limits.getSo21() * 1.00 - prelimits.getSo21() * 1.00) * (so2 - prelimits.getSo21()) + prelimits.getIaqi();
                    if (so2iaqi > maxV) {
                        primaryPollutant = "SO2,";
                        maxV = so2iaqi;
                    } else if (so2iaqi == maxV) {
                        primaryPollutant += "SO2,";
                    }
                    if (so2iaqi > 100) {
                        excessivePollutant += "SO2,";
                    }
                }
                if (no2 != 0 && limits.getNo21() != null && no2iaqi == 0) {
                    if (no2 > limitMax.getNo21()) {
                        limits = limitMax;
                        prelimits = limitMin;
                        no2iaqi = (limits.getIaqi() - prelimits.getIaqi()) / (limits.getNo21() * 1.00 - prelimits.getNo21() * 1.00) * (no2 - prelimits.getNo21()) + prelimits.getIaqi();
                    }
                    if (limits.getNo21() >= no2) {
                        no2iaqi = (limits.getIaqi() - prelimits.getIaqi()) / (limits.getNo21() * 1.00 - prelimits.getNo21() * 1.00) * (no2 - prelimits.getNo21()) + prelimits.getIaqi();
                    }

                    if (no2iaqi > maxV) {
                        primaryPollutant = "NO2,";
                        maxV = no2iaqi;
                    } else if (no2iaqi == maxV) {
                        primaryPollutant += "NO2,";
                    }
                    if (no2iaqi > 100) {
                        excessivePollutant += "NO2,";
                    }
                }
                if (co != 0 && limits.getCo1() != null && coiaqi == 0) {
                    if (co > limitMax.getCo1()) {
                        limits = limitMax;
                        prelimits = limitMin;
                        coiaqi = (limits.getIaqi() - prelimits.getIaqi()) / (limits.getCo1() * 1.00 - prelimits.getCo1() * 1.00) * (co - prelimits.getCo1()) + prelimits.getIaqi();
                    }
                    if (limits.getCo1() >= co) {
                        coiaqi = (limits.getIaqi() - prelimits.getIaqi()) / (limits.getCo1() * 1.00 - prelimits.getCo1() * 1.00) * (co - prelimits.getCo1()) + prelimits.getIaqi();
                    }

                    if (coiaqi > maxV) {
                        primaryPollutant = "CO,";
                        maxV = coiaqi;
                    } else if (coiaqi == maxV) {
                        primaryPollutant += "CO,";
                    }
                    if (coiaqi > 100) {
                        excessivePollutant += "CO,";
                    }
                }
                if (pm25 != 0 && limits.getPm25() != null && pm25iaqi == 0) {

                    if (pm25 > limitMax.getPm25()) {
                        limits = limitMax;
                        prelimits = limitMin;
                        pm25iaqi = (limits.getIaqi() - prelimits.getIaqi()) / (limits.getPm25() * 1.00 - prelimits.getPm25() * 1.00) * (pm25 - prelimits.getPm25()) + prelimits.getIaqi();
                    }
                    if (limits.getPm25() >= pm25) {
                        pm25iaqi = (limits.getIaqi() - prelimits.getIaqi()) / (limits.getPm25() * 1.00 - prelimits.getPm25() * 1.00) * (pm25 - prelimits.getPm25()) + prelimits.getIaqi();
                    }
                    if (pm25iaqi > maxV) {
                        primaryPollutant = "PM25,";
                        maxV = pm25iaqi;
                    } else if (pm25iaqi == maxV) {
                        primaryPollutant += "PM25,";
                    }
                    if (pm25iaqi > 100) {
                        excessivePollutant += "PM25,";
                    }
                }
                if (pm10 != 0 && limits.getPm10() != null && pm10iaqi == 0) {
                    if (pm10 > limitMax.getPm10()) {
                        limits = limitMax;
                        prelimits = limitMin;
                        pm10iaqi = (limits.getIaqi() - prelimits.getIaqi()) / (limits.getPm10() * 1.00 - prelimits.getPm10() * 1.00) * (pm10 - prelimits.getPm10()) + prelimits.getIaqi();
                    }
                    if (limits.getPm10() >= pm10) {
                        pm10iaqi = (limits.getIaqi() - prelimits.getIaqi()) / (limits.getPm10() * 1.00 - prelimits.getPm10() * 1.00) * (pm10 - prelimits.getPm10()) + prelimits.getIaqi();
                    }
                    if (pm10iaqi > maxV) {
                        primaryPollutant = "PM10,";
                        maxV = pm10iaqi;
                    } else if (pm10iaqi == maxV) {
                        primaryPollutant += "PM10,";
                    }
                    if (pm10iaqi > 100) {
                        excessivePollutant += "PM10,";
                    }
                }
                if (o3 != 0 && limits.getO31() != null && o31iaqi == 0) {
                    if (o3 > limitMax.getO31()) {
                        limits = limitMax;
                        prelimits = limitMin;
                        o31iaqi = (limits.getIaqi() - prelimits.getIaqi()) / (limits.getO31() * 1.00 - prelimits.getO31() * 1.00) * (o3 - prelimits.getO31()) + prelimits.getIaqi();
                    }
                    if (limits.getO31() >= o3) {
                        o31iaqi = (limits.getIaqi() - prelimits.getIaqi()) / (limits.getO31() * 1.00 - prelimits.getO31() * 1.00) * (o3 - prelimits.getO31()) + prelimits.getIaqi();
                    }

                    if (o31iaqi > maxV) {
                        primaryPollutant = "O3,";
                        maxV = o31iaqi;
                    } else if (o31iaqi == maxV) {
                        primaryPollutant += "O3,";
                    }
                    if (o31iaqi > 100) {
                        excessivePollutant += "O3,";
                    }

                }
                if (o38 != 0 && o38 <= 800 && limits.getO38() != null && limits.getO38() >= o38 && o3iaqi == 0) {
                    o3iaqi = (limits.getIaqi() - prelimits.getIaqi()) / (limits.getO38() * 1.00 - prelimits.getO38() * 1.00) * (o38 - prelimits.getO38()) + prelimits.getIaqi();
                }
            }
            // 在 getAqiDeviceDataExtends 方法中修改如下：
            devicedate.setAqi(Double.valueOf(maxV.intValue())); // 显式转换为 Double

        }
    }


}

class PollutantLimits implements java.io.Serializable {
    /** 版本号 */
    private static final long serialVersionUID = 7788062090935260781L;

    public PollutantLimits() {
        super();
        // TODO Auto-generated constructor stub
    }

    public PollutantLimits(Integer iaqi, Integer so224, Integer so21,
                           Integer no224, Integer no21, Integer pm10, Integer co24,
                           Integer co1, Integer o31, Integer o38, Integer pm25) {
        super();
        this.iaqi = iaqi;
        this.so224 = so224;
        this.so21 = so21;
        this.no224 = no224;
        this.no21 = no21;
        this.pm10 = pm10;
        this.co24 = co24;
        this.co1 = co1;
        this.o31 = o31;
        this.o38 = o38;
        this.pm25 = pm25;
    }


    /** 空气质量分指数 */
    private Integer iaqi;

    /** 二氧化硫24小时限值 */
    private Integer so224;

    /** 二氧化硫1小时限值 */
    private Integer so21;

    /** 二氧化氮24小时限值 */
    private Integer no224;

    /** 二氧化氮1小时限值 */
    private Integer no21;

    /** pm10限值 */
    private Integer pm10;

    /** 一氧化碳24小时限值 */
    private Integer co24;

    /** 一氧化碳1小时限值 */
    private Integer co1;

    /** 臭氧1小时限值 */
    private Integer o31;

    /** 臭氧8小时限值 */
    private Integer o38;

    /** pm2.5限值 */
    private Integer pm25;

    /**
     * 获取空气质量分指数
     *
     * @return 空气质量分指数
     */
    public Integer getIaqi() {
        return this.iaqi;
    }

    /**
     * 设置空气质量分指数
     *
     * @param iaqi
     *          空气质量分指数
     */
    public void setIaqi(Integer iaqi) {
        this.iaqi = iaqi;
    }

    /**
     * 获取二氧化硫24小时限值
     *
     * @return 二氧化硫24小时限值
     */
    public Integer getSo224() {
        return this.so224;
    }

    /**
     * 设置二氧化硫24小时限值
     *
     * @param so224
     *          二氧化硫24小时限值
     */
    public void setSo224(Integer so224) {
        this.so224 = so224;
    }

    /**
     * 获取二氧化硫1小时限值
     *
     * @return 二氧化硫1小时限值
     */
    public Integer getSo21() {
        return this.so21;
    }

    /**
     * 设置二氧化硫1小时限值
     *
     * @param so21
     *          二氧化硫1小时限值
     */
    public void setSo21(Integer so21) {
        this.so21 = so21;
    }

    /**
     * 获取二氧化氮24小时限值
     *
     * @return 二氧化氮24小时限值
     */
    public Integer getNo224() {
        return this.no224;
    }

    /**
     * 设置二氧化氮24小时限值
     *
     * @param no224
     *          二氧化氮24小时限值
     */
    public void setNo224(Integer no224) {
        this.no224 = no224;
    }

    /**
     * 获取二氧化氮1小时限值
     *
     * @return 二氧化氮1小时限值
     */
    public Integer getNo21() {
        return this.no21;
    }

    /**
     * 设置二氧化氮1小时限值
     *
     * @param no21
     *          二氧化氮1小时限值
     */
    public void setNo21(Integer no21) {
        this.no21 = no21;
    }

    /**
     * 获取pm10限值
     *
     * @return pm10限值
     */
    public Integer getPm10() {
        return this.pm10;
    }

    /**
     * 设置pm10限值
     *
     * @param pm10
     *          pm10限值
     */
    public void setPm10(Integer pm10) {
        this.pm10 = pm10;
    }

    /**
     * 获取一氧化碳24小时限值
     *
     * @return 一氧化碳24小时限值
     */
    public Integer getCo24() {
        return this.co24;
    }

    /**
     * 设置一氧化碳24小时限值
     *
     * @param co24
     *          一氧化碳24小时限值
     */
    public void setCo24(Integer co24) {
        this.co24 = co24;
    }

    /**
     * 获取一氧化碳1小时限值
     *
     * @return 一氧化碳1小时限值
     */
    public Integer getCo1() {
        return this.co1;
    }

    /**
     * 设置一氧化碳1小时限值
     *
     * @param co1
     *          一氧化碳1小时限值
     */
    public void setCo1(Integer co1) {
        this.co1 = co1;
    }

    /**
     * 获取臭氧1小时限值
     *
     * @return 臭氧1小时限值
     */
    public Integer getO31() {
        return this.o31;
    }

    /**
     * 设置臭氧1小时限值
     *
     * @param o31
     *          臭氧1小时限值
     */
    public void setO31(Integer o31) {
        this.o31 = o31;
    }

    /**
     * 获取臭氧8小时限值
     *
     * @return 臭氧8小时限值
     */
    public Integer getO38() {
        return this.o38;
    }

    /**
     * 设置臭氧8小时限值
     *
     * @param o38
     *          臭氧8小时限值
     */
    public void setO38(Integer o38) {
        this.o38 = o38;
    }

    /**
     * 获取pm2.5限值
     *
     * @return pm2
     */
    public Integer getPm25() {
        return this.pm25;
    }

    /**
     * 设置pm2.5限值
     *
     * @param pm25
     *          pm2.5限值
     */
    public void setPm25(Integer pm25) {
        this.pm25 = pm25;
    }
}