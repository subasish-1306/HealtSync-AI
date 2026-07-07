package com.healthsync.inventory.entity;

import com.healthsync.common.BaseAuditEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * Entity mapping drug types and their generic descriptors.
 */
@Entity
@Table(
    name = "medicines",
    indexes = {
        @Index(name = "idx_medicines_code", columnList = "code"),
        @Index(name = "idx_medicines_name", columnList = "name"),
        @Index(name = "idx_medicines_category", columnList = "category_id")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Medicine extends BaseAuditEntity {

    @NotBlank(message = "Medicine name is required")
    @Column(name = "name", nullable = false, length = 150)
    private String name;

    @NotBlank(message = "Generic name is required")
    @Column(name = "generic_name", nullable = false, length = 150)
    private String genericName;

    @NotBlank(message = "Unique medicine code is required")
    @Column(name = "code", nullable = false, unique = true, length = 50)
    private String code;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    @NotNull(message = "Category is required")
    private MedicineCategory category;

    @NotBlank(message = "Strength details are required")
    @Column(name = "strength", nullable = false, length = 50)
    private String strength;

    @NotBlank(message = "Unit measurement is required")
    @Column(name = "unit", nullable = false, length = 50)
    private String unit;
}
