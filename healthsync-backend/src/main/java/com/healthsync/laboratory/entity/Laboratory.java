package com.healthsync.laboratory.entity;

import com.healthsync.common.BaseAuditEntity;
import com.healthsync.district.entity.HealthCenter;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * Entity mapping clinical laboratories mapped to Health Centers.
 */
@Entity
@Table(
    name = "laboratories",
    indexes = {
        @Index(name = "idx_laboratories_center", columnList = "health_center_id")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Laboratory extends BaseAuditEntity {

    @NotBlank(message = "Laboratory name is required")
    @Column(name = "name", nullable = false, length = 150)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "health_center_id", nullable = false)
    @NotNull(message = "Health center mapping is required")
    private HealthCenter healthCenter;

    @NotBlank(message = "Laboratory status is required")
    @Column(name = "status", nullable = false, length = 20)
    private String status; // E.g., "ACTIVE", "INACTIVE"

    @NotBlank(message = "Working hours description is required")
    @Column(name = "working_hours", nullable = false, length = 50)
    private String workingHours; // E.g., "08:00 - 17:00"

    @NotNull(message = "Equipment count is required")
    @Column(name = "equipment_count", nullable = false)
    private Integer equipmentCount;
}
