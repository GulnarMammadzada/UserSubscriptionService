package com.example.usersubscriptionservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableFeignClients
@EnableScheduling
public class UserSubscriptionServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserSubscriptionServiceApplication.class, args);
    }

}
