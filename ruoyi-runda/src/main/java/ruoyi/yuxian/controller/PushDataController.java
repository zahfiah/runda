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
import java.util.List;
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

    @GetMapping("/pending")
    public ResponseEntity<ApiResponse<List<DataQuery212>>> getPendingPushData(
            @RequestParam String regionCode) {

        try {
            // 1. 查询未推送数据
            List<DataQuery212> allData = dataQuery212ServiceImpl.selectRecentUnpushedDataByRegion();

            // 2. 过滤出stationId为1126和926的数据
            List<DataQuery212> filteredData = allData.stream()
                    .filter(data -> data.getStationId() == 1126 || data.getStationId() == 926)
                    .collect(Collectors.toList());

            // 3. 异步推送数据
            if (!filteredData.isEmpty()) {
                CompletableFuture.runAsync(() -> {
                    try {
                        regionDataPushService.pushAirQualityData(regionCode, filteredData);
                    } catch (Exception e) {
                        log.error("异步推送数据失败", e);
                    }
                });
            }

            // 4. 返回查询结果
            return ResponseEntity.ok(ApiResponse.success(filteredData));

        } catch (Exception e) {
            log.error("获取待推送数据失败", e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("获取数据失败: " + e.getMessage()));
        }
    }


}