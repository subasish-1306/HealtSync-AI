package com.healthsync.bed.entity;

import com.healthsync.common.BaseAuditEntity;
import com.healthsync.patient.entity.Patient;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.Instant;

/**
 * Entity logging inpatient stays transfers from one bed to another.
 */
@Entity
@Table(
    name = "bed_transfers",
    indexes = {
        @Index(name = "idx_bed_trans_patient", columnList = "patient_id"),
        @Index(name = "idx_bed_trans_time", columnList = "transfer_time")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BedTransfer extends BaseAuditEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    @NotNull(message = "Patient is required")
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_bed_id", nullable = false)
    @NotNull(message = "Origin bed is required")
    private Bed fromBed;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_bed_id", nullable = false)
    @NotNull(message = "Destination bed is required")
    private Bed toBed;

    @NotNull(message = "Transfer time is required")
    @Column(name = "transfer_time", nullable = false)
    private Instant transferTime;

    @NotBlank(message = "Transfer reason is required")
    @Column(name = "reason", nullable = false, length = 255)
    private String reason;
}
