package com.healthsync.ai.dto;

import java.util.UUID;

/**
 * Data Transfer Object modeling proposed medicine stock transfers.
 */
public record RedistributionTransfer(
    String medicineName,
    int quantity,
    UUID sourceCenterId,
    String sourceCenterName,
    UUID destinationCenterId,
    String destinationCenterName,
    String urgencyLevel
) {}
