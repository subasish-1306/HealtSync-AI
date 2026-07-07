package com.healthsync.laboratory.entity;

import com.healthsync.common.BaseAuditEntity;
import com.healthsync.doctor.entity.Doctor;
import com.healthsync.patient.entity.Patient;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

/**
 * Entity mapping patient diagnostic test bookings.
 */
@Entity
@Table(
    name = "test_bookings",
    indexes = {
        @Index(name = "idx_test_bookings_patient", columnList = "patient_id"),
        @Index(name = "idx_test_bookings_date", columnList = "booking_date")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TestBooking extends BaseAuditEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    @NotNull(message = "Patient is required")
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", nullable = false)
    @NotNull(message = "Referral doctor is required")
    private Doctor doctor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "diagnostic_test_id", nullable = false)
    @NotNull(message = "Diagnostic test is required")
    private DiagnosticTest diagnosticTest;

    @NotNull(message = "Booking date is required")
    @Column(name = "booking_date", nullable = false)
    private LocalDate bookingDate;

    @NotBlank(message = "Booking status is required")
    @Column(name = "status", nullable = false, length = 20)
    private String status; // E.g., "PENDING", "COMPLETED", "CANCELLED"

    @NotBlank(message = "Booking priority is required")
    @Column(name = "priority", nullable = false, length = 20)
    private String priority; // E.g., "ROUTINE", "URGENT", "EMERGENCY"
}
