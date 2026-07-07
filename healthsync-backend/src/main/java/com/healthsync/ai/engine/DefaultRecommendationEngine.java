package com.healthsync.ai.engine;

import com.healthsync.ai.dto.AiAlertDto;
import com.healthsync.ai.dto.RecommendationDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Default implementation of RecommendationEngine proposing resource reallocations.
 */
@Component
@RequiredArgsConstructor
public class DefaultRecommendationEngine implements RecommendationEngine {

    private final AlertGenerationEngine alertEngine;

    @Override
    public List<RecommendationDto> generateRecommendations(UUID healthCenterId) {
        List<AiAlertDto> alerts = alertEngine.generateAlerts(healthCenterId);
        List<RecommendationDto> recommendations = new ArrayList<>();

        recommendations.add(new RecommendationDto(
                "GENERAL",
                "Perform scheduled bi-weekly medicine inventory verification audits.",
                "LOW",
                "Ensure stock consistency between physical shelves and database records."
        ));

        for (AiAlertDto alert : alerts) {
            switch (alert.alertType()) {
                case "MEDICINE_SHORTAGE" -> {
                    recommendations.add(new RecommendationDto(
                            "MEDICINE",
                            "Initiate inter-PHC medicine redistribution or place an emergency order.",
                            "HIGH",
                            "Quantity of critical medicine types fell below the safety reorder levels."
                    ));
                    recommendations.add(new RecommendationDto(
                            "MEDICINE",
                            "Assess generic drug substitutes for the depleted inventory categories.",
                            "MEDIUM",
                            "Maintain primary clinical treatment capabilities during outages."
                    ));
                }
                case "DOCTOR_SHORTAGE" -> recommendations.add(new RecommendationDto(
                        "DOCTOR",
                        "Request doctor shift coverage support from the nearest Community Health Center (CHC) hub.",
                        "CRITICAL",
                        "Expected daily consultation volume exceeds current doctor availability threshold."
                ));
                case "BED_FULL" -> recommendations.add(new RecommendationDto(
                        "BED",
                        "Initiate non-critical patient discharges and trigger bed transfer options.",
                        "CRITICAL",
                        "Occupied ward beds exceed 90% of capacity limits."
                ));
                case "HIGH_PATIENT_LOAD" -> recommendations.add(new RecommendationDto(
                        "GENERAL",
                        "Deploy additional registration counters and increase outpatient nurse shifts.",
                        "MEDIUM",
                        "Prepare for high visitor volumes forecast for tomorrow."
                ));
                case "CRITICAL_HEALTH_CENTER" -> recommendations.add(new RecommendationDto(
                        "GENERAL",
                        "Flag health center for operational supervision and resource redistribution review.",
                        "CRITICAL",
                        "Aggregated resource indicators fell below minimum thresholds."
                ));
            }
        }

        return recommendations;
    }
}
