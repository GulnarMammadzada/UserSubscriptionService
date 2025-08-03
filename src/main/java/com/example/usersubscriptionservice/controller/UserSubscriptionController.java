package com.example.usersubscriptionservice.controller;

import com.example.usersubscriptionservice.dto.MonthlyCostSummary;
import com.example.usersubscriptionservice.dto.UserSubscriptionRequest;
import com.example.usersubscriptionservice.dto.UserSubscriptionResponse;
import com.example.usersubscriptionservice.service.UserSubscriptionService;
import com.example.usersubscriptionservice.util.UserContextUtil;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

    @Autowired
    private UserContextUtil userContextUtil;

    // User endpoints
    @PostMapping
    public ResponseEntity<?> createUserSubscription(@Valid @RequestBody UserSubscriptionRequest request) {
        try {
            String username = userContextUtil.getCurrentUsername();
            logger.info("Create user subscription request received for user: {}", username);

            UserSubscriptionResponse subscription = userSubscriptionService.createUserSubscription(username, request);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Subscription added successfully");
            response.put("subscription", subscription);

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            logger.error("Failed to create user subscription", e);

            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @GetMapping
    public ResponseEntity<?> getUserSubscriptions() {
        try {
            String username = userContextUtil.getCurrentUsername();
            logger.info("Get user subscriptions request received for user: {}", username);

            List<UserSubscriptionResponse> subscriptions = userSubscriptionService.getUserSubscriptions(username);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("subscriptions", subscriptions);
            response.put("count", subscriptions.size());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Failed to get user subscriptions", e);

            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserSubscriptionById(@PathVariable Long id) {
        try {
            String username = userContextUtil.getCurrentUsername();
            logger.info("Get user subscription by ID request received: {} for user: {}", id, username);

            UserSubscriptionResponse subscription = userSubscriptionService.getUserSubscriptionById(username, id);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("subscription", subscription);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Failed to get user subscription by ID: {}", id, e);

            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUserSubscription(@PathVariable Long id, @Valid @RequestBody UserSubscriptionRequest request) {
        try {
            String username = userContextUtil.getCurrentUsername();
            logger.info("Update user subscription request received: {} for user: {}", id, username);

            UserSubscriptionResponse subscription = userSubscriptionService.updateUserSubscription(username, id, request);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Subscription updated successfully");
            response.put("subscription", subscription);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Failed to update user subscription: {}", id, e);

            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUserSubscription(@PathVariable Long id) {
        try {
            String username = userContextUtil.getCurrentUsername();
            logger.info("Delete user subscription request received: {} for user: {}", id, username);

            userSubscriptionService.deleteUserSubscription(username, id);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Subscription deleted successfully");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Failed to delete user subscription: {}", id, e);

            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @GetMapping("/monthly-cost")
    public ResponseEntity<?> getMonthlyCostSummary() {
        try {
            String username = userContextUtil.getCurrentUsername();
            logger.info("Get monthly cost summary request received for user: {}", username);

            MonthlyCostSummary summary = userSubscriptionService.getMonthlyCostSummary(username);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("summary", summary);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Failed to get monthly cost summary", e);

            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // Admin endpoints
    @GetMapping("/admin/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAllUserSubscriptions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        try {
            Sort sort = sortDir.equalsIgnoreCase("desc") ?
                    Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
            Pageable pageable = PageRequest.of(page, size, sort);

            Page<UserSubscriptionResponse> subscriptions = userSubscriptionService.getAllUserSubscriptions(pageable);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("subscriptions", subscriptions.getContent());
            response.put("currentPage", subscriptions.getNumber());
            response.put("totalItems", subscriptions.getTotalElements());
            response.put("totalPages", subscriptions.getTotalPages());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Failed to get all user subscriptions", e);

            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/admin/user/{username}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getUserSubscriptionsByUsername(@PathVariable String username) {
        try {
            logger.info("Admin getting user subscriptions for: {}", username);

            List<UserSubscriptionResponse> subscriptions = userSubscriptionService.getUserSubscriptions(username);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("subscriptions", subscriptions);
            response.put("username", username);
            response.put("count", subscriptions.size());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Failed to get user subscriptions for: {}", username, e);

            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/admin/search")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> searchUserSubscriptions(
            @RequestParam String searchTerm,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<UserSubscriptionResponse> subscriptions = userSubscriptionService.searchUserSubscriptions(searchTerm, pageable);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("subscriptions", subscriptions.getContent());
            response.put("currentPage", subscriptions.getNumber());
            response.put("totalItems", subscriptions.getTotalElements());
            response.put("totalPages", subscriptions.getTotalPages());
            response.put("searchTerm", searchTerm);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Failed to search user subscriptions with term: {}", searchTerm, e);

            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/admin/send-reminders")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> sendBillingReminders() {
        try {
            logger.info("Admin send billing reminders request received");
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

    // Public endpoint for automated reminders
    @PostMapping("/send-reminders")
    public ResponseEntity<?> sendBillingRemindersPublic() {
        try {
            logger.info("Automated billing reminders request received");
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

    @GetMapping("/admin/statistics")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getSubscriptionStatistics() {
        try {
            logger.info("Admin get subscription statistics request received");
            Map<String, Object> statistics = userSubscriptionService.getSubscriptionStatistics();

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("statistics", statistics);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Failed to get subscription statistics", e);

            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}