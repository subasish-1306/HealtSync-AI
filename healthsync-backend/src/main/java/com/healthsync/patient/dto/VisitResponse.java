package com.healthsync.patient.dto;

import java.time.Instant;
import java.time.LocalTime;
import java.util.UUID;

/**
 * Data Transfer Object representing patient visit details.
 */
public record VisitResponse(
    UUID id,
    UUID patientId,
    String patientCode,
    String patientName,
    UUID healthCenterId,
    String healthCenterName,
    UUID doctorId,
    Instant visitDate,
    LocalTime visitTime,
    String department,
    String visitType,
    String symptoms,
    String diagnosis,
    String prescriptionReference,
    String status,
    int visitDuration,
    int waitingTime,
    String ageGroup,
    UUID diseaseCategoryId,
    String diseaseCategoryCode,
    String diseaseCategoryName
) {}
