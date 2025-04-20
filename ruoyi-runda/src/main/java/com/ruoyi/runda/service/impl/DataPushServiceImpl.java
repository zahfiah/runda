package com.ruoyi.runda.service.impl;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.ruoyi.runda.domain.HourlyAverageAirData;
import com.ruoyi.runda.mapper.HourlyAverageAirDataMapper;
import com.ruoyi.runda.service.DataPushService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;


@Service
public class DataPushServiceImpl implements DataPushService {

    @Autowired
    private HourlyAverageAirDataMapper hourlyAverageAirDataMapper;

    @Value("${api.push.url}")
    private String apiPushUrl;
    @Override
    public List<HourlyAverageAirData> getHourlyAverageAirData() {
        LocalDateTime nowDate = LocalDateTime.now();
        // 提取年、月、日和小时
        int year = nowDate.getYear();
        int month = nowDate.getMonthValue();
        int day = nowDate.getDayOfMonth();
        int hour = nowDate.getHour()-1;

        //拼接成字符串形式
        String dateTimeStr = String.format("%d-%02d-%02d %02d:00", year, month, day, hour);
        // 使用MyBatis查询数据
        return hourlyAverageAirDataMapper.selectHourlyAverageAirDataByDate(dateTimeStr);
    }

    @Override
    public boolean batchPushAirData(List<HourlyAverageAirData> dataList) {
        if (CollectionUtils.isEmpty(dataList)) {
            return false;
        }

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        try {
            // 分批推送，每批100条
            int batchSize = 100;
            for (int i = 0; i < dataList.size(); i += batchSize) {
                int end = Math.min(i + batchSize, dataList.size());
                List<HourlyAverageAirData> batchList = dataList.subList(i, end);

                HttpEntity<List<HourlyAverageAirData>> request =
                        new HttpEntity<>(batchList, headers);

                ResponseEntity<String> response = restTemplate.postForEntity(
                        apiPushUrl,
                        request,
                        String.class);

                if (!response.getStatusCode().is2xxSuccessful()) {
                    throw new RuntimeException("API返回非成功状态码: " + response.getStatusCodeValue());
                }
            }
            return true;
        } catch (Exception e) {
            throw new RuntimeException("推送数据失败", e);
        }
    }
}
