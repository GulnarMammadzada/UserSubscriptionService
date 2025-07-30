package com.example.usersubscriptionservice.controller;

import com.example.usersubscriptionservice.dto.MonthlyCostSummary;
import com.example.usersubscriptionservice.dto.UserSubscriptionRequest;
import com.example.usersubscriptionservice.dto.UserSubscriptionResponse;
import com.example.usersubscriptionservice.service.UserSubscriptionService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user-subscriptions")
@CrossOrigin(origins = "http://localhost:8080")
public class UserSubscriptionController {

    private static final Logger logger = LoggerFactory.getLogger(UserSubscriptionController.class);

    @Autowired
    private UserSubscriptionService userSubscriptionService;

    private String getCurrentUsername() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    @PostMapping
    public ResponseEntity<?> createUserSubscription(@Valid @RequestBody UserSubscriptionRequest request) {
        String username = getCurrentUsername();
        try {
            logger.info("Create user subscription request received for user: {}", username);
            UserSubscriptionResponse subscription = userSubscriptionService.createUserSubscription(username, request);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Subscription added successfully");
            response.put("subscription", subscription);

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            logger.error("Failed to create user subscription for user: {}", username, e);

            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @GetMapping
    public ResponseEntity<?> getUserSubscriptions() {
        String username = getCurrentUsername();
        try {
            logger.info("Get user subscriptions request received for user: {}", username);
            List<UserSubscriptionResponse> subscriptions = userSubscriptionService.getUserSubscriptions(username);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("subscriptions", subscriptions);
            response.put("count", subscriptions.size());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Failed to get user subscriptions for user: {}", username, e);

            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserSubscriptionById(@PathVariable Long id) {
        String username = getCurrentUsername();
        try {
            logger.info("Get user subscription by ID request received: {} for user: {}", id, username);
            UserSubscriptionResponse subscription = userSubscriptionService.getUserSubscriptionById(username, id);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("subscription", subscription);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Failed to get user subscription by ID: {} for user: {}", id, username, e);

            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUserSubscription(@PathVariable Long id, @Valid @RequestBody UserSubscriptionRequest request) {
        String username = getCurrentUsername();
        try {
            logger.info("Update user subscription request received: {} for user: {}", id, username);
            UserSubscriptionResponse subscription = userSubscriptionService.updateUserSubscription(username, id, request);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Subscription updated successfully");
            response.put("subscription", subscription);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Failed to update user subscription: {} for user: {}", id, username, e);

            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUserSubscription(@PathVariable Long id) {
        String username = getCurrentUsername();
        try {
            logger.info("Delete user subscription request received: {} for user: {}", id, username);
            userSubscriptionService.deleteUserSubscription(username, id);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Subscription deleted successfully");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Failed to delete user subscription: {} for user: {}", id, username, e);

            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @GetMapping("/monthly-cost")
    public ResponseEntity<?> getMonthlyCostSummary() {
        String username = getCurrentUsername();
        try {
            logger.info("Get monthly cost summary request received for user: {}", username);
            MonthlyCostSummary summary = userSubscriptionService.getMonthlyCostSummary(username);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("summary", summary);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Failed to get monthly cost summary for user: {}", username, e);

            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/send-reminders")
    public ResponseEntity<?> sendBillingReminders() {
        try {
            logger.info("Send billing reminders request received");
            userSubscriptionService.sendUpcomingBillingReminders();

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Billing reminders sent successfully");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Failed to send billing reminders", e);

            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
