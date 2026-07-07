package com.healthsync.report.entity;

import com.healthsync.common.BaseAuditEntity;
import com.healthsync.district.entity.HealthCenter;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.Instant;

/**
 * Entity mapping operational alerts triggered by business rules (like low stock).
 */
@Entity
@Table(
    name = "alerts",
    indexes = {
        @Index(name = "idx_alerts_center", columnList = "health_center_id"),
        @Index(name = "idx_alerts_severity", columnList = "severity"),
        @Index(name = "idx_alerts_acknowledged", columnList = "acknowledged")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Alert extends BaseAuditEntity {

    @NotBlank(message = "Alert title is required")
    @Column(name = "title", nullable = false, length = 150)
    private String title;

    @NotBlank(message = "Alert description is required")
    @Column(name = "description", nullable = false, length = 255)
    private String description;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Severity is required")
    @Column(name = "severity", nullable = false, length = 20)
    private AlertSeverity severity;

    @NotNull(message = "Acknowledge flag is required")
    @Column(name = "acknowledged", nullable = false)
    private Boolean acknowledged;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "health_center_id", nullable = false)
    @NotNull(message = "Health center is required")
    private HealthCenter healthCenter;

    @NotNull(message = "Trigger timestamp is required")
    @Column(name = "triggered_at", nullable = false)
    private Instant triggeredAt;
}
