package com.healthsync.analytics.entity;

import com.healthsync.common.BaseAuditEntity;
import com.healthsync.district.entity.HealthCenter;
import com.healthsync.inventory.entity.Medicine;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * Entity mapping AI-driven supply transfer proposals.
 */
@Entity
@Table(
    name = "redistribution_recommendations",
    indexes = {
        @Index(name = "idx_redist_recom_source", columnList = "source_center_id"),
        @Index(name = "idx_redist_recom_dest", columnList = "destination_center_id"),
        @Index(name = "idx_redist_recom_medicine", columnList = "medicine_id"),
        @Index(name = "idx_redist_recom_status", columnList = "status")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RedistributionRecommendation extends BaseAuditEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "source_center_id", nullable = false)
    @NotNull(message = "Source health center is required")
    private HealthCenter sourceCenter;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "destination_center_id", nullable = false)
    @NotNull(message = "Destination health center is required")
    private HealthCenter destinationCenter;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medicine_id", nullable = false)
    @NotNull(message = "Medicine is required")
    private Medicine medicine;

    @NotNull(message = "Redistribution quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Recommendation status is required")
    @Column(name = "status", nullable = false, length = 20)
    private RecommendationStatus status;

    @NotBlank(message = "Reason is required")
    @Column(name = "reason", nullable = false, length = 255)
    private String reason;
}
