package com.interview.backend.service;

import com.interview.backend.dto.LoginRequest;
import com.interview.backend.dto.LoginResponse;
import com.interview.backend.entity.UserAccount;
import com.interview.backend.exception.LoginFailedException;
import com.interview.backend.repository.UserAccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;

import java.util.UUID;

@Service
public class AuthService {
    private final UserAccountRepository userAccountRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(
        UserAccountRepository userAccountRepository,
        PasswordEncoder passwordEncoder
    ) {
        this.userAccountRepository = userAccountRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public LoginResponse login(LoginRequest request) {
        String username = normalize(request.username());
        String password = request.password();
        UserAccount user = userAccountRepository.findByUsernameAndActiveTrue(username)
                .orElseThrow(
                        () -> new LoginFailedException("用户名或密码错误")
                );

        if (!passwordMatches(password,user.getPasswordHash())) {
            throw new LoginFailedException("用户名或密码错误");
        }

        String token = generateToken(user);

        return new LoginResponse (
                user.getId(),
                user.getUsername(),
                user.getDisplayName(),
                token
        );

    }

    private String normalize(String value) {
        return value == null ? null : value.trim();
    }

    private boolean passwordMatches(String rawPassword, String passwordHash) {
        return StringUtils.hasText(rawPassword)
            && StringUtils.hasText(passwordHash)
            && passwordEncoder.matches(rawPassword, passwordHash);
    }

    private String generateToken(UserAccount user) {
        return UUID.randomUUID().toString();
    }
}
