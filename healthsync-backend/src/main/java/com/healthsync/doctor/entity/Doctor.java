package com.healthsync.doctor.entity;

import com.healthsync.auth.entity.User;
import com.healthsync.common.BaseAuditEntity;
import com.healthsync.district.entity.HealthCenter;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * Entity mapping doctor credentials, health centers, and assigned shifts.
 */
@Entity
@Table(
    name = "doctors",
    indexes = {
        @Index(name = "idx_doctors_license", columnList = "license_number"),
        @Index(name = "idx_doctors_center", columnList = "health_center_id"),
        @Index(name = "idx_doctors_user", columnList = "user_id")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Doctor extends BaseAuditEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    @NotNull(message = "User account association is required")
    private User user;

    @NotBlank(message = "License number is required")
    @Column(name = "license_number", nullable = false, unique = true, length = 50)
    private String licenseNumber;

    @NotBlank(message = "Specialization is required")
    @Column(name = "specialization", nullable = false, length = 100)
    private String specialization;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "health_center_id", nullable = false)
    @NotNull(message = "Health center mapping is required")
    private HealthCenter healthCenter;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "default_shift_id", nullable = false)
    @NotNull(message = "Default shift is required")
    private Shift defaultShift;
}
