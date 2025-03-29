package ruoyi.yuxian.uils;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.HttpClientErrorException;
import ruoyi.yuxian.config.YuxianPushProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;


import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
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

        // 先尝试GET请求
        String getUrl = String.format("%s?client_id=%s&client_secret=%s&grant_type=client_credentials&tenant_id=%s",
                config.getAuthUrl(),
                URLEncoder.encode(config.getAppId(), StandardCharsets.UTF_8),
                URLEncoder.encode(config.getAppSecret(), StandardCharsets.UTF_8),
                config.getTenantId());

        try {
            ResponseEntity<Map> response = restTemplate.getForEntity(getUrl, Map.class);
            return extractAccessToken(response);
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.METHOD_NOT_ALLOWED) {
                // GET失败后尝试POST
                return tryPostForToken(config);
            }
            throw e;
        }
    }

    private String tryPostForToken(YuxianPushProperties.RegionConfig config) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("client_id", config.getAppId());
        params.add("client_secret", config.getAppSecret());
        params.add("grant_type", "client_credentials");
        params.add("tenant_id", config.getTenantId());

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(
                config.getAuthUrl(),
                request,
                Map.class);

        return extractAccessToken(response);
    }

    private String extractAccessToken(ResponseEntity<Map> response) {
        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            Map<String, Object> body = response.getBody();
            if (body.containsKey("access_token")) {
                return (String) body.get("access_token");
            }
        }
        throw new RuntimeException("获取token失败: " + response.getStatusCode());
    }

    private void handleHttpClientError(String regionCode, HttpClientErrorException e, int retryCount) {
        if (e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
            log.warn("认证失效，清除token缓存");
            accessTokenCache.remove(regionCode);
            tokenExpireTimeCache.remove(regionCode);
        }
        log.error("HTTP请求失败 [状态: {}], 重试 {}/{}",
                e.getStatusCode(), retryCount, properties.getHttp().getMaxRetry());
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

        HttpEntity<Object> request = new HttpEntity<>(requestBody, headers);

        int retryCount = 0;
        while (retryCount <= properties.getHttp().getMaxRetry()) {
            try {
                ResponseEntity<T> response = restTemplate.postForEntity(url, request, responseType);
                if (response.getStatusCode().is2xxSuccessful()) {
                    return response.getBody();
                }
            } catch (Exception e) {
                log.error("请求{}地区接口失败，URL: {}", regionCode, url, e);
                if (retryCount == properties.getHttp().getMaxRetry()) {
                    throw new RuntimeException("请求" + regionCode + "地区接口失败: " + url, e);
                }

                // 清除token缓存，下次重试会重新获取
                accessTokenCache.remove(regionCode);
                tokenExpireTimeCache.remove(regionCode);

                try {
                    Thread.sleep(1000 * (retryCount + 1));
                } catch (InterruptedException ignored) {
                }
            }
            retryCount++;
        }

        throw new RuntimeException("请求" + regionCode + "地区接口失败: " + url);
    }

    public <T> T request(String regionCode, String url, Object requestBody, Class<T> responseType) {
        int retryCount = 0;
        while (retryCount <= properties.getHttp().getMaxRetry()) {
            try {
                String accessToken = getAccessToken(regionCode);
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                headers.set("Authorization", "Bearer " + accessToken);
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

                HttpEntity<Object> request = new HttpEntity<>(requestBody, headers);

                // 使用ClientHttpRequestInterceptor处理原始响应
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.setInterceptors(Collections.singletonList(new ErrorHandlingInterceptor()));

                // 明确设置消息转换器
                List<HttpMessageConverter<?>> converters = new ArrayList<>();
                converters.add(new MappingJackson2HttpMessageConverter());
                converters.add(new StringHttpMessageConverter());
                restTemplate.setMessageConverters(converters);

                // 执行请求
                ResponseEntity<T> response = restTemplate.exchange(
                        url,
                        HttpMethod.POST,
                        request,
                        responseType);

                return response.getBody();

            } catch (HttpClientErrorException e) {
                handleHttpError(regionCode, url, retryCount, e);
                retryCount++;
            } catch (Exception e) {
                log.error("请求异常 [URL: {}], 重试 {}/{}", url, retryCount,
                        properties.getHttp().getMaxRetry(), e);
                if (retryCount == properties.getHttp().getMaxRetry()) {
                    throw new RuntimeException("请求失败，超过最大重试次数", e);
                }
                sleepBeforeRetry(retryCount);
                retryCount++;
            }
        }
        throw new RuntimeException("请求失败");
    }

    // 错误处理拦截器
    private static class ErrorHandlingInterceptor implements ClientHttpRequestInterceptor {
        @Override
        public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
            ClientHttpResponse response = execution.execute(request, body);
            if (response.getHeaders().getContentType().includes(MediaType.TEXT_HTML)) {
                String responseBody = StreamUtils.copyToString(response.getBody(), StandardCharsets.UTF_8);
                throw new HttpClientErrorException(
                        response.getStatusCode(),
                        "服务端返回HTML响应: " + responseBody);
            }
            return response;
        }
    }

    private void handleHttpError(String regionCode, String url, int retryCount, HttpClientErrorException e) {
        // 清除token缓存
        accessTokenCache.remove(regionCode);
        tokenExpireTimeCache.remove(regionCode);

        log.error("HTTP错误 [状态: {}], URL: {}, 响应: {}, 重试 {}/{}",
                e.getStatusCode(), url, e.getResponseBodyAsString(),
                retryCount, properties.getHttp().getMaxRetry());

        if (retryCount == properties.getHttp().getMaxRetry()) {
            throw new RuntimeException("最终请求失败: " + e.getMessage(), e);
        }

        sleepBeforeRetry(retryCount);
    }

    private void sleepBeforeRetry(int retryCount) {
        try {
            Thread.sleep(50000 * (1 << retryCount)); // 指数退避
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
        }
    }
}