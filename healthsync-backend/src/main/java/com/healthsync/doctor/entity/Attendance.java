package com.healthsync.doctor.entity;

import com.healthsync.common.BaseAuditEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Entity mapping doctor daily attendance records.
 */
@Entity
@Table(
    name = "attendances",
    uniqueConstraints = {
        @UniqueConstraint(name = "uc_doctor_date", columnNames = {"doctor_id", "attendance_date"})
    },
    indexes = {
        @Index(name = "idx_attendances_doctor", columnList = "doctor_id"),
        @Index(name = "idx_attendances_date", columnList = "attendance_date"),
        @Index(name = "idx_attendances_status", columnList = "status")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Attendance extends BaseAuditEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", nullable = false)
    @NotNull(message = "Doctor is required")
    private Doctor doctor;

    @NotNull(message = "Attendance date is required")
    @Column(name = "attendance_date", nullable = false)
    private LocalDate date;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Attendance status is required")
    @Column(name = "status", nullable = false, length = 20)
    private AttendanceStatus status;

    @Column(name = "check_in_time")
    private LocalTime checkInTime;

    @Column(name = "check_out_time")
    private LocalTime checkOutTime;
}
