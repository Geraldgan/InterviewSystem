package com.interview.backend.dto;

public record LoginResponse(
        Long userId,
        String username,
        String displayName,
        String token
) {
}
