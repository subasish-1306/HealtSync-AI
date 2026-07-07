package com.healthsync.inventory.entity;

import com.healthsync.common.BaseAuditEntity;
import com.healthsync.district.entity.HealthCenter;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.Instant;

/**
 * Entity mapping the live stock level of medicines at specific Health Centers.
 */
@Entity
@Table(
    name = "inventories",
    uniqueConstraints = {
        @UniqueConstraint(name = "uc_center_medicine_batch", columnNames = {"health_center_id", "medicine_id", "batch_number"})
    },
    indexes = {
        @Index(name = "idx_inventories_center", columnList = "health_center_id"),
        @Index(name = "idx_inventories_medicine", columnList = "medicine_id"),
        @Index(name = "idx_inventories_expiry", columnList = "expiry_date")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Inventory extends BaseAuditEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "health_center_id", nullable = false)
    @NotNull(message = "Health center is required")
    private HealthCenter healthCenter;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medicine_id", nullable = false)
    @NotNull(message = "Medicine is required")
    private Medicine medicine;

    @NotNull(message = "Quantity is required")
    @Min(value = 0, message = "Quantity cannot be negative")
    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @NotBlank(message = "Batch number is required")
    @Column(name = "batch_number", nullable = false, length = 50)
    private String batchNumber;

    @NotNull(message = "Expiry date is required")
    @Column(name = "expiry_date", nullable = false)
    private Instant expiryDate;

    @NotNull(message = "Reorder level is required")
    @Min(value = 0, message = "Reorder level cannot be negative")
    @Column(name = "reorder_level", nullable = false)
    private Integer reorderLevel;
}
