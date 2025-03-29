package ruoyi.yuxian.service.impl;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.ruoyi.common.utils.StringUtils;
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

@Slf4j
@Service
@RequiredArgsConstructor
public class YuxianDataPushServiceImpl implements RegionDataPushService {
    private static final String REGION_CODE = "130726";

    private final RegionPushHttpClient httpClient;
    private final YuxianPushProperties properties;

    @Override
    public void syncDevices(String regionCode, List<Device> devices) {
        if (!REGION_CODE.equals(regionCode)) {
            return;
        }

        YuxianPushProperties.RegionConfig config = properties.getRegions().get(regionCode);
        if (config == null) {
            log.error("蔚县推送配置未找到");
            return;
        }

        for (Device device : devices) {
            Map<String, Object> request = new HashMap<>();
            request.put("deviceId", device.getId());
            request.put("deviceName", device.getName());
            request.put("projectId", config.getProjectId());
            request.put("county_cn", device.getCountyCn());
            request.put("county", device.getCounty());
            request.put("status", device.getStatus());
            request.put("manufacturerName", device.getManufacturer());
            request.put("created_time", device.getCreatedTime());
            request.put("Latitude", device.getLatitude());
            request.put("Longitude", device.getLongitude());
            try {
                httpClient.post(regionCode, config.getDeviceCreateUrl(), request, Map.class);
                log.info("设备同步成功: {}", device.getId());
            } catch (Exception e) {
                log.error("设备同步失败: {}", device.getId(), e);
            }
        }
    }

    @Override
    public void pushAirQualityData(String regionCode, List<DataQuery212> dataList) {
        if (StringUtils.isEmpty(regionCode)) {
            log.error("区域编码不能为空");
            return;
        }

        if (CollectionUtils.isEmpty(dataList)) {
            log.warn("数据列表为空，无需推送");
            return;
        }

        YuxianPushProperties.RegionConfig config = properties.getRegions().get(regionCode);
        if (config == null) {
            log.error("地区[{}]配置未找到", regionCode);
            return;
        }

        int successCount = 0;
        for (DataQuery212 data : dataList) {
            try {
                Map<String, Object> request = createRequestData(data);
                httpClient.request(regionCode, config.getDataPushUrl(), request, Map.class);
                successCount++;
                log.debug("推送成功 - 设备ID: {}", data.getDeviceId());
            } catch (Exception e) {
                log.error("推送失败 - 设备ID: {}, 错误: {}", data.getDeviceId(), e.getMessage());
            }
        }

        log.info("推送完成. 成功: {}, 总数: {}", successCount, dataList.size());
    }

    private Map<String, Object> createRequestData(DataQuery212 data) {
        Map<String, Object> request = new HashMap<>();
        request.put("deviceId", data.getDeviceId());
        request.put("sn", data.getSn());
        request.put("pm25", data.getPm2_5());
        request.put("pm10", data.getPm10());
        request.put("WindSpeed", data.getWindSpeed());
        request.put("WindDirection", data.getWindDirection());
        request.put("temperature", data.getTemperature());
        request.put("humidity", data.getHumidity());
        request.put("pressure", data.getPressure());
        request.put("noise", data.getNoise());
        request.put("aqi", data.getAqi());
        return request;
    }

    @Override
    public String getSupportedRegionCode() {
        return REGION_CODE;
    }
}