package com.healthsync.inventory.entity;

import com.healthsync.common.BaseAuditEntity;
import com.healthsync.district.entity.HealthCenter;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * Entity logging individual inventory events (receives, transfers, dispenses).
 */
@Entity
@Table(
    name = "inventory_transactions",
    indexes = {
        @Index(name = "idx_inv_trans_source", columnList = "source_center_id"),
        @Index(name = "idx_inv_trans_dest", columnList = "destination_center_id"),
        @Index(name = "idx_inv_trans_medicine", columnList = "medicine_id"),
        @Index(name = "idx_inv_trans_type", columnList = "type")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryTransaction extends BaseAuditEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "source_center_id")
    private HealthCenter sourceCenter;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "destination_center_id")
    private HealthCenter destinationCenter;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medicine_id", nullable = false)
    @NotNull(message = "Medicine is required")
    private Medicine medicine;

    @NotNull(message = "Transaction quantity is required")
    @Min(value = 1, message = "Transaction quantity must be at least 1")
    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Transaction type is required")
    @Column(name = "type", nullable = false, length = 20)
    private InventoryTransactionType type;

    @Column(name = "remarks", length = 255)
    private String remarks;
}
