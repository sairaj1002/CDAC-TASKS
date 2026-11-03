package in.gov.wamis.master.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.Immutable;

/**
 * Read-only master row loaded from demo_masters_updated.xls (sheet: master_tenderer_class).
 * Rows are maintained by db/master-data.sql, not by the application.
 */
@Entity
@Immutable
@Table(name = "master_tenderer_class", schema = "wamis_master")
public class MasterTendererClass {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tenderclass", length = 50, nullable = false, unique = true)
    private String tenderClass;

    @Column(name = "description", length = 255)
    private String description;

    @Column(name = "display_order", nullable = false)
    private Integer displayOrder = 0;

    protected MasterTendererClass() {
    }

    public MasterTendererClass(String tenderClass) {
        this.tenderClass = tenderClass;
    }

    public Long getId() {
        return id;
    }

    public String getTenderClass() {
        return tenderClass;
    }

    public String getDescription() {
        return description;
    }

    public Integer getDisplayOrder() {
        return displayOrder;
    }
}
