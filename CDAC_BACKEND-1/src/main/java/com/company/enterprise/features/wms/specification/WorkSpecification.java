package com.company.enterprise.features.wms.specification;

import java.time.LocalDate;
import java.util.Locale;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.company.enterprise.features.wms.entity.WorkEntity;

import jakarta.persistence.criteria.Predicate;

public final class WorkSpecification {

    private WorkSpecification() {
    }

    public static Specification<WorkEntity> matchingSearch(String search) {
        if (search == null || search.isBlank()) {
            return (root, query, builder) -> builder.conjunction();
        }

        String pattern = "%" + search.trim().toLowerCase(Locale.ROOT) + "%";
        return (root, query, builder) -> {
            Predicate detailsMatch = builder.like(builder.lower(root.get("workDetails")), pattern);
            Predicate contractorMatch = builder.like(builder.lower(root.get("contractorName")), pattern);
            return builder.or(detailsMatch, contractorMatch);
        };
    }

    public static Specification<WorkEntity> matchingReportFilters(
            LocalDate fromDate,
            LocalDate toDate,
            String contractor) {
        return (root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (fromDate != null) {
                predicates.add(builder.greaterThanOrEqualTo(root.get("stipulatedStartDate"), fromDate));
            }
            if (toDate != null) {
                predicates.add(builder.lessThanOrEqualTo(root.get("stipulatedStartDate"), toDate));
            }
            if (contractor != null && !contractor.isBlank()) {
                predicates.add(builder.like(
                        builder.lower(root.get("contractorName")),
                        "%" + contractor.trim().toLowerCase(Locale.ROOT) + "%"));
            }
            return builder.and(predicates.toArray(Predicate[]::new));
        };
    }

    public static Specification<WorkEntity> matchingContractorReportDetails(
            LocalDate fromDate,
            LocalDate toDate,
            String contractor) {
        String exactContractor = contractor.trim().toLowerCase(Locale.ROOT);
        return (root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (fromDate != null) {
                predicates.add(builder.greaterThanOrEqualTo(root.get("stipulatedStartDate"), fromDate));
            }
            if (toDate != null) {
                predicates.add(builder.lessThanOrEqualTo(root.get("stipulatedStartDate"), toDate));
            }
            predicates.add(builder.equal(builder.lower(root.get("contractorName")), exactContractor));
            return builder.and(predicates.toArray(Predicate[]::new));
        };
    }
}
