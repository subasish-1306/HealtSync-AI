package com.healthsync.ai.engine;

import com.healthsync.ai.dto.PredictionSummary.MedicineStockPrediction;
import java.util.List;
import java.util.UUID;

/**
 * Strategy interface for predicting medicine stock-outs.
 */
public interface MedicinePredictionEngine {
    List<MedicineStockPrediction> predictMedicineStockOuts(UUID healthCenterId);
}
