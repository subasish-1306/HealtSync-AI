package com.healthsync.patient.entity;

import com.healthsync.auth.entity.User;
import com.healthsync.common.BaseAuditEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

/**
 * Entity mapping patient demographic and registry profiles.
 */
@Entity
@Table(
    name = "patients",
    indexes = {
        @Index(name = "idx_patients_mrn", columnList = "medical_record_number"),
        @Index(name = "idx_patients_code", columnList = "patient_id"),
        @Index(name = "idx_patients_user", columnList = "user_id"),
        @Index(name = "idx_patients_mobile", columnList = "mobile_number")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Patient extends BaseAuditEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", unique = true)
    private User user; // Nullable if patient has no login credentials

    @Column(name = "patient_id", nullable = false, unique = true, length = 50)
    private String patientId;

    @NotBlank(message = "Medical Record Number (MRN) is required")
    @Column(name = "medical_record_number", nullable = false, unique = true, length = 50)
    private String medicalRecordNumber;

    @NotBlank(message = "Full name is required")
    @Column(name = "full_name", nullable = false, length = 150)
    private String fullName;

    @NotNull(message = "Age is required")
    @Column(name = "age", nullable = false)
    private Integer age;

    @NotBlank(message = "Gender is required")
    @Column(name = "gender", nullable = false, length = 20)
    private String gender;

    @NotNull(message = "Date of birth is required")
    @Column(name = "date_of_birth", nullable = false)
    private LocalDate dateOfBirth;

    @Column(name = "blood_group", length = 20)
    private String bloodGroup;

    @NotBlank(message = "Mobile number is required")
    @Column(name = "mobile_number", nullable = false, unique = true, length = 20)
    private String mobileNumber;

    @NotBlank(message = "Address is required")
    @Column(name = "address", nullable = false, length = 255)
    private String address;

    @NotBlank(message = "Status is required")
    @Column(name = "status", nullable = false, length = 20)
    private String status; // E.g. "ACTIVE", "INACTIVE"

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "district_id", nullable = false)
    @NotNull(message = "District mapping is required")
    private com.healthsync.district.entity.District district;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "health_center_id", nullable = false)
    @NotNull(message = "Health center mapping is required")
    private com.healthsync.district.entity.HealthCenter healthCenter;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "emergency_contact_id")
    private PatientEmergencyContact emergencyContact;
}
