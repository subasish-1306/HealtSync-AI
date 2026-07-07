package com.healthsync.patient.dto;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Data Transfer Object representing patient registry responses.
 */
public record PatientResponse(
    UUID id,
    String patientId,
    String medicalRecordNumber,
    String fullName,
    int age,
    String gender,
    LocalDate dateOfBirth,
    String bloodGroup,
    String mobileNumber,
    String address,
    String status,
    UUID districtId,
    String districtName,
    UUID healthCenterId,
    String healthCenterName,
    EmergencyContactDto emergencyContact
) {}
