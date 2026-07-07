package com.healthsync.inventory.entity;

import com.healthsync.common.BaseAuditEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Index;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

/**
 * Entity mapping medicine classifications (e.g. Analgesics, Antibiotics).
 */
@Entity
@Table(
    name = "medicine_categories",
    indexes = {
        @Index(name = "idx_medicine_categories_name", columnList = "name")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MedicineCategory extends BaseAuditEntity {

    @NotBlank(message = "Category name is required")
    @Column(name = "name", nullable = false, unique = true, length = 100)
    private String name;

    @Column(name = "description", length = 255)
    private String description;
}
