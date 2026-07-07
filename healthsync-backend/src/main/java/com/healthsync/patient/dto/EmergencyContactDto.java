package com.healthsync.patient.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * Data Transfer Object for Emergency Contact information.
 */
public record EmergencyContactDto(
    @NotBlank(message = "Emergency contact name is required")
    String contactName,

    @NotBlank(message = "Emergency contact relationship is required")
    String relationship,

    @NotBlank(message = "Emergency contact phone number is required")
    @Pattern(regexp = "^\\+?[0-9]{7,15}$", message = "Phone number must be valid (7-15 digits, optional +)")
    String phoneNumber
) {}
