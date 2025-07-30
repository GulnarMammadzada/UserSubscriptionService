package com.example.usersubscriptionservice.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class MonthlyCostSummary {
    private BigDecimal totalMonthlyCost;
    private String currency;
    private int activeSubscriptionCount;
    private BigDecimal averageCostPerSubscription;

    // Constructors
    public MonthlyCostSummary() {
    }

    public MonthlyCostSummary(BigDecimal totalMonthlyCost, String currency,
                              int activeSubscriptionCount, BigDecimal averageCostPerSubscription) {
        this.totalMonthlyCost = totalMonthlyCost;
        this.currency = currency;
        this.activeSubscriptionCount = activeSubscriptionCount;
        this.averageCostPerSubscription = averageCostPerSubscription;
    }
}