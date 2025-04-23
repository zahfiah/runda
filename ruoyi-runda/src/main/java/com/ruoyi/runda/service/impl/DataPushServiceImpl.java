package com.ruoyi.runda.service.impl;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.ruoyi.runda.domain.FilteredAirDataDTO;
import com.ruoyi.runda.domain.HourlyAverageAirDataCopy;
import com.ruoyi.runda.mapper.HourlyAverageAirDataMapper;
import com.ruoyi.runda.service.DataPushService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class DataPushServiceImpl implements DataPushService {

    @Autowired
    private HourlyAverageAirDataMapper hourlyAverageAirDataMapper;

    @Value("${api.push.url}")
    private String apiPushUrl; // 配置示例: http://zjk.dust.zjkyjhb.com/zjk/data/hours

    private static final SimpleDateFormat TIME_FORMATTER =
            new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    public List<HourlyAverageAirDataCopy> getHourlyAverageAirData(String dateTimeStr) {
        return hourlyAverageAirDataMapper.selectHourlyAverageAirDataByDate(dateTimeStr);
    }

    @Override
    public boolean batchPushAirData(List<FilteredAirDataDTO> dataList) {
        if (CollectionUtils.isEmpty(dataList)) {
            log.warn("推送数据为空");
            return false;
        }

        try {
            // 1. 转换数据结构
            List<Map<String, Object>> apiDataList = dataList.stream()
                    .map(data -> {
                        Map<String, Object> item = new HashMap<>();
                        item.put("mn", data.getSn());       // 字段名映射
                        item.put("pm10", data.getAveragePm10());
                        return item;
                    })
                    .collect(Collectors.toList());

            // 2. 构建请求体（完全匹配文档格式）
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("time", formatDate(dataList.get(0).getCreatedAt()));
            requestBody.put("data", apiDataList);

            log.debug("推送请求体: {}", requestBody);

            // 3. 发送HTTP请求
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

            ResponseEntity<Map> response = restTemplate.postForEntity(
                    apiPushUrl,
                    request,
                    Map.class);

            // 4. 处理响应
            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new RuntimeException("HTTP状态码异常: " + response.getStatusCode());
            }

            Map<String, Object> responseBody = response.getBody();
            if (responseBody == null || !Boolean.TRUE.equals(responseBody.get("result"))) {
                throw new RuntimeException("接口返回失败: " + responseBody);
            }

            log.info("推送成功，影响数据量: {}", dataList.size());
            return true;

        } catch (Exception e) {
            log.error("推送数据到{}失败: {}", apiPushUrl, e.getMessage());
            throw new RuntimeException("数据推送失败: " + e.getMessage(), e);
        }
    }

    // 线程安全的时间格式化
    private String formatDate(Date date) {
        synchronized (TIME_FORMATTER) {
            return TIME_FORMATTER.format(date);
        }
    }
}