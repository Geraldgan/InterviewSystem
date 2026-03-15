package com.interview.backend.controller;

import com.interview.backend.dto.LoginRequest;
import com.interview.backend.dto.LoginResponse;
import com.interview.backend.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public LoginResponse login (@Valid @RequestBody LoginRequest loginRequest) {
        return authService.login(loginRequest);
    }
}
