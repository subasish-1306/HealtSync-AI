package com.healthsync.analytics.entity;

import com.healthsync.common.BaseAuditEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Index;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Entity mapping machine learning prediction events.
 */
@Entity
@Table(
    name = "prediction_histories",
    indexes = {
        @Index(name = "idx_pred_hist_type", columnList = "prediction_type")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PredictionHistory extends BaseAuditEntity {

    @NotBlank(message = "Prediction type is required")
    @Column(name = "prediction_type", nullable = false, length = 100)
    private String predictionType; // E.g. "DISEASE_OUTBREAK", "INVENTORY_SHORTAGE"

    @NotBlank(message = "Input parameters are required")
    @Column(name = "input_parameters", nullable = false, columnDefinition = "TEXT")
    private String inputParameters;

    @NotBlank(message = "Predicted value is required")
    @Column(name = "predicted_value", nullable = false, length = 255)
    private String predictedValue;

    @NotNull(message = "Confidence score is required")
    @DecimalMin(value = "0.0", message = "Confidence score must be at least 0.0")
    @DecimalMax(value = "1.0", message = "Confidence score cannot exceed 1.0")
    @Column(name = "confidence_score", nullable = false, precision = 3, scale = 2)
    private BigDecimal confidenceScore;

    @NotNull(message = "Generation timestamp is required")
    @Column(name = "generated_at", nullable = false)
    private Instant generatedAt;
}
