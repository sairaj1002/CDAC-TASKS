package com.company.enterprise.features.scopes.entity;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "work_scopes")
@Getter
@Setter
@NoArgsConstructor
public class WorkScopes {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "scope_id", nullable = false, updatable = false)
    private Long scopeId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "work_id", nullable = false)
    private WorkDetails workDetails;

    @Column(name = "name_of_river_nallah_barrier", nullable = false, length = 200)
    private String nameOfRiverNallahBarrier;

    @Column(name = "width", nullable = false, precision = 12, scale = 3)
    private BigDecimal width;

    @Column(name = "length", nullable = false, precision = 12, scale = 3)
    private BigDecimal length;

    @Column(name = "approach_road", nullable = false, length = 500)
    private String approachRoad;

    @Column(name = "number_of_spans", nullable = false)
    private Integer numberOfSpans;

    @Column(name = "span_length", nullable = false, precision = 12, scale = 3)
    private BigDecimal spanLength;

    @Enumerated(EnumType.STRING)
    @Column(name = "bridge_activity", nullable = false, length = 40)
    private BridgeActivity bridgeActivity;
}
