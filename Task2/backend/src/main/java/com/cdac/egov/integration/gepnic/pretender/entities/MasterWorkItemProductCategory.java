package com.cdac.egov.integration.gepnic.pretender.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "master_work_item_product_category")
public class MasterWorkItemProductCategory extends MasterEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tender_category_id")
    private MasterTenderCategory tenderCategory;

    public MasterWorkItemProductCategory() {}

    public MasterTenderCategory getTenderCategory() { return tenderCategory; }
    public void setTenderCategory(MasterTenderCategory tenderCategory) { this.tenderCategory = tenderCategory; }
}

