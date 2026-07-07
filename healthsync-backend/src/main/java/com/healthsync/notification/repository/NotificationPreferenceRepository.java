package com.healthsync.notification.repository;

import com.healthsync.notification.entity.NotificationPreference;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for managing NotificationPreference persistence operations.
 */
@Repository
public interface NotificationPreferenceRepository extends JpaRepository<NotificationPreference, UUID> {
    List<NotificationPreference> findByUserId(UUID userId);
    Optional<NotificationPreference> findByUserIdAndNotificationTypeAndChannel(UUID userId, String notificationType, String channel);
}
