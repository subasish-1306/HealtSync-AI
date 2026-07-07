package com.healthsync.patient.entity;

import com.healthsync.common.BaseAuditEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Index;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * Entity mapping ICD-10 disease categories.
 */
@Entity
@Table(
    name = "disease_categories",
    indexes = {
        @Index(name = "idx_disease_categories_code", columnList = "code"),
        @Index(name = "idx_disease_categories_name", columnList = "name")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DiseaseCategory extends BaseAuditEntity {

    @NotBlank(message = "ICD-10 code is required")
    @Column(name = "code", nullable = false, unique = true, length = 20)
    private String code;

    @NotBlank(message = "Category name is required")
    @Column(name = "name", nullable = false, length = 150)
    private String name;

    @Column(name = "description", length = 255)
    private String description;

    @NotNull(message = "Priority is required")
    @Column(name = "priority", nullable = false)
    private Integer priority;

    @NotNull(message = "Communicable flag is required")
    @Column(name = "communicable", nullable = false)
    private Boolean communicable;

    @NotNull(message = "Seasonal flag is required")
    @Column(name = "seasonal", nullable = false)
    private Boolean seasonal;
}
