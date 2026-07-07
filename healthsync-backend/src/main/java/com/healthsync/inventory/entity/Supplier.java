package com.healthsync.inventory.entity;

import com.healthsync.common.BaseAuditEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Index;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

/**
 * Entity mapping logistics suppliers of medicines.
 */
@Entity
@Table(
    name = "suppliers",
    indexes = {
        @Index(name = "idx_suppliers_name", columnList = "name")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Supplier extends BaseAuditEntity {

    @NotBlank(message = "Supplier name is required")
    @Column(name = "name", nullable = false, unique = true, length = 150)
    private String name;

    @NotBlank(message = "Contact person name is required")
    @Column(name = "contact_person", nullable = false, length = 100)
    private String contactPerson;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Column(name = "email", nullable = false, length = 100)
    private String email;

    @NotBlank(message = "Phone number is required")
    @Column(name = "phone", nullable = false, length = 20)
    private String phone;

    @NotBlank(message = "Address is required")
    @Column(name = "address", nullable = false, length = 255)
    private String address;
}
