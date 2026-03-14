package com.interview.backend.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * AI 功能开关配置。
 */
@Component
@ConfigurationProperties(prefix = "app.ai")
public class AiFeatureProperties {

    /**
     * 当没有配置 OpenAI API Key 时，是否允许自动切换到本地模拟数据。
     */
    private boolean mockEnabled = true;

    public boolean isMockEnabled() {
        return mockEnabled;
    }

    public void setMockEnabled(boolean mockEnabled) {
        this.mockEnabled = mockEnabled;
    }
}
