package com.cdac.egov.integration.gepnic.pretender.entities;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class MasterEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "code", length = 255)
    private String code;

    @Column(name = "description", columnDefinition = "text")
    private String description;

    @Column(name = "source_row_key", unique = true, length = 255)
    private String sourceRowKey;

    public MasterEntity() {}
    public Long getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getSourceRowKey() { return sourceRowKey; }
    public void setSourceRowKey(String sourceRowKey) { this.sourceRowKey = sourceRowKey; }
}
