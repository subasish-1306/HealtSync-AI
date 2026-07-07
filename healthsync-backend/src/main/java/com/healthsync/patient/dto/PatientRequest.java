package com.healthsync.patient.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Data Transfer Object for creating or updating a Patient.
 */
public record PatientRequest(
    UUID userId,

    @NotBlank(message = "Medical Record Number (MRN) is required")
    String medicalRecordNumber,

    @NotBlank(message = "Full name is required")
    String fullName,

    @NotBlank(message = "Gender is required")
    String gender,

    @NotNull(message = "Date of birth is required")
    LocalDate dateOfBirth,

    String bloodGroup,

    @NotBlank(message = "Mobile number is required")
    @Pattern(regexp = "^\\+?[0-9]{7,15}$", message = "Mobile number must be valid (7-15 digits, optional +)")
    String mobileNumber,

    @NotBlank(message = "Address is required")
    String address,

    @NotBlank(message = "Status is required")
    String status,

    @NotNull(message = "District ID is required")
    UUID districtId,

    @NotNull(message = "Health Center ID is required")
    UUID healthCenterId,

    @Valid
    @NotNull(message = "Emergency contact is required")
    EmergencyContactDto emergencyContact
) {}
