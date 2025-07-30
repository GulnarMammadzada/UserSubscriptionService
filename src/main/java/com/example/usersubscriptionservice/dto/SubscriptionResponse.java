package com.example.usersubscriptionservice.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class SubscriptionResponse {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private String currency;
    private String category;
    private String billingPeriod;
    private String websiteUrl;
    private String logoUrl;
    private Boolean isActive;

    // Constructors
    public SubscriptionResponse() {
    }
}
