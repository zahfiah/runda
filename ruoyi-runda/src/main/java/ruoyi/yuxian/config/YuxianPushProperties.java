package ruoyi.yuxian.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;


@ConfigurationProperties(prefix = "yuxian.push")
@Data
public class YuxianPushProperties {
    private boolean enabled;
    private Map<String, RegionConfig> regions;
    private HttpConfig http;

    @Data
    public static class RegionConfig {
        private String authUrl;
        private String tenantId;
        private String appId;
        private String appSecret;
        private String deviceCreateUrl;  // 确保这个字段存在
        private String dataPushUrl;
        private String projectId;
    }

    @Data
    public static class HttpConfig {
        private int connectTimeout;
        private int readTimeout;
        private int maxRetry;
    }
}