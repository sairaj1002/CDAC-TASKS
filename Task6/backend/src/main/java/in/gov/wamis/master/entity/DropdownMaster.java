package in.gov.wamis.master.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Master entity for configurable dropdown options across WAMIS modules.
 * Includes audit tracking fields.
 */
@Entity
@Table(name = "wamis_master_dropdowns", indexes = {
        @Index(name = "idx_dropdown_cat", columnList = "category"),
        @Index(name = "idx_dropdown_code", columnList = "code")
})
public class DropdownMaster {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "category", length = 50, nullable = false)
    private String category;

    @Column(name = "code", length = 50, nullable = false)
    private String code;

    @Column(name = "display_name", length = 150, nullable = false)
    private String displayName;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "display_order", nullable = false)
    private Integer displayOrder = 0;

    @Column(name = "active", nullable = false)
    private Boolean active = true;

    @Column(name = "created_by", length = 100)
    private String createdBy = "SYSTEM";

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_by", length = 100)
    private String updatedBy = "SYSTEM";

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    public DropdownMaster() {
    }

    public DropdownMaster(String category, String code, String displayName, String description, Integer displayOrder) {
        this.category = category;
        this.code = code;
        this.displayName = displayName;
        this.description = description;
        this.displayOrder = displayOrder;
        this.active = true;
        this.createdBy = "SYSTEM";
        this.createdAt = LocalDateTime.now();
        this.updatedBy = "SYSTEM";
        this.updatedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}

