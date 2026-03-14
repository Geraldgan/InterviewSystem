package com.interview.backend.config;

import java.time.Duration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

/**
 * 外部 HTTP 调用客户端配置。
 */
@Configuration
public class RestClientConfig {

    /**
     * 创建访问 OpenAI 的 RestClient。
     *
     * @param builder builder 实例
     * @param openAiProperties OpenAI 配置
     * @return 预先设置基础地址与超时时间的 RestClient
     */
    @Bean
    public RestClient openAiRestClient(RestClient.Builder builder, OpenAiProperties openAiProperties) {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(Duration.ofSeconds(openAiProperties.getRequestTimeoutSeconds()));
        requestFactory.setReadTimeout(Duration.ofSeconds(openAiProperties.getRequestTimeoutSeconds()));

        return builder
            .baseUrl(openAiProperties.getBaseUrl())
            .defaultHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
            .requestFactory(requestFactory)
            .build();
    }
}
