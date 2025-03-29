package ruoyi.yuxian.task;

import  com.ruoyi.runda.domain.Device;
import  com.ruoyi.runda.domain.DataQuery212;
import ruoyi.yuxian.factory.RegionDataPushFactory;
import com.ruoyi.runda.mapper.DeviceMapper;
import ruoyi.yuxian.service.RegionDataPushService;
import com.ruoyi.runda.service.impl.DataQuery212ServiceImpl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataPushTask {
    private final DeviceMapper deviceMapper;
    private final  DataQuery212ServiceImpl dataQuery212ServiceImpl;
    private final RegionDataPushFactory pushFactory;

    // 每天凌晨1点同步设备信息
    @Scheduled(cron = "0 0 1 * * ?")
    public void syncDevices() {
        log.info("开始执行设备同步任务...");
        Integer county = 130726;
        // 获取所有需要推送的地区设备
        List<Device> yuxianDevices = deviceMapper.selectByRegion(county);

        if (!yuxianDevices.isEmpty()) {
            RegionDataPushService pushService = pushFactory.getService("130726");
            if (pushService != null) {
                pushService.syncDevices("130726", yuxianDevices);
            }
        }

        log.info("设备同步任务执行完成");
    }

    // 每5分钟推送一次大气数据
//    @Scheduled(cron = "0 */5 * * * ?")
    public void pushAirQualityData() throws ParseException {
        log.info("开始执行大气数据推送任务...");

        // 获取最近5分钟未推送的蔚县数据
        List<DataQuery212> yuxianData = dataQuery212ServiceImpl.selectRecentUnpushedDataByRegion();

        if (!yuxianData.isEmpty()) {
            RegionDataPushService pushService = pushFactory.getService("130726");
            if (pushService != null) {
                pushService.pushAirQualityData("130726", yuxianData);
            } else if (yuxianData == null || yuxianData.isEmpty()) {
                log.warn("未查询到需要推送的数据");
                return;
            }
        }
        log.info("大气数据推送任务执行完成，推送记录数: {}", yuxianData.size());
    }
}