package com.interview.backend.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * OpenAI 接口访问配置。
 */
@Component
@ConfigurationProperties(prefix = "app.openai")
public class OpenAiProperties {

    private String baseUrl = "https://api.openai.com/v1";

    private String apiKey;

    private String model = "gpt-5-mini";

    private String reasoningEffort = "low";

    private int requestTimeoutSeconds = 60;

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getReasoningEffort() {
        return reasoningEffort;
    }

    public void setReasoningEffort(String reasoningEffort) {
        this.reasoningEffort = reasoningEffort;
    }

    public int getRequestTimeoutSeconds() {
        return requestTimeoutSeconds;
    }

    public void setRequestTimeoutSeconds(int requestTimeoutSeconds) {
        this.requestTimeoutSeconds = requestTimeoutSeconds;
    }

    /**
     * 判断当前是否具备调用 OpenAI 的最基本条件。
     *
     * @return 是否存在非空 API Key
     */
    public boolean hasApiKey() {
        return StringUtils.hasText(apiKey);
    }
}
