package com.example.usersubscriptionservice.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDate;

@Data
public class UserSubscriptionRequest {
    @NotNull(message = "Subscription ID is required")
    private Long subscriptionId;

    @NotNull(message = "Start date is required")
    private LocalDate startDate;

    @NotNull(message = "Next billing date is required")
    private LocalDate nextBillingDate;

    private String notes;

    public UserSubscriptionRequest() {
    }

    public UserSubscriptionRequest(Long subscriptionId, LocalDate startDate, LocalDate nextBillingDate, String notes) {
        this.subscriptionId = subscriptionId;
        this.startDate = startDate;
        this.nextBillingDate = nextBillingDate;
        this.notes = notes;
    }
}
