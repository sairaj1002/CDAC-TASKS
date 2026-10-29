package com.cdac.egov.integration.gepnic.pretender.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Column;
import jakarta.persistence.Table;

@Entity
@Table(name = "master_number_of_bid_openers")
public class MasterNumberOfBidOpeners extends MasterEntity {
    @Column(name = "bovalue")
    private Integer bovalue;

    public MasterNumberOfBidOpeners() {}

    public Integer getBovalue() { return bovalue; }
    public void setBovalue(Integer bovalue) { this.bovalue = bovalue; }
}

