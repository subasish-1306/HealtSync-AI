package com.healthsync.notification.strategy;

import com.healthsync.notification.entity.Notification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Strategy implementation for dispatching In-App notifications.
 */
@Component
@Slf4j
public class InAppNotificationStrategy implements NotificationStrategy {

    @Override
    public void sendNotification(Notification notification) {
        log.info("Dispatching IN_APP notification for user {}: {}", 
                notification.getUser() != null ? notification.getUser().getUsername() : "All", 
                notification.getMessage());
    }

    @Override
    public boolean supportsChannel(String channel) {
        return "IN_APP".equalsIgnoreCase(channel);
    }
}
