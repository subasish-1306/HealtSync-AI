package com.healthsync.district.entity;

import com.healthsync.common.BaseAuditEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * Entity mapping individual health center profiles (PHCs and CHCs).
 */
@Entity
@Table(
    name = "health_centers",
    indexes = {
        @Index(name = "idx_health_centers_district", columnList = "district_id"),
        @Index(name = "idx_health_centers_type", columnList = "type")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HealthCenter extends BaseAuditEntity {

    @NotBlank(message = "Health center name is required")
    @Column(name = "name", nullable = false, length = 150)
    private String name;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Health center type is required")
    @Column(name = "type", nullable = false, length = 20)
    private HealthCenterType type;

    @NotBlank(message = "Address is required")
    @Column(name = "address", nullable = false, length = 255)
    private String address;

    @NotNull(message = "Bed capacity is required")
    @Column(name = "capacity", nullable = false)
    private Integer capacity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "district_id", nullable = false)
    @NotNull(message = "District is required")
    private District district;
}
