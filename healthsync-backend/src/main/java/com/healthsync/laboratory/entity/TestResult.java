package com.healthsync.laboratory.entity;

import com.healthsync.common.BaseAuditEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.Instant;

/**
 * Entity mapping test completion results.
 */
@Entity
@Table(name = "test_results")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TestResult extends BaseAuditEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", nullable = false, unique = true)
    @NotNull(message = "Associated booking is required")
    private TestBooking booking;

    @NotBlank(message = "Result metrics values description is required")
    @Column(name = "result", nullable = false, length = 255)
    private String result;

    @NotNull(message = "Completed date is required")
    @Column(name = "completed_date", nullable = false)
    private Instant completedDate;

    @Column(name = "remarks", length = 255)
    private String remarks;
}
