package in.gov.wamis.master.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.Immutable;

/**
 * Read-only master row loaded from demo_masters_updated.xls (sheet: master_tender_category).
 * Rows are maintained by db/master-data.sql, not by the application.
 */
@Entity
@Immutable
@Table(name = "master_tender_category", schema = "wamis_master")
public class MasterTenderCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "category", length = 50, nullable = false, unique = true)
    private String category;

    @Column(name = "description", length = 255)
    private String description;

    @Column(name = "display_order", nullable = false)
    private Integer displayOrder = 0;

    protected MasterTenderCategory() {
    }

    public MasterTenderCategory(String category) {
        this.category = category;
    }

    public Long getId() {
        return id;
    }

    public String getCategory() {
        return category;
    }

    public String getDescription() {
        return description;
    }

    public Integer getDisplayOrder() {
        return displayOrder;
    }
}
