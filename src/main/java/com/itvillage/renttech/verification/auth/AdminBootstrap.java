package com.itvillage.renttech.verification.auth;

import com.itvillage.renttech.verification.user.User;
import com.itvillage.renttech.verification.user.UserService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(AdminBootstrap.AdminProperties.class)
public class AdminBootstrap {

    private final UserService userService;
    private final AdminProperties properties;

    @Bean
    public ApplicationRunner seedAdminUsers() {
        return args -> {
            List<AdminCredentials> admins = properties.getAdmins();
            if (admins == null || admins.isEmpty()) {
                log.warn("application.admins not configured — skipping admin bootstrap");
                return;
            }
            for (AdminCredentials a : admins) {
                if (a.getMobileNo() == null || a.getMobileNo().isBlank()
                        || a.getPassword() == null || a.getPassword().isBlank()) {
                    log.warn("Skipping admin entry with missing mobile-no or password");
                    continue;
                }
                if (userService.getUserByMobileNoNumber(a.getMobileNo()).isPresent()) {
                    log.info("Admin user already exists for mobile={} — skipped", a.getMobileNo());
                    continue;
                }
                User admin = userService.createAdminUser(a.getMobileNo(), a.getPassword());
                log.info("Bootstrapped admin user id={} mobile={}", admin.getId(), a.getMobileNo());
            }
        };
    }

    @Data
    @ConfigurationProperties(prefix = "application")
    public static class AdminProperties {
        private List<AdminCredentials> admins = new ArrayList<>();
    }

    @Data
    public static class AdminCredentials {
        private String mobileNo;
        private String password;
    }
}
