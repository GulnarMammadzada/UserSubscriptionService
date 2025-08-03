package com.example.usersubscriptionservice.service;

import com.example.usersubscriptionservice.client.EmailServiceClient;
import com.example.usersubscriptionservice.client.SubscriptionServiceClient;
import com.example.usersubscriptionservice.client.UserServiceClient;
import com.example.usersubscriptionservice.dto.MonthlyCostSummary;
import com.example.usersubscriptionservice.dto.SubscriptionResponse;
import com.example.usersubscriptionservice.dto.UserSubscriptionRequest;
import com.example.usersubscriptionservice.dto.UserSubscriptionResponse;
import com.example.usersubscriptionservice.entity.UserSubscription;
import com.example.usersubscriptionservice.repository.UserSubscriptionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class UserSubscriptionService {

    private static final Logger logger = LoggerFactory.getLogger(UserSubscriptionService.class);

    @Autowired
    private UserSubscriptionRepository userSubscriptionRepository;

    @Autowired
    private UserServiceClient userServiceClient;

    @Autowired
    private SubscriptionServiceClient subscriptionServiceClient;

    @Autowired
    private EmailServiceClient emailServiceClient;

    @Transactional
    public UserSubscriptionResponse createUserSubscription(String username, UserSubscriptionRequest request) {
        logger.info("Creating subscription for user: {}, subscription ID: {}", username, request.getSubscriptionId());

        // Check if user already has this subscription
        if (userSubscriptionRepository.existsByUsernameAndSubscriptionIdAndIsActive(
                username, request.getSubscriptionId(), true)) {
            logger.warn("User {} already has subscription {}", username, request.getSubscriptionId());
            throw new RuntimeException("You already have this subscription");
        }

        // Validate user exists
        try {
            Map<String, Object> userResponse = userServiceClient.getUserByUsername(username);
            if (userResponse == null || !(boolean) userResponse.get("success")) {
                throw new RuntimeException("User not found");
            }
        } catch (Exception e) {
            logger.error("Failed to validate user: {}", username, e);
            throw new RuntimeException("Failed to validate user");
        }

        // Get subscription details
        SubscriptionResponse subscription = getSubscriptionDetails(request.getSubscriptionId());

        // Create user subscription
        UserSubscription userSubscription = new UserSubscription(
                username,
                request.getSubscriptionId(),
                request.getStartDate(),
                request.getNextBillingDate(),
                subscription.getPrice(),
                subscription.getCurrency(),
                subscription.getBillingPeriod()
        );

        userSubscription.setNotes(request.getNotes());

        UserSubscription savedSubscription = userSubscriptionRepository.save(userSubscription);
        logger.info("Successfully created user subscription with ID: {}", savedSubscription.getId());

        return mapToResponse(savedSubscription, subscription);
    }

    public List<UserSubscriptionResponse> getUserSubscriptions(String username) {
        logger.info("Getting subscriptions for user: {}", username);

        List<UserSubscription> userSubscriptions = userSubscriptionRepository.findByUsernameAndIsActive(username, true);

        List<UserSubscriptionResponse> response = userSubscriptions.stream()
                .map(this::mapToResponseWithSubscriptionDetails)
                .collect(Collectors.toList());

        logger.debug("Retrieved {} user subscriptions for user: {}", response.size(), username);

        return response;
    }

    public UserSubscriptionResponse getUserSubscriptionById(String username, Long id) {
        logger.info("Getting user subscription by ID: {} for user: {}", id, username);

        UserSubscription userSubscription = userSubscriptionRepository.findByIdAndUsername(id, username)
                .orElseThrow(() -> {
                    logger.warn("User subscription not found: {} for user: {}", id, username);
                    return new RuntimeException("Subscription not found");
                });

        return mapToResponseWithSubscriptionDetails(userSubscription);
    }

    @Transactional
    public UserSubscriptionResponse updateUserSubscription(String username, Long id, UserSubscriptionRequest request) {
        logger.info("Updating user subscription: {} for user: {}", id, username);

        UserSubscription userSubscription = userSubscriptionRepository.findByIdAndUsername(id, username)
                .orElseThrow(() -> new RuntimeException("Subscription not found"));

        userSubscription.setStartDate(request.getStartDate());
        userSubscription.setNextBillingDate(request.getNextBillingDate());
        userSubscription.setNotes(request.getNotes());

        UserSubscription updatedSubscription = userSubscriptionRepository.save(userSubscription);
        logger.info("Successfully updated user subscription: {}", id);

        return mapToResponseWithSubscriptionDetails(updatedSubscription);
    }

    @Transactional
    public void deleteUserSubscription(String username, Long id) {
        logger.info("Deleting user subscription: {} for user: {}", id, username);

        UserSubscription userSubscription = userSubscriptionRepository.findByIdAndUsername(id, username)
                .orElseThrow(() -> new RuntimeException("Subscription not found"));

        userSubscription.setIsActive(false);
        userSubscriptionRepository.save(userSubscription);

        logger.info("Successfully deleted user subscription: {}", id);
    }

    public MonthlyCostSummary getMonthlyCostSummary(String username) {
        logger.info("Calculating monthly cost summary for user: {}", username);

        BigDecimal totalMonthlyCost = userSubscriptionRepository.calculateTotalMonthlyCostByUsername(username);
        int activeSubscriptionCount = userSubscriptionRepository.countActiveSubscriptionsByUsername(username);

        if (totalMonthlyCost == null) {
            totalMonthlyCost = BigDecimal.ZERO;
        }

        BigDecimal averageCostPerSubscription = activeSubscriptionCount > 0
                ? totalMonthlyCost.divide(BigDecimal.valueOf(activeSubscriptionCount), 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        MonthlyCostSummary summary = new MonthlyCostSummary(
                totalMonthlyCost, "AZN", activeSubscriptionCount, averageCostPerSubscription);

        logger.info("Monthly cost summary for {}: Total={}, Count={}", username, totalMonthlyCost, activeSubscriptionCount);
        return summary;
    }

    // Admin Functions
    public Page<UserSubscriptionResponse> getAllUserSubscriptions(Pageable pageable) {
        logger.info("Getting all user subscriptions with pagination");

        return userSubscriptionRepository.findAll(pageable)
                .map(this::mapToResponseWithSubscriptionDetails);
    }

    public Page<UserSubscriptionResponse> searchUserSubscriptions(String searchTerm, Pageable pageable) {
        logger.info("Searching user subscriptions with term: {}", searchTerm);

        return userSubscriptionRepository.searchUserSubscriptions(searchTerm, pageable)
                .map(this::mapToResponseWithSubscriptionDetails);
    }

    public Map<String, Object> getSubscriptionStatistics() {
        logger.info("Getting subscription statistics");

        Map<String, Object> statistics = new HashMap<>();

        try {
            Long activeSubscriptions = userSubscriptionRepository.countActiveSubscriptions();
            Long inactiveSubscriptions = userSubscriptionRepository.countInactiveSubscriptions();
            BigDecimal totalMonthlyRevenue = userSubscriptionRepository.getTotalMonthlyRevenue();
            Long activeUsers = userSubscriptionRepository.countActiveUsers();

            statistics.put("activeSubscriptions", activeSubscriptions != null ? activeSubscriptions : 0L);
            statistics.put("inactiveSubscriptions", inactiveSubscriptions != null ? inactiveSubscriptions : 0L);
            statistics.put("totalSubscriptions", (activeSubscriptions != null ? activeSubscriptions : 0L) +
                    (inactiveSubscriptions != null ? inactiveSubscriptions : 0L));
            statistics.put("totalMonthlyRevenue", totalMonthlyRevenue != null ? totalMonthlyRevenue : BigDecimal.ZERO);
            statistics.put("activeUsers", activeUsers != null ? activeUsers : 0L);
            statistics.put("averageRevenuePerUser",
                    activeUsers != null && activeUsers > 0 && totalMonthlyRevenue != null
                            ? totalMonthlyRevenue.divide(BigDecimal.valueOf(activeUsers), 2, RoundingMode.HALF_UP)
                            : BigDecimal.ZERO);

            logger.info("Generated subscription statistics: Active={}, Total Revenue={}",
                    activeSubscriptions, totalMonthlyRevenue);

        } catch (Exception e) {
            logger.error("Failed to generate statistics", e);
            throw new RuntimeException("Failed to generate statistics");
        }

        return statistics;
    }

    public void sendUpcomingBillingReminders() {
        logger.info("Sending upcoming billing reminders");

        LocalDate tomorrow = LocalDate.now().plusDays(1);
        LocalDate threeDaysLater = LocalDate.now().plusDays(3);

        List<UserSubscription> upcomingBillings = userSubscriptionRepository
                .findByNextBillingDateBetweenAndIsActive(tomorrow, threeDaysLater);

        int successCount = 0;
        for (UserSubscription userSubscription : upcomingBillings) {
            try {
                sendBillingReminder(userSubscription);
                successCount++;
            } catch (Exception e) {
                logger.error("Failed to send billing reminder for subscription: {}", userSubscription.getId(), e);
            }
        }

        logger.info("Sent {} out of {} billing reminders successfully", successCount, upcomingBillings.size());
    }

    private void sendBillingReminder(UserSubscription userSubscription) {
        try {
            // Get user details
            Map<String, Object> userResponse = userServiceClient.getUserByUsername(userSubscription.getUsername());
            if (userResponse == null || !(boolean) userResponse.get("success")) {
                logger.warn("User not found for billing reminder: {}", userSubscription.getUsername());
                return;
            }

            @SuppressWarnings("unchecked")
            Map<String, Object> user = (Map<String, Object>) userResponse.get("user");

            // Get subscription details
            SubscriptionResponse subscription = getSubscriptionDetails(userSubscription.getSubscriptionId());

            // Prepare email request
            Map<String, Object> emailRequest = new HashMap<>();
            emailRequest.put("to", user.get("email"));
            emailRequest.put("username", user.get("firstName") + " " + user.get("lastName"));
            emailRequest.put("subscriptionName", subscription.getName());
            emailRequest.put("nextBillingDate", userSubscription.getNextBillingDate().toString());
            emailRequest.put("amount", userSubscription.getMonthlyPrice().toString());
            emailRequest.put("currency", userSubscription.getCurrency());

            // Send email via Email Service
            emailServiceClient.sendSubscriptionReminder(emailRequest);

            logger.info("Sent billing reminder for user: {}, subscription: {}",
                    userSubscription.getUsername(), subscription.getName());

        } catch (Exception e) {
            logger.error("Failed to send billing reminder for subscription: {}", userSubscription.getId(), e);
            throw e;
        }
    }

    private SubscriptionResponse getSubscriptionDetails(Long subscriptionId) {
        try {
            Map<String, Object> response = subscriptionServiceClient.getSubscriptionById(subscriptionId);
            if (response == null || !(boolean) response.get("success")) {
                throw new RuntimeException("Subscription not found");
            }

            @SuppressWarnings("unchecked")
            Map<String, Object> subscriptionData = (Map<String, Object>) response.get("subscription");

            SubscriptionResponse subscription = new SubscriptionResponse();
            subscription.setId(((Number) subscriptionData.get("id")).longValue());
            subscription.setName((String) subscriptionData.get("name"));
            subscription.setDescription((String) subscriptionData.get("description"));
            subscription.setPrice(new BigDecimal(subscriptionData.get("price").toString()));
            subscription.setCurrency((String) subscriptionData.get("currency"));
            subscription.setCategory((String) subscriptionData.get("category"));
            subscription.setBillingPeriod((String) subscriptionData.get("billingPeriod"));
            subscription.setWebsiteUrl((String) subscriptionData.get("websiteUrl"));
            subscription.setLogoUrl((String) subscriptionData.get("logoUrl"));
            subscription.setIsActive((Boolean) subscriptionData.get("isActive"));

            return subscription;
        } catch (Exception e) {
            logger.error("Failed to get subscription details for ID: {}", subscriptionId, e);
            throw new RuntimeException("Failed to get subscription details");
        }
    }

    private UserSubscriptionResponse mapToResponse(UserSubscription userSubscription, SubscriptionResponse subscription) {
        UserSubscriptionResponse response = new UserSubscriptionResponse();
        response.setId(userSubscription.getId());
        response.setUsername(userSubscription.getUsername());
        response.setSubscriptionId(userSubscription.getSubscriptionId());
        response.setSubscriptionName(subscription.getName());
        response.setSubscriptionCategory(subscription.getCategory());
        response.setStartDate(userSubscription.getStartDate());
        response.setNextBillingDate(userSubscription.getNextBillingDate());
        response.setMonthlyPrice(userSubscription.getMonthlyPrice());
        response.setCurrency(userSubscription.getCurrency());
        response.setBillingPeriod(userSubscription.getBillingPeriod());
        response.setNotes(userSubscription.getNotes());
        response.setIsActive(userSubscription.getIsActive());
        response.setLogoUrl(subscription.getLogoUrl());
        response.setWebsiteUrl(subscription.getWebsiteUrl());
        return response;
    }

    private UserSubscriptionResponse mapToResponseWithSubscriptionDetails(UserSubscription userSubscription) {
        SubscriptionResponse subscription = getSubscriptionDetails(userSubscription.getSubscriptionId());
        return mapToResponse(userSubscription, subscription);
    }
}