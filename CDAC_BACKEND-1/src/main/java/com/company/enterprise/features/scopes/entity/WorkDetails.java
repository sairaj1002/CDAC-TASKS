package com.company.enterprise.features.scopes.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "work_details")
@Getter
@Setter
@NoArgsConstructor
public class WorkDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "work_id", nullable = false, updatable = false)
    private Long workId;

    @Column(name = "sub_work_details", nullable = false, length = 500)
    private String subWorkDetails;

    @OneToMany(mappedBy = "workDetails", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("scopeId ASC")
    private List<WorkScopes> workScopes = new ArrayList<>();

    public void replaceWorkScopes(List<WorkScopes> scopes) {
        workScopes.clear();
        for (WorkScopes scope : scopes) {
            scope.setWorkDetails(this);
            workScopes.add(scope);
        }
    }
}