package com.example.usersubscriptionservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@FeignClient(name = "email-service", url = "${services.email-service.url}")
public interface EmailServiceClient {

    @PostMapping("/api/email/subscription-reminder")
    Map<String, Object> sendSubscriptionReminder(@RequestBody Map<String, Object> emailRequest);
}