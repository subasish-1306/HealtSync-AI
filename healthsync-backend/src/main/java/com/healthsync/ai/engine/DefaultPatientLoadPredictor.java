package com.healthsync.ai.engine;

import com.healthsync.ai.dto.PredictionSummary.PatientLoadPrediction;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Default implementation of PatientLoadPredictor forecasting footfalls based on recent visit averages.
 */
@Component
public class DefaultPatientLoadPredictor implements PatientLoadPredictor {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<PatientLoadPrediction> predictPatientLoad(UUID healthCenterId) {
        long totalVisits = entityManager.createQuery(
                "SELECT COUNT(v) FROM PatientVisit v WHERE v.healthCenter.id = :centerId", Long.class)
                .setParameter("centerId", healthCenterId)
                .getSingleResult();

        int avgDailyVisits = totalVisits == 0 ? 15 : (int) Math.max(1, totalVisits / 30);

        List<PatientLoadPrediction> loadPredictions = new ArrayList<>();
        // Tomorrow prediction with flu-season adjustment
        int tomorrowVisits = (int) (avgDailyVisits * 1.15);
        loadPredictions.add(new PatientLoadPrediction("Tomorrow", tomorrowVisits, 0.90));

        // Next Week prediction
        int weekVisits = avgDailyVisits * 7;
        loadPredictions.add(new PatientLoadPrediction("Next Week", weekVisits, 0.85));

        // Next Month prediction
        int monthVisits = avgDailyVisits * 30;
        loadPredictions.add(new PatientLoadPrediction("Next Month", monthVisits, 0.75));

        return loadPredictions;
    }
}
