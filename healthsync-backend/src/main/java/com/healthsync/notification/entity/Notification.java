package com.healthsync.notification.entity;

import com.healthsync.auth.entity.User;
import com.healthsync.common.BaseAuditEntity;
import com.healthsync.district.entity.HealthCenter;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * Entity mapping system alerts and user notifications logs.
 */
@Entity
@Table(
    name = "notifications",
    indexes = {
        @Index(name = "idx_notifications_user", columnList = "user_id"),
        @Index(name = "idx_notifications_status", columnList = "status")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification extends BaseAuditEntity {

    @NotBlank(message = "Notification message is required")
    @Column(name = "message", nullable = false)
    private String message;

    @NotBlank(message = "Notification type is required")
    @Column(name = "type", nullable = false, length = 50)
    private String type; // E.g., "MEDICINE_LOW_STOCK", "EMERGENCY_ALERT"

    @NotBlank(message = "Notification status is required")
    @Column(name = "status", nullable = false, length = 20)
    private String status; // E.g., "UNREAD", "READ"

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "health_center_id")
    private HealthCenter healthCenter;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
}
