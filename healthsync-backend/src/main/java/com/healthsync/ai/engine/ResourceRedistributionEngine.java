package com.healthsync.ai.engine;

import com.healthsync.ai.dto.RedistributionTransfer;
import java.util.List;
import java.util.UUID;

/**
 * Strategy interface for recommending medicine redistribution.
 */
public interface ResourceRedistributionEngine {
    List<RedistributionTransfer> recommendRedistributions(UUID healthCenterId);
}
