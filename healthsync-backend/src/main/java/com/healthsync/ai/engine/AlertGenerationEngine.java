package com.healthsync.ai.engine;

import com.healthsync.ai.dto.AiAlertDto;
import java.util.List;
import java.util.UUID;

/**
 * Strategy interface for compiling real-time system warnings and alerts.
 */
public interface AlertGenerationEngine {
    List<AiAlertDto> generateAlerts(UUID healthCenterId);
}
