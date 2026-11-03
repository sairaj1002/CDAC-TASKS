package in.gov.wamis.master.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.Immutable;

/**
 * Read-only master row loaded from demo_masters_updated.xls (sheet: product_category).
 * Each product category belongs to one tender category; the same name may appear
 * under more than one tender category.
 */
@Entity
@Immutable
@Table(name = "master_product_category", schema = "wamis_master")
public class MasterProductCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", length = 100, nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "tender_category_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_product_tender_category"))
    private MasterTenderCategory tenderCategory;

    @Column(name = "display_order", nullable = false)
    private Integer displayOrder = 0;

    protected MasterProductCategory() {
    }

    public MasterProductCategory(String name, MasterTenderCategory tenderCategory) {
        this.name = name;
        this.tenderCategory = tenderCategory;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public MasterTenderCategory getTenderCategory() {
        return tenderCategory;
    }

    public Integer getDisplayOrder() {
        return displayOrder;
    }
}
