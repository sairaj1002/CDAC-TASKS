package in.gov.wamis.master.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.Immutable;

/**
 * Read-only master row loaded from demo_masters_updated.xls (sheet: master_form_of_contract).
 * Rows are maintained by db/master-data.sql, not by the application.
 */
@Entity
@Immutable
@Table(name = "master_form_of_contract", schema = "wamis_master")
public class MasterFormOfContract {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "formofcontract", length = 100, nullable = false, unique = true)
    private String formOfContract;

    @Column(name = "display_order", nullable = false)
    private Integer displayOrder = 0;

    protected MasterFormOfContract() {
    }

    public MasterFormOfContract(String formOfContract) {
        this.formOfContract = formOfContract;
    }

    public Long getId() {
        return id;
    }

    public String getFormOfContract() {
        return formOfContract;
    }

    public Integer getDisplayOrder() {
        return displayOrder;
    }
}
