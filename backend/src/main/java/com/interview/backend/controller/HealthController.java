package com.interview.backend.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 最基础的健康检查接口。
 */
@RestController
@RequestMapping("/api/health")
public class HealthController {

    /**
     * 返回服务当前状态。
     *
     * @return 简单状态数据
     */
    @GetMapping
//    public Map<String, String> health() {
//        return Map.of("status", "ok");
//    }
    public Map<String, String> health2() {
        return Map.of(
            "status","ok3"
        );
    }
}
