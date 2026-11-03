package in.gov.wamis.master.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.Immutable;

/**
 * Read-only master row loaded from demo_masters_updated.xls (sheet: master_tender_type).
 * Rows are maintained by db/master-data.sql, not by the application.
 */
@Entity
@Immutable
@Table(name = "master_tender_type", schema = "wamis_master")
public class MasterTenderType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tendertype", length = 50, nullable = false, unique = true)
    private String tenderType;

    @Column(name = "description", length = 255)
    private String description;

    @Column(name = "display_order", nullable = false)
    private Integer displayOrder = 0;

    protected MasterTenderType() {
    }

    public MasterTenderType(String tenderType) {
        this.tenderType = tenderType;
    }

    public Long getId() {
        return id;
    }

    public String getTenderType() {
        return tenderType;
    }

    public String getDescription() {
        return description;
    }

    public Integer getDisplayOrder() {
        return displayOrder;
    }
}
