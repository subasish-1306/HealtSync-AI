package com.healthsync.ai.engine;

import com.healthsync.ai.dto.PredictionSummary.BedOccupancyPrediction;
import java.util.UUID;

/**
 * Strategy interface for predicting inpatient bed occupancy.
 */
public interface BedOccupancyPredictor {
    BedOccupancyPrediction predictBedOccupancy(UUID healthCenterId);
}
