package com.example.usersubscriptionservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

@FeignClient(name = "subscription-service", url = "${services.subscription-service.url}")
public interface SubscriptionServiceClient {

    @GetMapping("/api/subscriptions/{id}")
    Map<String, Object> getSubscriptionById(@PathVariable("id") Long id);
}
