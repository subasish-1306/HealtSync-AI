package com.healthsync.notification.strategy;

import com.healthsync.notification.entity.Notification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Strategy implementation for dispatching Email notifications.
 */
@Component
@Slf4j
public class EmailNotificationStrategy implements NotificationStrategy {

    @Override
    public void sendNotification(Notification notification) {
        if (notification.getUser() != null && notification.getUser().getEmail() != null) {
            log.info("Dispatching EMAIL notification to {}: [Subject: HealthSync Alert] - {}", 
                    notification.getUser().getEmail(), 
                    notification.getMessage());
        } else {
            log.info("Dispatching EMAIL notification to broadcast group: {}", notification.getMessage());
        }
    }

    @Override
    public boolean supportsChannel(String channel) {
        return "EMAIL".equalsIgnoreCase(channel);
    }
}
