package com.interview.backend.dto;

import java.time.LocalDateTime;

/**
 * 统一错误响应。
 *
 * @param timestamp 错误发生时间
 * @param message 人类可读的错误描述
 * @param path 发生错误的请求路径
 */
public record ErrorResponse(
    LocalDateTime timestamp,
    String message,
    String path
) {
}
