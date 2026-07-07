package com.healthsync.district.entity;

import com.healthsync.common.BaseAuditEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Index;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * Entity mapping administrative districts.
 */
@Entity
@Table(
    name = "districts",
    indexes = {
        @Index(name = "idx_districts_name", columnList = "name")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class District extends BaseAuditEntity {

    @NotBlank(message = "District name is required")
    @Column(name = "name", nullable = false, unique = true, length = 100)
    private String name;

    @NotBlank(message = "Region is required")
    @Column(name = "region", nullable = false, length = 100)
    private String region;

    @NotNull(message = "Population is required")
    @Column(name = "population", nullable = false)
    private Long population;
}
