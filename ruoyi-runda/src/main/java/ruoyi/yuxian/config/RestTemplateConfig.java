package ruoyi.yuxian.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();

        // 1. 配置消息转换器
        List<HttpMessageConverter<?>> converters = new ArrayList<>();

        // 1.1 添加表单转换器 (新增)
        FormHttpMessageConverter formHttpMessageConverter = new FormHttpMessageConverter();
        formHttpMessageConverter.setSupportedMediaTypes(Arrays.asList(
                MediaType.APPLICATION_FORM_URLENCODED,
                MediaType.MULTIPART_FORM_DATA
        ));
        converters.add(formHttpMessageConverter);

        // 1.2 添加JSON转换器
        MappingJackson2HttpMessageConverter jsonConverter = new MappingJackson2HttpMessageConverter();
        jsonConverter.setSupportedMediaTypes(Arrays.asList(
                MediaType.APPLICATION_JSON,
                MediaType.TEXT_HTML,
                MediaType.TEXT_PLAIN
        ));
        converters.add(jsonConverter);

        // 1.3 添加字符串转换器
        converters.add(new StringHttpMessageConverter(StandardCharsets.UTF_8));

        restTemplate.setMessageConverters(converters);

        // 2. 配置错误处理器
        restTemplate.setErrorHandler(new DefaultResponseErrorHandler() {
            @Override
            public void handleError(ClientHttpResponse response) throws IOException {
                if (response.getHeaders().getContentType().includes(MediaType.TEXT_HTML)) {
                    String body = StreamUtils.copyToString(response.getBody(), StandardCharsets.UTF_8);
                    throw new HttpClientErrorException(
                            response.getStatusCode(),
                            "服务器返回HTML响应: " + body);
                }
                super.handleError(response);
            }
        });

        return restTemplate;
    }
}