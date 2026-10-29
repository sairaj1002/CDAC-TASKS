package com.cdac.egov.integration.gepnic.pretender.services;

import com.cdac.egov.integration.gepnic.pretender.forms.TenderRequest;
import com.cdac.egov.integration.gepnic.pretender.works.WorkSummary;
import com.cdac.egov.integration.gepnic.pretender.works.WorksService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class ParentWorkSelectionService {
    private final WorksService works;

    public ParentWorkSelectionService(WorksService works) { this.works = works; }

    public TenderRequest resolve(TenderRequest request) {
        String id = request.parentWorkId();
        if (id == null || id.isBlank()) {
            if (hasText(request.parentWorkNumber()) || hasText(request.parentWorkDescription())) {
                throw new IllegalArgumentException("Select a parent work from the search results; work details cannot be submitted without its ID");
            }
            return request.withParentWork(null, null, null);
        }

        WorkSummary work = works.findById(id).orElseThrow(() -> new EntityNotFoundException("Selected parent work " + id + " was not found"));
        return request.withParentWork(work.id(), work.workNumber(), work.description());
    }

    private boolean hasText(String value) { return value != null && !value.isBlank(); }
}
