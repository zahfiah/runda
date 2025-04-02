package ruoyi.yuxian.service;

import com.ruoyi.runda.domain.Device;
import com.ruoyi.runda.domain.DataQuery212;


import java.util.List;

public interface RegionDataPushService {

    /**
     * 推送大气数据到目标系统
     */
    Boolean pushAirQualityData(String regionCode, List<DataQuery212> dataList);

    /**
     * 获取支持的地区代码
     */
    String getSupportedRegionCode();

    Boolean pushDeviceData(String regionCode, List<Device> devices);
}