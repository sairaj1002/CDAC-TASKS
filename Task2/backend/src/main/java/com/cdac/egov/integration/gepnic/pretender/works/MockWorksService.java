package com.cdac.egov.integration.gepnic.pretender.works;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

public class MockWorksService implements WorksService {
    public static final String SOURCE_LABEL = "DEVELOPMENT MOCK — NOT WAMIS CORE";
    private static final List<WorkSummary> WORKS = List.of(
        new WorkSummary("mock-work-1001", "MOCK-WORK-1001", "Development mock work: road resurfacing in a sample division", SOURCE_LABEL, true),
        new WorkSummary("mock-work-1002", "MOCK-WORK-1002", "Development mock work: bridge maintenance in a sample district", SOURCE_LABEL, true),
        new WorkSummary("mock-work-1003", "MOCK-WORK-1003", "Development mock work: drainage improvements for testing", SOURCE_LABEL, true),
        new WorkSummary("mock-work-1004", "MOCK-WORK-1004", "Development mock work: culvert replacement for testing", SOURCE_LABEL, true),
        new WorkSummary("mock-work-1005", "MOCK-WORK-1005", "Development mock work: widening and four-laning of National Highway corridor", SOURCE_LABEL, true),
        new WorkSummary("mock-work-1006", "MOCK-WORK-1006", "Development mock work: pavement overlay on state highway section", SOURCE_LABEL, true),
        new WorkSummary("mock-work-1007", "MOCK-WORK-1007", "Development mock work: causeway restoration on district road", SOURCE_LABEL, true),
        new WorkSummary("mock-work-1008", "MOCK-WORK-1008", "Development mock work: rural connectivity and slab drain work", SOURCE_LABEL, true),
        new WorkSummary("mock-work-1009", "MOCK-WORK-1009", "Development mock work: urban ring corridor asphalt resurfacing", SOURCE_LABEL, true),
        new WorkSummary("mock-work-1010", "MOCK-WORK-1010", "Development mock work: expressway connector and viaduct structure", SOURCE_LABEL, true)
    );

    @Override
    public List<WorkSummary> listAvailable() { return WORKS; }

    @Override
    public List<WorkSummary> search(String query) {
        String normalized = query.toLowerCase(Locale.ROOT);
        return WORKS.stream().filter(work -> work.workNumber().toLowerCase(Locale.ROOT).contains(normalized)
            || work.description().toLowerCase(Locale.ROOT).contains(normalized)
            || work.id().toLowerCase(Locale.ROOT).contains(normalized)).toList();
    }

    @Override
    public Optional<WorkSummary> findById(String id) {
        return WORKS.stream().filter(work -> work.id().equals(id)).findFirst();
    }
}
