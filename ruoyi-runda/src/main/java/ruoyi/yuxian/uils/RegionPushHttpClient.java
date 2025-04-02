package ruoyi.yuxian.uils;
import lombok.RequiredArgsConstructor;
import ruoyi.yuxian.config.YuxianPushProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Component;

import org.springframework.web.client.RestTemplate;


import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


@Slf4j
@Component
@RequiredArgsConstructor
public class RegionPushHttpClient {
    private final RestTemplate restTemplate;
    private final YuxianPushProperties properties;

    private final Map<String, String> accessTokenCache = new ConcurrentHashMap<>();
    private final Map<String, Long> tokenExpireTimeCache = new ConcurrentHashMap<>();

    public String getAccessToken(String regionCode) {
        YuxianPushProperties.RegionConfig config = properties.getRegions().get(regionCode);
        if (config == null) {
            throw new IllegalArgumentException("无效的地区代码: " + regionCode);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("tag", "twins_local");
        headers.set("tenant-id", config.getTenantId()); // 从配置读取tenant-id

        // 构建JSON请求体
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("appId", config.getAppId());
        requestBody.put("appSecret", config.getAppSecret());

        HttpEntity<Map<String, String>> request = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(
                    config.getAuthUrl(),
                    request,
                    Map.class);

            // 解析响应
            Map<String, Object> body = response.getBody();
            if (body == null) {
                throw new RuntimeException("响应体为空");
            }

            // 检查业务状态码
            if (body.containsKey("code") && !"0".equals(String.valueOf(body.get("code")))) {
                String msg = body.containsKey("msg") ? (String) body.get("msg") : "未知错误";
                throw new RuntimeException("获取token失败: " + msg);
            }

            // 提取token
            if (body.containsKey("data")) {
                Map<String, Object> data = (Map<String, Object>) body.get("data");
                if (data != null && data.containsKey("accessToken")) {
                    return (String) data.get("accessToken");
                }
            }
            throw new RuntimeException("响应中缺少accessToken字段");
        } catch (Exception e) {
            log.error("获取Token异常: {}", e.getMessage());
            throw new RuntimeException("获取token失败: " + e.getMessage(), e);
        }
    }

    private String extractAccessToken(ResponseEntity<Map> response) {
        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            Map<String, Object> body = response.getBody();

            // 调试日志 - 打印完整响应
            log.debug("Token接口响应: {}", body);

            // 尝试多种可能的token字段名
            String[] possibleTokenFields = {"access_token", "accessToken", "token"};
            for (String field : possibleTokenFields) {
                if (body.containsKey(field)) {
                    return (String) body.get(field);
                }
            }

            // 如果标准字段都不存在，尝试遍历所有值
            for (Object value : body.values()) {
                if (value instanceof String && ((String) value).length() > 32) {
                    return (String) value;
                }
            }
        }
        throw new RuntimeException("获取token失败: " + response.getStatusCode() +
                ", 响应体: " + response.getBody());
    }


    private void waitForRetry(int retryCount) {
        try {
            Thread.sleep(1000L * retryCount); // 指数退避
        } catch (InterruptedException ignored) {
            Thread.currentThread().interrupt();
        }
    }

    public <T> T post(String regionCode, String url, Object requestBody, Class<T> responseType) {
        String accessToken = getAccessToken(regionCode);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + accessToken);
        headers.set("tag", "twins_local");
        headers.set("tenant-id", properties.getRegions().get(regionCode).getTenantId());

        HttpEntity<Object> request = new HttpEntity<>(requestBody, headers);

        int retryCount = 0;
        while (retryCount <= properties.getHttp().getMaxRetry()) {
            try {
                // 修改这里：使用postForObject代替postForEntity
                T responseBody = restTemplate.postForObject(url, request, responseType);
                log.debug("请求成功 - 区域: {}, URL: {}, 响应: {}", regionCode, url, responseBody);
                return responseBody;
            } catch (Exception e) {
                log.error("请求{}地区接口失败，URL: {}", regionCode, url, e);
                if (retryCount == properties.getHttp().getMaxRetry()) {
                    throw new RuntimeException("请求" + regionCode + "地区接口失败: " + url, e);
                }

                // 清除token缓存，下次重试会重新获取
                accessTokenCache.remove(regionCode);
                tokenExpireTimeCache.remove(regionCode);

                waitForRetry(retryCount);
            }
            retryCount++;
        }
        throw new RuntimeException("请求" + regionCode + "地区接口失败: " + url);
    }

    public <T> T request(String regionCode, String url, Object requestBody, Class<T> responseType) {
        try {
            YuxianPushProperties.RegionConfig config = properties.getRegions().get(regionCode);
            if (config == null) {
                throw new IllegalArgumentException("无效的地区代码: " + regionCode);
            }

            String accessToken = getAccessToken(regionCode);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + accessToken);
            headers.set("tag", "twins_local");
            headers.set("tenant-id", config.getTenantId());

            HttpEntity<Object> request = new HttpEntity<>(requestBody, headers);

            log.debug("推送请求 - 区域: {}, URL: {}", regionCode, url);

            // 修改这里：使用postForObject代替exchange
            T responseBody = restTemplate.postForObject(url, request, responseType);

            log.debug("推送成功 - 区域: {}, 响应: {}", regionCode, responseBody);
            return responseBody;

        } catch (Exception e) {
            log.error("推送异常 - 区域: {}, 错误: {}", regionCode, e.getMessage(), e);
            throw new RuntimeException("推送请求异常", e);
        }
    }

    public <T> T put(String regionCode, String url, Object requestBody, Class<T> responseType) {
        String accessToken = getAccessToken(regionCode);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + accessToken);
        headers.set("tag", "twins_local");
        headers.set("tenant-id", properties.getRegions().get(regionCode).getTenantId());

        HttpEntity<Object> request = new HttpEntity<>(requestBody, headers);

        int retryCount = 0;
        while (retryCount <= properties.getHttp().getMaxRetry()) {
            try {
                ResponseEntity<T> response = restTemplate.exchange(
                        url,
                        HttpMethod.PUT,
                        request,
                        responseType);

                log.debug("PUT请求成功 - 区域: {}, URL: {}, 响应: {}", regionCode, url, response.getBody());
                return response.getBody();
            } catch (Exception e) {
                log.error("PUT请求{}地区接口失败，URL: {}", regionCode, url, e);
                if (retryCount == properties.getHttp().getMaxRetry()) {
                    throw new RuntimeException("PUT请求" + regionCode + "地区接口失败: " + url, e);
                }

                accessTokenCache.remove(regionCode);
                tokenExpireTimeCache.remove(regionCode);

                waitForRetry(retryCount);
            }
            retryCount++;
        }
        throw new RuntimeException("PUT请求" + regionCode + "地区接口失败: " + url);
    }
}