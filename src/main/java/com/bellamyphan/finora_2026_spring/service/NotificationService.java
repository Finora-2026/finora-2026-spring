package com.bellamyphan.finora_2026_spring.service;

import com.bellamyphan.finora_2026_spring.config.AppEnvironmentInfo;
import com.bellamyphan.finora_2026_spring.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class NotificationService {

    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // Capture the exact moment the service starts
    private final LocalDateTime bootTime = LocalDateTime.now();

    private final EmailService emailService;
    private final UserService userService;
    private final AppEnvironmentInfo appEnvironmentInfo;

    public NotificationService(
            EmailService emailService,
            UserService userService,
            AppEnvironmentInfo appEnvironmentInfo
    ) {
        this.emailService = emailService;
        this.userService = userService;
        this.appEnvironmentInfo = appEnvironmentInfo;
    }

    /**
     * Sends a rich notification when the server boots up.
     * Async ensures this doesn't delay the actual application availability.
     */
    @Async
    public void sendStartupNotification() {
        logger.info("Preparing startup notification...");
        sendToAllAdmins(
                "✅ Finora Server Online",
                "The Spring Boot backend has successfully initialized."
        );
    }

    /**
     * Heartbeat notification every 24 hours.
     * Send periodic update every 24 hours, but skip first execution.
     */
//    @Scheduled(fixedRateString = "PT24H", initialDelayString = "PT24H")
    @Scheduled(fixedRateString = "PT20S", initialDelayString = "PT20S")
    @Async
    public void sendDailyStatusNotification() {
        logger.info("Preparing daily status heartbeat...");
        sendToAllAdmins(
                "☀️ Finora Daily Heartbeat",
                "System check complete. All services are running smoothly."
        );
    }

    private void sendToAllAdmins(String subject, String message) {
        // 1. Fetch admins from DB.
        List<User> admins = userService.findAllActiveAdmins();

        if (admins.isEmpty()) {
            logger.warn("No Admin users found in database. Skipping notification.");
            return;
        }

        String body = buildEmailBody(subject, message);

        // 2. Loop through and send to each admin
        for (User admin : admins) {
            try {
                emailService.sendEmail(admin.getEmail(), subject, body);
                logger.info("Notification sent to Admin: {}", admin.getEmail());
            } catch (Exception ex) {
                // Log the actual exception so you can see if it's a Mailgun Auth or Network issue
                logger.error("❌ Failed to send notification [{}]: {}", subject, ex.getMessage(), ex);
            }
        }
    }

    private String buildEmailBody(String subject, String message) {

        LocalDateTime now = LocalDateTime.now();
        Duration uptime = Duration.between(bootTime, now);

        String uptimeString = String.format("%d days, %d hours, %d minutes",
                uptime.toDays(), uptime.toHoursPart(), uptime.toMinutesPart());

        return "### " + subject + "\n\n" +
                "**Server Boot Time:** " + bootTime.format(DATE_FORMAT) + "\n" +
                "**Current Time:** " + now.format(DATE_FORMAT) + "\n" +
                "**Total Uptime:** " + uptimeString + "\n" +
                "**Status:** " + message + "\n\n" +
                "---\n" +
                "#### System Information\n" +
                appEnvironmentInfo.buildInfo();
    }
}
