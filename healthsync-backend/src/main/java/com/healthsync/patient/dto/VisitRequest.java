package com.healthsync.patient.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.time.LocalTime;
import java.util.UUID;

/**
 * Data Transfer Object for creating or updating a Patient Visit.
 */
public record VisitRequest(
    @NotNull(message = "Patient ID is required")
    UUID patientId,

    @NotNull(message = "Health Center ID is required")
    UUID healthCenterId,

    UUID doctorId,

    @NotNull(message = "Visit date is required")
    Instant visitDate,

    @NotNull(message = "Visit time is required")
    LocalTime visitTime,

    @NotBlank(message = "Department is required")
    String department,

    @NotBlank(message = "Visit type is required")
    String visitType,

    @NotBlank(message = "Symptoms description is required")
    String symptoms,

    @NotBlank(message = "Diagnosis details are required")
    String diagnosis,

    String prescriptionReference,

    @NotBlank(message = "Status is required")
    String status,

    @NotNull(message = "Visit duration is required")
    @Min(value = 0, message = "Visit duration cannot be negative")
    Integer visitDuration,

    @NotNull(message = "Waiting time is required")
    @Min(value = 0, message = "Waiting time cannot be negative")
    Integer waitingTime,

    @NotNull(message = "Disease category ID is required")
    UUID diseaseCategoryId
) {}
