package com.healthsync.ai.engine;

import com.healthsync.ai.dto.RecommendationDto;
import java.util.List;
import java.util.UUID;

/**
 * Strategy interface for generating capacity recommendations.
 */
public interface RecommendationEngine {
    List<RecommendationDto> generateRecommendations(UUID healthCenterId);
}
