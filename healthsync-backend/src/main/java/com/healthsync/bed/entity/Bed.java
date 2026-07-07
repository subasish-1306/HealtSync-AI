package com.healthsync.bed.entity;

import com.healthsync.common.BaseAuditEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * Entity mapping specific beds within a ward.
 */
@Entity
@Table(
    name = "beds",
    uniqueConstraints = {
        @UniqueConstraint(name = "uc_ward_bed", columnNames = {"ward_id", "bed_number"})
    },
    indexes = {
        @Index(name = "idx_beds_ward", columnList = "ward_id"),
        @Index(name = "idx_beds_avail_status", columnList = "availability_status"),
        @Index(name = "idx_beds_clean_status", columnList = "cleaning_status")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Bed extends BaseAuditEntity {

    @NotBlank(message = "Bed number is required")
    @Column(name = "bed_number", nullable = false, length = 50)
    private String bedNumber;

    @NotBlank(message = "Bed type is required")
    @Column(name = "bed_type", nullable = false, length = 50)
    private String bedType; // E.g., "GENERAL", "ICU", "SEMI_PRIVATE"

    @NotBlank(message = "Availability status is required")
    @Column(name = "availability_status", nullable = false, length = 20)
    private String availabilityStatus; // E.g., "AVAILABLE", "OCCUPIED"

    @NotBlank(message = "Cleaning status is required")
    @Column(name = "cleaning_status", nullable = false, length = 20)
    private String cleaningStatus; // E.g., "CLEAN", "DIRTY", "BEING_CLEANED"

    @NotBlank(message = "Maintenance status is required")
    @Column(name = "maintenance_status", nullable = false, length = 20)
    private String maintenanceStatus; // E.g., "FUNCTIONAL", "UNDER_REPAIR"

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ward_id", nullable = false)
    @NotNull(message = "Ward mapping is required")
    private Ward ward;
}
