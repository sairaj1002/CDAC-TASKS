package com.cdac.egov.integration.gepnic.pretender.works;

import java.util.List;
import java.util.Optional;

public interface WorksService {
    List<WorkSummary> listAvailable();
    List<WorkSummary> search(String query);
    Optional<WorkSummary> findById(String id);
}
