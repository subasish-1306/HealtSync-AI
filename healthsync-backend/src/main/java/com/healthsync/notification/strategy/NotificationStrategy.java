package com.healthsync.notification.strategy;

import com.healthsync.notification.entity.Notification;

/**
 * Strategy interface for sending notifications across different channels.
 */
public interface NotificationStrategy {
    void sendNotification(Notification notification);
    boolean supportsChannel(String channel);
}
