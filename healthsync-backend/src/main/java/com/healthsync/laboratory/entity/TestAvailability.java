package com.healthsync.laboratory.entity;

import com.healthsync.common.BaseAuditEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * Entity mapping test availability and expected turnaround times within specific labs.
 */
@Entity
@Table(
    name = "test_availabilities",
    uniqueConstraints = {
        @UniqueConstraint(name = "uc_lab_test", columnNames = {"laboratory_id", "diagnostic_test_id"})
    },
    indexes = {
        @Index(name = "idx_test_avail_lab", columnList = "laboratory_id"),
        @Index(name = "idx_test_avail_test", columnList = "diagnostic_test_id")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TestAvailability extends BaseAuditEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "laboratory_id", nullable = false)
    @NotNull(message = "Laboratory mapping is required")
    private Laboratory laboratory;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "diagnostic_test_id", nullable = false)
    @NotNull(message = "Diagnostic test mapping is required")
    private DiagnosticTest diagnosticTest;

    @NotNull(message = "Availability flag is required")
    @Column(name = "is_available", nullable = false)
    private Boolean isAvailable;

    @NotNull(message = "Expected turnaround time in hours is required")
    @Min(value = 1, message = "Turnaround time must be at least 1 hour")
    @Column(name = "turnaround_time_hours", nullable = false)
    private Integer turnaroundTimeHours;
}
