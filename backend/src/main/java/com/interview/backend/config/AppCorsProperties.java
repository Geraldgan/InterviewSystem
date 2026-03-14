package com.interview.backend.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 跨域白名单配置。
 */
@Component
@ConfigurationProperties(prefix = "app.cors")
public class AppCorsProperties {

    /**
     * 允许访问后端接口的前端域名列表。
     */
    private List<String> allowedOrigins = new ArrayList<>();

    /**
     * 允许访问后端接口的前端域名模式，适合本地开发时的动态端口。
     */
    private List<String> allowedOriginPatterns = new ArrayList<>();

    public List<String> getAllowedOrigins() {
        return allowedOrigins;
    }

    public void setAllowedOrigins(List<String> allowedOrigins) {
        this.allowedOrigins = allowedOrigins;
    }

    public List<String> getAllowedOriginPatterns() {
        return allowedOriginPatterns;
    }

    public void setAllowedOriginPatterns(List<String> allowedOriginPatterns) {
        this.allowedOriginPatterns = allowedOriginPatterns;
    }
}
