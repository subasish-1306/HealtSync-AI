package com.healthsync.laboratory.entity;

import com.healthsync.common.BaseAuditEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Index;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

/**
 * Entity mapping diagnostic procedures and laboratory assays.
 */
@Entity
@Table(
    name = "diagnostic_tests",
    indexes = {
        @Index(name = "idx_diag_tests_code", columnList = "code"),
        @Index(name = "idx_diag_tests_name", columnList = "name")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DiagnosticTest extends BaseAuditEntity {

    @NotBlank(message = "Test name is required")
    @Column(name = "name", nullable = false, length = 150)
    private String testName;

    @NotBlank(message = "Unique diagnostic test code is required")
    @Column(name = "code", nullable = false, unique = true, length = 50)
    private String code;

    @Column(name = "description", length = 255)
    private String description;

    @NotBlank(message = "Reference normal range description is required")
    @Column(name = "normal_range", nullable = false, length = 100)
    private String normalRange;

    @NotNull(message = "Base cost is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Base cost cannot be negative")
    @Column(name = "base_cost", nullable = false, precision = 10, scale = 2)
    private BigDecimal baseCost;

    @NotBlank(message = "Department category is required")
    @Column(name = "department", nullable = false, length = 100)
    private String department;

    @NotNull(message = "Test duration in minutes is required")
    @Column(name = "duration", nullable = false)
    private Integer duration;

    @NotNull(message = "Test availability flag is required")
    @Column(name = "is_available", nullable = false)
    private Boolean isAvailable;
}
