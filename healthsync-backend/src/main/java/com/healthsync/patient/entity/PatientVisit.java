package com.healthsync.patient.entity;

import com.healthsync.common.BaseAuditEntity;
import com.healthsync.district.entity.HealthCenter;
import com.healthsync.doctor.entity.Doctor;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.Instant;

/**
 * Entity mapping patient clinical consultations.
 */
@Entity
@Table(
    name = "patient_visits",
    indexes = {
        @Index(name = "idx_patient_visits_patient", columnList = "patient_id"),
        @Index(name = "idx_patient_visits_center", columnList = "health_center_id"),
        @Index(name = "idx_patient_visits_doctor", columnList = "doctor_id"),
        @Index(name = "idx_patient_visits_disease", columnList = "disease_category_id")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PatientVisit extends BaseAuditEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    @NotNull(message = "Patient is required")
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "health_center_id", nullable = false)
    @NotNull(message = "Health center is required")
    private HealthCenter healthCenter;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id")
    private Doctor doctor; // Reference only, nullable

    @NotNull(message = "Visit date is required")
    @Column(name = "visit_date", nullable = false)
    private Instant visitDate;

    @NotNull(message = "Visit time is required")
    @Column(name = "visit_time", nullable = false)
    private java.time.LocalTime visitTime;

    @NotBlank(message = "Department is required")
    @Column(name = "department", nullable = false, length = 100)
    private String department;

    @NotBlank(message = "Visit type is required")
    @Column(name = "visit_type", nullable = false, length = 50)
    private String visitType; // E.g., FIRST_VISIT, FOLLOW_UP, EMERGENCY

    @NotBlank(message = "Symptoms description is required")
    @Column(name = "symptoms", nullable = false, columnDefinition = "TEXT")
    private String symptoms;

    @NotBlank(message = "Diagnosis details are required")
    @Column(name = "diagnosis", nullable = false, columnDefinition = "TEXT")
    private String diagnosis;

    @Column(name = "prescription_reference", length = 100)
    private String prescriptionReference;

    @NotBlank(message = "Status is required")
    @Column(name = "status", nullable = false, length = 20)
    private String status; // E.g. "SCHEDULED", "COMPLETED", "CANCELLED"

    @NotNull(message = "Visit duration is required")
    @Column(name = "visit_duration", nullable = false)
    private Integer visitDuration; // in minutes

    @NotNull(message = "Waiting time is required")
    @Column(name = "waiting_time", nullable = false)
    private Integer waitingTime; // in minutes

    @NotBlank(message = "Age group is required")
    @Column(name = "age_group", nullable = false, length = 20)
    private String ageGroup; // Calculated E.g. "CHILD", "ADULT", "SENIOR"

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "disease_category_id", nullable = false)
    @NotNull(message = "Disease category is required")
    private DiseaseCategory diseaseCategory;
}
