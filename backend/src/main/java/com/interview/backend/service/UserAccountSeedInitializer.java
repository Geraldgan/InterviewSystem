package com.interview.backend.service;

import com.interview.backend.entity.UserAccount;
import com.interview.backend.repository.UserAccountRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * 启动时准备一个可直接联调的演示账号。
 */
@Component
public class UserAccountSeedInitializer implements ApplicationRunner {

    private static final String DEMO_USERNAME = "test";
    private static final String DEMO_PASSWORD = "123456";
    private static final String DEMO_DISPLAY_NAME = "测试用户";

    private final UserAccountRepository userAccountRepository;
    private final PasswordEncoder passwordEncoder;

    public UserAccountSeedInitializer(
        UserAccountRepository userAccountRepository,
        PasswordEncoder passwordEncoder
    ) {
        this.userAccountRepository = userAccountRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(ApplicationArguments args) {
        UserAccount account = userAccountRepository.findByUsername(DEMO_USERNAME)
            .orElseGet(UserAccount::new);

        account.setUsername(DEMO_USERNAME);
        account.setDisplayName(DEMO_DISPLAY_NAME);
        account.setActive(true);

        if (needsPasswordRefresh(account.getPasswordHash())) {
            account.setPasswordHash(passwordEncoder.encode(DEMO_PASSWORD));
        }

        userAccountRepository.save(account);
    }

    private boolean needsPasswordRefresh(String passwordHash) {
        return !StringUtils.hasText(passwordHash)
            || !passwordEncoder.matches(DEMO_PASSWORD, passwordHash);
    }
}
