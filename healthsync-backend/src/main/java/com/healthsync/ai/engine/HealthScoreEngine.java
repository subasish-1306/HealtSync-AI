package com.healthsync.ai.engine;

import com.healthsync.ai.dto.HealthScoreResponse;
import java.util.UUID;

/**
 * Strategy interface for calculating Health Center health scores.
 */
public interface HealthScoreEngine {
    HealthScoreResponse calculateHealthScore(UUID healthCenterId);
}
