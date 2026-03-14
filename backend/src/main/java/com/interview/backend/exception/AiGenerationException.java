package com.interview.backend.exception;

/**
 * AI 生成失败时抛出的业务异常。
 */
public class AiGenerationException extends RuntimeException {

    public AiGenerationException(String message) {
        super(message);
    }

    public AiGenerationException(String message, Throwable cause) {
        super(message, cause);
    }
}
