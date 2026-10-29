package com.cdac.egov.integration.gepnic.pretender.controllers;

import java.util.List;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.cdac.egov.integration.gepnic.pretender.forms.TenderRequest;
import com.cdac.egov.integration.gepnic.pretender.forms.TenderResponse;
import com.cdac.egov.integration.gepnic.pretender.services.TenderService;
import com.cdac.egov.integration.gepnic.pretender.services.TenderWorkflowService;

@RestController
@RequestMapping("/api/tenders")
@org.springframework.web.bind.annotation.CrossOrigin(origins = "*")
public class TenderController {
    private final TenderService tenders;
    private final TenderWorkflowService workflow;
    public TenderController(TenderService tenders, TenderWorkflowService workflow) { this.tenders = tenders; this.workflow = workflow; }

    @GetMapping
    public List<TenderResponse> list() { return tenders.list(); }

    @GetMapping("/{id}")
    public TenderResponse get(@PathVariable long id) { return tenders.get(id); }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TenderResponse create(@Valid @RequestBody TenderRequest request) { return workflow.create(request); }

    @PostMapping(value = "/with-files", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public TenderResponse createWithFiles(@Valid @RequestPart("tender") TenderRequest request,
        @RequestPart(value = "nitFile", required = false) MultipartFile nit,
        @RequestPart(value = "dtcnFile", required = false) MultipartFile dtcn,
        @RequestPart(value = "otherFile", required = false) MultipartFile other) {
        return workflow.createWithFiles(request, nit, dtcn, other);
    }

    @PutMapping("/{id}")
    public TenderResponse update(@PathVariable long id, @Valid @RequestBody TenderRequest request) {
        return workflow.update(id, request);
    }

    @PutMapping(value = "/{id}/with-files", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public TenderResponse updateWithFiles(@PathVariable long id, @Valid @RequestPart("tender") TenderRequest request,
        @RequestPart(value = "nitFile", required = false) MultipartFile nit,
        @RequestPart(value = "dtcnFile", required = false) MultipartFile dtcn,
        @RequestPart(value = "otherFile", required = false) MultipartFile other) {
        return workflow.updateWithFiles(id, request, nit, dtcn, other);
    }
}
