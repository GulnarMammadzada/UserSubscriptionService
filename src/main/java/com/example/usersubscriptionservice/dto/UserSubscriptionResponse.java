package com.example.usersubscriptionservice.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class UserSubscriptionResponse {
    private Long id;
    private String username;
    private Long subscriptionId;
    private String subscriptionName;
    private String subscriptionCategory;
    private LocalDate startDate;
    private LocalDate nextBillingDate;
    private BigDecimal monthlyPrice;
    private String currency;
    private String billingPeriod;
    private String notes;
    private Boolean isActive;
    private String logoUrl;
    private String websiteUrl;

    public UserSubscriptionResponse() {
    }
}
