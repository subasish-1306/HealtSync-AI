package com.healthsync.notification.strategy;

import com.healthsync.notification.entity.Notification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Strategy implementation for dispatching SMS alerts.
 */
@Component
@Slf4j
public class SmsNotificationStrategy implements NotificationStrategy {

    @Override
    public void sendNotification(Notification notification) {
        log.info("Dispatching SMS alert: {}", notification.getMessage());
    }

    @Override
    public boolean supportsChannel(String channel) {
        return "SMS".equalsIgnoreCase(channel);
    }
}
