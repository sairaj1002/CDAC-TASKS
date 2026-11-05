package in.gov.wamis.master.form;

import com.fasterxml.jackson.annotation.JsonFormat;
import in.gov.wamis.master.entity.DropdownMaster;

import java.time.LocalDateTime;

public class DropdownMasterResponseForm {

    private Long id;
    private String category;
    private String code;
    private String displayName;
    private String description;
    private Integer displayOrder;
    private Boolean active;
    private String createdBy;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    private String updatedBy;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;

    public DropdownMasterResponseForm() {
    }

    public static DropdownMasterResponseForm fromEntity(DropdownMaster entity) {
        if (entity == null) {
            return null;
        }
        DropdownMasterResponseForm form = new DropdownMasterResponseForm();
        form.setId(entity.getId());
        form.setCategory(entity.getCategory());
        form.setCode(entity.getCode());
        form.setDisplayName(entity.getDisplayName());
        form.setDescription(entity.getDescription());
        form.setDisplayOrder(entity.getDisplayOrder());
        form.setActive(entity.getActive());
        form.setCreatedBy(entity.getCreatedBy());
        form.setCreatedAt(entity.getCreatedAt());
        form.setUpdatedBy(entity.getUpdatedBy());
        form.setUpdatedAt(entity.getUpdatedAt());
        return form;
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

