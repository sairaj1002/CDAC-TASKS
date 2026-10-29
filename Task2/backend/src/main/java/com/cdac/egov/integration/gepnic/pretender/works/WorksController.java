package com.cdac.egov.integration.gepnic.pretender.works;

import java.util.List;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/works")
@org.springframework.web.bind.annotation.CrossOrigin(origins = "*")
public class WorksController {
    private final WorksService works;

    public WorksController(WorksService works) { this.works = works; }

    @GetMapping
    public List<WorkSummary> listAvailable() { return works.listAvailable(); }

    @GetMapping("/search")
    public List<WorkSummary> search(@RequestParam("q") String query) {
        if (query == null || query.isBlank()) throw new IllegalArgumentException("Search query is required");
        if (query.length() > 120) throw new IllegalArgumentException("Search query cannot exceed 120 characters");
        return works.search(query.trim());
    }

    @GetMapping("/{id}")
    public WorkSummary get(@PathVariable String id) {
        return works.findById(id).orElseThrow(() -> new EntityNotFoundException("Work " + id + " was not found"));
    }
}
