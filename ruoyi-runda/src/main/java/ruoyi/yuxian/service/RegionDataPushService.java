package ruoyi.yuxian.service;

import com.ruoyi.runda.domain.Device;
import com.ruoyi.runda.domain.DataQuery212;


import java.util.List;

public interface RegionDataPushService {
    /**
     * 同步设备到目标系统
     */
    void syncDevices(String regionCode, List<Device> devices);

    /**
     * 推送大气数据到目标系统
     */
    void pushAirQualityData(String regionCode, List<DataQuery212> dataList);

    /**
     * 获取支持的地区代码
     */
    String getSupportedRegionCode();
}