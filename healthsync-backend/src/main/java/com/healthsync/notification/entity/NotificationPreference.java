package com.healthsync.notification.entity;

import com.healthsync.auth.entity.User;
import com.healthsync.common.BaseAuditEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * Entity mapping user specific channel preferences for specific alert types.
 */
@Entity
@Table(
    name = "notification_preferences",
    uniqueConstraints = {
        @UniqueConstraint(name = "uc_user_pref", columnNames = {"user_id", "notification_type", "channel"})
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationPreference extends BaseAuditEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @NotNull(message = "User mapping is required")
    private User user;

    @NotBlank(message = "Notification type is required")
    @Column(name = "notification_type", nullable = false, length = 50)
    private String notificationType; // E.g., "MEDICINE_LOW_STOCK", "EMERGENCY_ALERT"

    @NotBlank(message = "Channel is required")
    @Column(name = "channel", nullable = false, length = 20)
    private String channel; // E.g., "IN_APP", "EMAIL", "SMS", "WEBHOOK"

    @NotNull(message = "Enabled flag is required")
    @Column(name = "enabled", nullable = false)
    private Boolean enabled;
}
