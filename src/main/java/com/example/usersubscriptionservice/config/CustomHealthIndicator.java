package com.example.usersubscriptionservice.config;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class CustomHealthIndicator implements HealthIndicator {

    @Override
    public Health health() {
        // Add custom health checks here
        return Health.up()
                .withDetail("service", "user-subscription-service")
                .withDetail("status", "UP")
                .build();
    }
}