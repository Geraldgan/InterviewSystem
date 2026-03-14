package com.interview.backend.config;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

/**
 * 后端接口跨域配置，方便本地 H5 与小程序调试。
 */
@Configuration
public class CorsConfig {

    /**
     * 为 `/api/**` 提供跨域配置。
     *
     * @param corsProperties 跨域白名单配置
     * @return Spring Web 使用的跨域配置源
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource(AppCorsProperties corsProperties) {
        return request -> {
            if (request.getRequestURI() == null || !request.getRequestURI().startsWith("/api/")) {
                return null;
            }

            CorsConfiguration configuration = new CorsConfiguration();
            configuration.setAllowedOrigins(new ArrayList<>(corsProperties.getAllowedOrigins()));
            configuration.setAllowedOriginPatterns(new ArrayList<>(corsProperties.getAllowedOriginPatterns()));

            String origin = request.getHeader(HttpHeaders.ORIGIN);
            if (origin != null && isTrustedDevelopmentOrigin(origin)) {
                // 本地开发时端口经常变化，这里动态放行当前请求来源，减少手动改白名单次数。
                configuration.addAllowedOrigin(origin);
            }

            configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
            configuration.setAllowedHeaders(List.of("*"));
            configuration.setAllowCredentials(false);
            return configuration;
        };
    }

    /**
     * 显式注册 CORS 过滤器，优先处理浏览器的预检请求。
     *
     * @param corsConfigurationSource 跨域配置源
     * @return 可供 Spring Boot 注册的过滤器
     */
    @Bean
    public CorsFilter corsFilter(CorsConfigurationSource corsConfigurationSource) {
        return new CorsFilter(corsConfigurationSource);
    }

    /**
     * 识别本地浏览器与局域网真机调试常见的来源地址。
     *
     * @param origin 浏览器发送的 Origin 头
     * @return 是否属于可信的开发环境来源
     */
    private boolean isTrustedDevelopmentOrigin(String origin) {
        try {
            URI uri = URI.create(origin);
            String scheme = uri.getScheme();
            String host = uri.getHost();

            if (scheme == null || host == null) {
                return false;
            }

            if (!"http".equalsIgnoreCase(scheme) && !"https".equalsIgnoreCase(scheme)) {
                return false;
            }

            return "localhost".equalsIgnoreCase(host)
                || "127.0.0.1".equals(host)
                || "::1".equals(host)
                || isPrivateIpv4Host(host);
        } catch (IllegalArgumentException exception) {
            return false;
        }
    }

    /**
     * 识别 RFC1918 私网 IPv4 地址，便于手机和微信小程序访问电脑本机服务。
     *
     * @param host Origin 里的主机名
     * @return 是否为私网 IPv4
     */
    private boolean isPrivateIpv4Host(String host) {
        String[] segments = host.split("\\.");
        if (segments.length != 4) {
            return false;
        }

        try {
            int first = Integer.parseInt(segments[0]);
            int second = Integer.parseInt(segments[1]);

            return first == 10
                || (first == 172 && second >= 16 && second <= 31)
                || (first == 192 && second == 168);
        } catch (NumberFormatException exception) {
            return false;
        }
    }
}
