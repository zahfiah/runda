package ruoyi.yuxian.controller;

import com.ruoyi.runda.domain.DataQuery212;
import com.ruoyi.runda.domain.Device;
import com.ruoyi.runda.mapper.DeviceMapper;
import com.ruoyi.runda.service.impl.DataQuery212ServiceImpl;
import ruoyi.yuxian.response.ApiResponse; // 根据实际包结构调整
import org.springframework.http.ResponseEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ruoyi.yuxian.service.RegionDataPushService;

import java.text.ParseException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
@Slf4j
@RestController
@RequestMapping("/api/push/data")
@RequiredArgsConstructor
public class PushDataController {
    @Autowired
    private  DataQuery212ServiceImpl dataQuery212ServiceImpl;
    @Autowired
    private RegionDataPushService regionDataPushService;

    @Autowired
    private DeviceMapper deviceMapper;

    @PostMapping("/pending")
    public ResponseEntity<ApiResponse<List<DataQuery212>>> getPendingPushData(
            @RequestParam String regionCode) {

        try {
            // 1. 查询未推送数据
            List<DataQuery212> allData = dataQuery212ServiceImpl.selectRecentUnpushedDataByRegion();

            // 2. 过滤出蔚县鹏辉未来城和蔚县五馆五中心建设项目的数据
            List<DataQuery212> filteredData = allData.stream()
                    .filter(data -> data.getDeviceId() != null) // 确保不为null
                    .filter(data -> "1470".equals(data.getDeviceId()) || "1620".equals(data.getDeviceId()))
                    .collect(Collectors.toList());
            log.info("查询到 {} 条过滤后数据", filteredData.size());
            // 2. 异步推送数据
            CompletableFuture.runAsync(() -> {
                try {
                    boolean pushResult = regionDataPushService.pushAirQualityData(regionCode, filteredData);
                    if (pushResult) {
                        log.info("数据推送成功，共推送{}条数据", filteredData.size());
                    } else {
                        log.error("数据推送失败");
                    }
                } catch (Exception e) {
                    log.error("异步推送数据失败", e);
                }
            });
            // 4. 返回查询结果
            return ResponseEntity.ok(ApiResponse.success(filteredData));

        } catch (Exception e) {
            log.error("获取待推送数据失败", e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("获取数据失败: " + e.getMessage()));
        }
    }

    @PostMapping("/devices")
    public ResponseEntity<ApiResponse<List<Device>>> pushDevices(
            @RequestParam String regionCode) {

        try {
            // 获取设备列表，并确保不为 null
            List<Device> devices = Optional.ofNullable(deviceMapper.selectByRegion(Integer.parseInt(regionCode)))
                    .orElse(Collections.emptyList());

            // 过滤设备时，确保 deviceId 不为 null
            devices = devices.stream()
                    .filter(device -> {
                        Long deviceId = device.getId();
                        return deviceId != null && (deviceId == 1470 || deviceId == 1620);
                    })
                    .collect(Collectors.toList());

            // 调用服务方法推送数据
            Boolean result = regionDataPushService.pushDeviceData(regionCode, devices);

            if (result != null && result) {
                log.info("成功推送 {} 条设备数据到区域 {}", devices.size(), regionCode);
            } else {
                log.warn("推送设备数据失败或无数据推送，区域代码: {}", regionCode);
            }

            // 如果推送成功，返回成功响应；否则返回错误响应
            return result != null && result
                    ? ResponseEntity.ok().body(ApiResponse.success(devices))
                    : ResponseEntity.badRequest().body(ApiResponse.error("推送设备数据失败"));
        } catch (NumberFormatException e) {
            log.error("区域代码解析失败: {}", regionCode, e);
            return ResponseEntity.badRequest().body(ApiResponse.error("无效的区域代码"));
        } catch (Exception e) {
            log.error("推送设备数据时发生异常", e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("内部服务器错误: " + e.getMessage()));
        }
    }

}