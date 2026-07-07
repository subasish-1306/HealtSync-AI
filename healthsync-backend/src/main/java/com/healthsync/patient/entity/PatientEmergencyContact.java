package com.healthsync.patient.entity;

import com.healthsync.common.BaseAuditEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

/**
 * Entity mapping patient emergency contact details.
 */
@Entity
@Table(name = "patient_emergency_contacts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PatientEmergencyContact extends BaseAuditEntity {

    @NotBlank(message = "Contact name is required")
    @Column(name = "contact_name", nullable = false, length = 150)
    private String contactName;

    @NotBlank(message = "Relationship is required")
    @Column(name = "relationship", nullable = false, length = 50)
    private String relationship;

    @NotBlank(message = "Phone number is required")
    @Column(name = "phone_number", nullable = false, length = 20)
    private String phoneNumber;
}
