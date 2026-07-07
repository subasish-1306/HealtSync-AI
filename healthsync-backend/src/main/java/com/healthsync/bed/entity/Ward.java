package com.healthsync.bed.entity;

import com.healthsync.common.BaseAuditEntity;
import com.healthsync.district.entity.HealthCenter;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * Entity mapping inpatient wards inside Health Centers.
 */
@Entity
@Table(
    name = "wards",
    indexes = {
        @Index(name = "idx_wards_center", columnList = "health_center_id"),
        @Index(name = "idx_wards_code", columnList = "code")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ward extends BaseAuditEntity {

    @NotBlank(message = "Ward code is required")
    @Column(name = "code", nullable = false, unique = true, length = 50)
    private String code;

    @NotBlank(message = "Ward name is required")
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @NotBlank(message = "Department name is required")
    @Column(name = "department", nullable = false, length = 100)
    private String department;

    @NotNull(message = "Floor level is required")
    @Column(name = "floor", nullable = false)
    private Integer floor;

    @NotNull(message = "Ward capacity is required")
    @Min(value = 1, message = "Capacity must be at least 1")
    @Column(name = "capacity", nullable = false)
    private Integer capacity;

    @NotBlank(message = "Ward status is required")
    @Column(name = "status", nullable = false, length = 20)
    private String status; // E.g., "ACTIVE", "INACTIVE"

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "health_center_id", nullable = false)
    @NotNull(message = "Health center mapping is required")
    private HealthCenter healthCenter;
}
