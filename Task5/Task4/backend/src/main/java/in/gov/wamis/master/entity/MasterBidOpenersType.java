package in.gov.wamis.master.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.Immutable;

/**
 * Read-only master row loaded from demo_masters_updated.xls (sheet: master_bid_openers_type).
 * Rows are maintained by db/master-data.sql, not by the application.
 */
@Entity
@Immutable
@Table(name = "master_bid_openers_type", schema = "wamis_master")
public class MasterBidOpenersType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "botype", length = 50, nullable = false, unique = true)
    private String boType;

    @Column(name = "bovalue", nullable = false)
    private Integer boValue;

    @Column(name = "description", length = 255)
    private String description;

    @Column(name = "display_order", nullable = false)
    private Integer displayOrder = 0;

    protected MasterBidOpenersType() {
    }

    public MasterBidOpenersType(String boType) {
        this.boType = boType;
    }

    public Long getId() {
        return id;
    }

    public String getBoType() {
        return boType;
    }

    public Integer getBoValue() {
        return boValue;
    }

    public String getDescription() {
        return description;
    }

    public Integer getDisplayOrder() {
        return displayOrder;
    }
}
