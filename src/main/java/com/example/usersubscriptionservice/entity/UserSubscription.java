package com.example.usersubscriptionservice.entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "user_subscriptions")
public class UserSubscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Username is required")
    @Column(name = "username", nullable = false)
    private String username;

    @NotNull(message = "Subscription ID is required")
    @Column(name = "subscription_id", nullable = false)
    private Long subscriptionId;

    @NotNull(message = "Start date is required")
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @NotNull(message = "Next billing date is required")
    @Column(name = "next_billing_date", nullable = false)
    private LocalDate nextBillingDate;

    @NotNull(message = "Price is required")
    @Positive(message = "Price must be positive")
    @Column(name = "monthly_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal monthlyPrice;

    @Column(name = "currency", nullable = false)
    private String currency = "AZN";

    @Column(name = "billing_period", nullable = false)
    private String billingPeriod = "MONTHLY"; // MONTHLY, YEARLY

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "notes")
    private String notes;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public UserSubscription() {}

    public UserSubscription(String username, Long subscriptionId, LocalDate startDate,
                            LocalDate nextBillingDate, BigDecimal monthlyPrice, String currency, String billingPeriod) {
        this.username = username;
        this.subscriptionId = subscriptionId;
        this.startDate = startDate;
        this.nextBillingDate = nextBillingDate;
        this.monthlyPrice = monthlyPrice;
        this.currency = currency;
        this.billingPeriod = billingPeriod;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.isActive = true;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

}
