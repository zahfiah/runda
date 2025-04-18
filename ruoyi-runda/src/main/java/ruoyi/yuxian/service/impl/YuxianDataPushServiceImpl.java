package ruoyi.yuxian.service.impl;

import org.springframework.http.*;
import ruoyi.yuxian.config.YuxianPushProperties;
import com.ruoyi.runda.domain.Device;
import com.ruoyi.runda.domain.DataQuery212;
import ruoyi.yuxian.service.RegionDataPushService;
import ruoyi.yuxian.uils.RegionPushHttpClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class YuxianDataPushServiceImpl implements RegionDataPushService {
    private static final String REGION_CODE = "130726";

    private final RegionPushHttpClient httpClient;
    private final YuxianPushProperties properties;


    @Override
    public Boolean pushAirQualityData(String regionCode, List<DataQuery212> dataList) {
        List<DataQuery212> filteredData = dataList.stream()
                .filter(data -> data.getDeviceId() != null) // 确保不为null
                .filter(data -> "1470".equals(data.getDeviceId()) || "1620".equals(data.getDeviceId()))
                .collect(Collectors.toList());
        log.info("查询到 {} 条过滤后数据", filteredData.size());
        if (!REGION_CODE.equals(regionCode)) {
            log.error("区域代码不匹配");
            return false;
        }

        YuxianPushProperties.RegionConfig config = properties.getRegions().get(regionCode);
        if (config == null) {
            log.error("未找到区域配置");
            return false;
        }

        String pushUrl = "http://60.8.195.86:9001/admin-api/pm/device-air-quality-record/create";
        int successCount = 0;
        int failureCount = 0;
        log.info("开始推送 {} 条数据", dataList.size());
        for (DataQuery212 data : filteredData) {
            //打印data数据信息
            log.info("设备[{}]", data.getSn());
            Map<String, Object> requestData = createRequestData(data);
            try {
                Map<String, Object> response = httpClient.post(
                        regionCode,
                        pushUrl,
                        requestData,
                        Map.class);

                if (isSuccessResponse(response)) {
                    successCount++;
//                    log.info("✅ 设备[{}]数据推送成功 - 记录ID: {}",
//                            data.getSn(),
//                            response.get("data"));
                } else {
                    failureCount++;
//                    log.warn("❌ 设备[{}]数据推送失败 - 状态码: {}, 消息: {}",
//                            data.getSn(),
//                            response != null ? response.get("code") : "null",
//                            response != null ? response.get("msg") : "无响应");
                }
            } catch (Exception e) {
                failureCount++;
                log.error("⚠️ 设备[{}]数据推送异常: {}", data.getSn(), e.getMessage());
            }
        }
//        打印每条数据的sn号
        for (DataQuery212 data : dataList) {
            log.info("设备[{}]", data.getSn());
        }
        log.info("推送结果统计: 成功 {} 条, 失败 {} 条, 总计 {} 条",
                successCount, failureCount, dataList.size());

        // 只要成功推送至少一条就返回true
        return successCount > 0;
    }

    private boolean isSuccessResponse(Map<String, Object> response) {
        if (response == null) {
            return false;
        }

        // 获取响应码并转为字符串
        Object codeObj = response.get("code");
        String code = codeObj != null ? String.valueOf(codeObj) : "";

        // 定义所有可能的成功码
        Set<String> successCodes = Set.of("200", "0", "success");

        // 检查是否有data字段（可选）
        boolean hasData = response.containsKey("data");

        return successCodes.contains(code) && hasData;
    }
    private Map<String, Object> createRequestData(DataQuery212 data) {
        Map<String, Object> request = new HashMap<>();
        if(data.getStationId()!=1126){
            request.put("projectId", "df1fccd32e314b2ca8b02ba261832b36");
        }else {
            request.put("projectId", "ea9b34c9a883416c84734d0a1ee7ac22");
        }
        request.put("deviceCode", data.getSn());
        request.put("pm25", data.getPm2_5());
        request.put("pm10", data.getPm10());
        request.put("WindSpeed", data.getWindSpeed());
        request.put("WindDirection", data.getWindDirection());
        request.put("temperature", data.getTemperature());
        request.put("humidity", data.getHumidity());
        request.put("airPressure", data.getPressure());
        request.put("aqi", data.getAqi());
        return request;
    }

    @Override
    public String getSupportedRegionCode() {
        return REGION_CODE;
    }

    @Override
    public Boolean pushDeviceData(String regionCode, List<Device> devices) {
        if (!REGION_CODE.equals(regionCode)) {
            log.error("区域代码不匹配");
            return false;
        }

        YuxianPushProperties.RegionConfig config = properties.getRegions().get(regionCode);
        if (config == null) {
            log.error("未找到区域配置");
            return false;
        }

        String createUrl = "http://60.8.195.86:9001/admin-api/pm/device/create";
        String updateUrl = "http://60.8.195.86:9001/admin-api/pm/device/update";
        int successCount = 0;

        try {
            for (Device device : devices) {
                log.info("设备[{}]", device.getSn());
                Map<String, Object> requestData = createDeviceRequestData(device);

                try {
                    // 先尝试创建
                    Map<String, Object> response = httpClient.post(
                            regionCode,
                            createUrl,
                            requestData,
                            Map.class);

                    // 如果创建失败且原因是设备已存在，则尝试更新
                    if (response != null && "100500".equals(String.valueOf(response.get("code")))) {
                        String msg = (String) response.get("msg");
                        if (msg != null && msg.contains("设备唯一编码填写重复")) {
                            // 使用PUT方法更新设备
                            response = httpClient.put(
                                    regionCode,
                                    updateUrl,
                                    requestData,
                                    Map.class);
                        }
                    }

                    if (response != null && "0".equals(String.valueOf(response.get("code")))) {
                        successCount++;
                        log.info("设备{}操作成功，响应: {}", device.getSn(), response);
                    } else {
                        log.error("设备{}操作失败，响应: {}", device.getSn(), response);
                    }
                } catch (Exception e) {
                    log.error("设备{}操作异常: {}", device.getSn(), e.getMessage());
                }
            }
            return successCount > 0;
        } catch (Exception e) {
            log.error("设备数据操作全局异常", e);
            return false;
        }
    }

    private Map<String, Object> createDeviceRequestData(Device device) {
        Map<String, Object> request = new HashMap<>();
        request.put("deviceCode", device.getSn());
        request.put("deviceName", device.getName());
        if(device.getId()!=1470){
            request.put("projectId", "df1fccd32e314b2ca8b02ba261832b36");
        }else {
            request.put("projectId", "ea9b34c9a883416c84734d0a1ee7ac22");
        }
        request.put("id",device.getId());
        request.put("deviceType", "1");
        request.put("status", device.getStatus());
        request.put("manufacturerName", device.getManufacturer());
        request.put("created_time", device.getCreatedTime());
        request.put("Latitude", device.getLatitude());
        request.put("Longitude", device.getLongitude());
        return request;
    }
}