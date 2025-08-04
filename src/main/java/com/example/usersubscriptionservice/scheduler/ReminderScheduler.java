package com.example.usersubscriptionservice.scheduler;

import com.example.usersubscriptionservice.service.UserSubscriptionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
public class ReminderScheduler {

    private static final Logger logger = LoggerFactory.getLogger(ReminderScheduler.class);

    @Autowired
    private UserSubscriptionService userSubscriptionService;

    // Run every day at 9:00 AM
    @Scheduled(cron = "0 0 9 * * ?")
    public void sendDailyBillingReminders() {
        logger.info("Starting daily billing reminders job");
        try {
            userSubscriptionService.sendUpcomingBillingReminders();
            logger.info("Daily billing reminders job completed successfully");
        } catch (Exception e) {
            logger.error("Daily billing reminders job failed", e);
        }
    }

    // Run every hour to cleanup cache if needed
    @Scheduled(fixedRate = 3600000) // 1 hour
    public void performHousekeeping() {
        logger.debug("Performing housekeeping tasks");
    }
}