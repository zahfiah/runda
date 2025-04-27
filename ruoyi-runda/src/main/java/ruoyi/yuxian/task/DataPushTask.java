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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataPushTask {
    private final DeviceMapper deviceMapper;
    private final  DataQuery212ServiceImpl dataQuery212ServiceImpl;
    private final RegionDataPushFactory pushFactory;

    // 每10分钟推送一次大气数据
    // 每小时的第12分钟执行一次大气数据推送任务
    @Scheduled(cron = "0 12 * * * ?")
    public void pushAirQualityDataAt12() {
        pushAirQualityData();
    }

    // 每小时的第22分钟、32分钟、42分钟和52分钟执行一次大气数据推送任务
    @Scheduled(cron = "0 22,32,42,52 * * * ?")
    public void pushAirQualityDataAt22_32_42_52() {
        pushAirQualityData();
    }
    public void pushAirQualityData() {
        log.info("开始执行大气数据推送任务...");

        try {
            // 1. 查询未推送数据
            List<DataQuery212> allData = dataQuery212ServiceImpl.selectRecentUnpushedDataByRegion();
            log.info("查询到 {} 条未推送数据", allData.size());
            // 2. 过滤出蔚县鹏辉未来城和蔚县五馆五中心建设项目的数据
            List<DataQuery212> filteredData = allData.stream()
                    .filter(data -> data.getDeviceId() != null) // 确保不为null
                    .filter(data -> "1470".equals(data.getDeviceId()) || "1620".equals(data.getDeviceId()))
                    .collect(Collectors.toList());
            log.info("查询到 {} 条过滤后数据", filteredData.size());


            if (filteredData == null || filteredData.isEmpty()) {
                log.warn("未查询到需要推送的数据");
                return;
            }

            RegionDataPushService pushService = pushFactory.getService("130726");
            if (pushService != null) {
                boolean result = pushService.pushAirQualityData("130726", filteredData);
                log.info("大气数据推送任务执行完成，推送{}条记录，结果: {}",
                        filteredData.size(), result ? "成功" : "失败");
            } else {
                log.error("未找到区域[130726]的推送服务实现");
            }
        } catch (Exception e) {
            log.error("大气数据推送任务执行异常", e);
        }
    }

    // 定时任务方法
//    @Scheduled(cron = "0 0/5 * * * ?") // 每5分钟执行一次
    //每5秒执行一次
//    @Scheduled(fixedRate = 5000)
    public void pushDevicesTask() {
        String regionCode = "130726"; // 可以配置化或从数据库获取

        try {
            // 获取设备列表，并确保不为 null
            List<Device> devices = Optional.ofNullable(deviceMapper.selectByRegion(Integer.parseInt(regionCode)))
                    .orElse(Collections.emptyList());
            log.info("查询到 {} 条设备数据", devices.size());
            // 过滤设备时，确保 deviceId 不为 null
            devices = devices.stream()
                    .filter(device -> {
                        Long deviceId = device.getId();
                        return deviceId != null && (deviceId == 1470 || deviceId == 1620);
                    })
                    .collect(Collectors.toList());
            log.info("过滤后查询到 {} 条设备数据", devices.size());
            RegionDataPushService pushService = pushFactory.getService("130726");
            // 调用服务方法推送数据
            Boolean result = pushService.pushDeviceData(regionCode, devices);

            if (result != null && result) {
                log.info("定时任务成功推送 {} 条设备数据到区域 {}", devices.size(), regionCode);
            } else {
                log.warn("定时任务推送设备数据失败或无数据推送，区域代码: {}", regionCode);
            }
        } catch (NumberFormatException e) {
            log.error("定时任务区域代码解析失败: {}", regionCode, e);
        } catch (Exception e) {
            log.error("定时任务推送设备数据时发生异常", e);
        }
    }

}