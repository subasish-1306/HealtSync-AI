package com.healthsync.notification.strategy;

import com.healthsync.notification.entity.Notification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Strategy implementation for dispatching Webhook alert payloads.
 */
@Component
@Slf4j
public class WebhookNotificationStrategy implements NotificationStrategy {

    @Override
    public void sendNotification(Notification notification) {
        log.info("Dispatching WEBHOOK payload to registered subscribers: {}", notification.getMessage());
    }

    @Override
    public boolean supportsChannel(String channel) {
        return "WEBHOOK".equalsIgnoreCase(channel);
    }
}
