package com.company.enterprise.features.ams.entity;

import com.company.enterprise.core.auditing.AuditableBaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
        name = "ams_assets",
        uniqueConstraints = @UniqueConstraint(name = "uk_ams_assets_asset_code", columnNames = "asset_code"))
@Getter
@Setter
@NoArgsConstructor
public class AssetEntity extends AuditableBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ams_id", nullable = false, updatable = false)
    private Long amsId;

    @Column(name = "asset_code", nullable = false, length = 50)
    private String assetCode;

    @Column(name = "asset_name", nullable = false, length = 100)
    private String assetName;

    @Enumerated(EnumType.STRING)
    @Column(name = "asset_type", nullable = false, length = 30)
    private AssetType assetType;

    @Column(name = "description", nullable = false, length = 500)
    private String description;

    @Column(name = "location", nullable = false, length = 150)
    private String location;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private AssetStatus status;
}
