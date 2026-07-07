package com.healthsync.bed.entity;

import com.healthsync.common.BaseAuditEntity;
import com.healthsync.patient.entity.Patient;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.Instant;

/**
 * Entity mapping patient bed allocations and inpatient stays.
 */
@Entity
@Table(
    name = "bed_allocations",
    indexes = {
        @Index(name = "idx_bed_alloc_bed", columnList = "bed_id"),
        @Index(name = "idx_bed_alloc_patient", columnList = "patient_id"),
        @Index(name = "idx_bed_alloc_admission", columnList = "admission_date")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BedAllocation extends BaseAuditEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bed_id", nullable = false)
    @NotNull(message = "Bed is required")
    private Bed bed;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    @NotNull(message = "Patient is required")
    private Patient patient;

    @NotNull(message = "Admission date is required")
    @Column(name = "admission_date", nullable = false)
    private Instant admissionDate;

    @Column(name = "discharge_date")
    private Instant dischargeDate;

    @NotBlank(message = "Stay status is required")
    @Column(name = "status", nullable = false, length = 20)
    private String status; // E.g., "ADMITTED", "DISCHARGED"
}
