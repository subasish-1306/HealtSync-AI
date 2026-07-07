package com.healthsync.ai.engine;

import com.healthsync.ai.dto.PredictionSummary.DoctorShortagePrediction;
import java.util.List;
import java.util.UUID;

/**
 * Strategy interface for analyzing doctor attendance and shortages.
 */
public interface DoctorAvailabilityAnalyzer {
    List<DoctorShortagePrediction> analyzeDoctorAvailability(UUID healthCenterId);
}
