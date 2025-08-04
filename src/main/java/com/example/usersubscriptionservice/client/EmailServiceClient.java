// EmailServiceClient.java - mövcud client-lərin yanında əlavə edin
package com.example.usersubscriptionservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@FeignClient(name = "email-service", url = "${services.email-service.url}")
public interface EmailServiceClient {

    @PostMapping("/api/email/subscription-reminder")
    void sendSubscriptionReminder(@RequestBody Map<String, Object> emailRequest);

    @PostMapping("/api/email/subscription-added")
    void sendSubscriptionAddedNotification(@RequestBody Map<String, Object> emailRequest);

    @PostMapping("/api/email/subscription-updated")
    void sendSubscriptionUpdatedNotification(@RequestBody Map<String, Object> emailRequest);

    @PostMapping("/api/email/subscription-cancelled")
    void sendSubscriptionCancelledNotification(@RequestBody Map<String, Object> emailRequest);
}