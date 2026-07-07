package com.healthsync.ai.engine;

import com.healthsync.ai.dto.PredictionSummary.PatientLoadPrediction;
import java.util.List;
import java.util.UUID;

/**
 * Strategy interface for predicting patient visit loads.
 */
public interface PatientLoadPredictor {
    List<PatientLoadPrediction> predictPatientLoad(UUID healthCenterId);
}
