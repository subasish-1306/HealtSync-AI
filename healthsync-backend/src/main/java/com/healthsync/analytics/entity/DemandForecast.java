package com.healthsync.analytics.entity;

import com.healthsync.common.BaseAuditEntity;
import com.healthsync.district.entity.HealthCenter;
import com.healthsync.inventory.entity.Medicine;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Entity mapping predictive stock level demands across health centers.
 */
@Entity
@Table(
    name = "demand_forecasts",
    indexes = {
        @Index(name = "idx_demand_forecast_center", columnList = "health_center_id"),
        @Index(name = "idx_demand_forecast_medicine", columnList = "medicine_id"),
        @Index(name = "idx_demand_forecast_date", columnList = "forecast_date")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DemandForecast extends BaseAuditEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "health_center_id", nullable = false)
    @NotNull(message = "Health center is required")
    private HealthCenter healthCenter;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medicine_id", nullable = false)
    @NotNull(message = "Medicine is required")
    private Medicine medicine;

    @NotNull(message = "Forecasted quantity is required")
    @Min(value = 0, message = "Forecasted quantity cannot be negative")
    @Column(name = "forecasted_quantity", nullable = false)
    private Integer forecastedQuantity;

    @NotNull(message = "Forecast date is required")
    @Column(name = "forecast_date", nullable = false)
    private LocalDate forecastDate;

    @NotNull(message = "Confidence level is required")
    @DecimalMin(value = "0.0", message = "Confidence level must be at least 0.0")
    @DecimalMax(value = "1.0", message = "Confidence level cannot exceed 1.0")
    @Column(name = "confidence_level", nullable = false, precision = 3, scale = 2)
    private BigDecimal confidenceLevel;
}
