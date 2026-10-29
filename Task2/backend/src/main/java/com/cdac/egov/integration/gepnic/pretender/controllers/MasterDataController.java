package com.cdac.egov.integration.gepnic.pretender.controllers;

import java.util.List;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import com.cdac.egov.integration.gepnic.pretender.forms.MasterOptionRequest;
import com.cdac.egov.integration.gepnic.pretender.forms.MasterOptionResponse;
import com.cdac.egov.integration.gepnic.pretender.services.MasterDataService;

@RestController
@RequestMapping("/api/masters")
@org.springframework.web.bind.annotation.CrossOrigin(origins = "*")
public class MasterDataController {
    private final MasterDataService masters;
    public MasterDataController(MasterDataService masters) { this.masters = masters; }

    @GetMapping("/{key}")
    public List<MasterOptionResponse> list(@PathVariable String key) { return masters.list(key); }

    @PostMapping("/{key}")
    @ResponseStatus(HttpStatus.CREATED)
    public MasterOptionResponse create(@PathVariable String key, @Valid @RequestBody MasterOptionRequest request) {
        return masters.create(key, request);
    }

    @PutMapping("/{key}/{id}")
    public MasterOptionResponse update(@PathVariable String key, @PathVariable long id,
        @Valid @RequestBody MasterOptionRequest request) {
        return masters.update(key, id, request);
    }

    @DeleteMapping("/{key}/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String key, @PathVariable long id) { masters.delete(key, id); }
}
