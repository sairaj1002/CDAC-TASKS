package com.cdac.scope.entities;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
    name = "dropdown_option",
    uniqueConstraints = {
        @UniqueConstraint(name = "uq_dropdown_category_code", columnNames = {"category", "code"})
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DropdownOptionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "active", nullable = false)
    private Boolean active = true;

    @NotBlank
    @Size(max = 60)
    @Column(name = "category", nullable = false, length = 60)
    private String category;

    @NotBlank
    @Size(max = 60)
    @Column(name = "code", nullable = false, length = 60)
    private String code;

    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @NotNull
    @Column(name = "display_order", nullable = false)
    private Integer displayOrder = 1;

    @NotBlank
    @Size(max = 150)
    @Column(name = "label", nullable = false, length = 150)
    private String label;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @PrePersist
    public void prePersist() {
        Instant now = Instant.now();
        if (createdAt == null) {
            createdAt = now;
        }
        if (updatedAt == null) {
            updatedAt = now;
        }
        if (active == null) {
            active = true;
        }
        if (displayOrder == null) {
            displayOrder = 1;
        }
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = Instant.now();
    }
}
