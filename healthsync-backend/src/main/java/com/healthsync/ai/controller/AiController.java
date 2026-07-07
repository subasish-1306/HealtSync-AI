package com.healthsync.ai.controller;

import com.healthsync.ai.dto.*;
import com.healthsync.ai.service.AiInsightService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST controller exposing AI decision engine insights, score evaluations, and predictions.
 */
@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
@Tag(name = "AI Insights", description = "Endpoints for AI-driven operational forecasts and score evaluations")
public class AiController {

    private final AiInsightService aiInsightService;

    @GetMapping("/health-score/{healthCenterId}")
    @Operation(summary = "Calculate operational health score of a health center")
    public ResponseEntity<HealthScoreResponse> getHealthScore(@PathVariable UUID healthCenterId) {
        return ResponseEntity.ok(aiInsightService.getHealthScore(healthCenterId));
    }

    @GetMapping("/predictions")
    @Operation(summary = "Generate future load, bed occupancy, and drug stock prediction logs")
    public ResponseEntity<PredictionSummary> getPredictions(@RequestParam UUID healthCenterId) {
        return ResponseEntity.ok(aiInsightService.getPredictions(healthCenterId));
    }

    @GetMapping("/recommendations")
    @Operation(summary = "Generate strategic capacity optimization recommendations")
    public ResponseEntity<List<RecommendationDto>> getRecommendations(@RequestParam UUID healthCenterId) {
        return ResponseEntity.ok(aiInsightService.getRecommendations(healthCenterId));
    }

    @GetMapping("/alerts")
    @Operation(summary = "Retrieve active resource-critical alerts")
    public ResponseEntity<List<AiAlertDto>> getAlerts(@RequestParam UUID healthCenterId) {
        return ResponseEntity.ok(aiInsightService.getAlerts(healthCenterId));
    }

    @GetMapping("/resource-transfer")
    @Operation(summary = "Recommend supply redistribution transfers between centers")
    public ResponseEntity<List<RedistributionTransfer>> getResourceRedistributions(@RequestParam UUID healthCenterId) {
        return ResponseEntity.ok(aiInsightService.getResourceRedistributions(healthCenterId));
    }
}
