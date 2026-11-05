package in.gov.wamis.master.controller;

import in.gov.wamis.common.security.SecurityContextHelper;
import in.gov.wamis.master.form.DropdownMasterForm;
import in.gov.wamis.master.form.DropdownMasterResponseForm;
import in.gov.wamis.master.service.DropdownMasterService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Controller exposing dropdown master data required by frontend select menus.
 * Strictly returns Response Forms; keeps JPA entities internal.
 */
@RestController
@RequestMapping("/api/masters/dropdowns")
public class DropdownMasterController {

    private final DropdownMasterService dropdownService;
    private final SecurityContextHelper securityContextHelper;

    public DropdownMasterController(DropdownMasterService dropdownService,
                                   SecurityContextHelper securityContextHelper) {
        this.dropdownService = dropdownService;
        this.securityContextHelper = securityContextHelper;
    }

    @GetMapping
    public ResponseEntity<Map<String, List<DropdownMasterResponseForm>>> getAllDropdowns() {
        return ResponseEntity.ok(dropdownService.getAllGrouped());
    }

    @GetMapping("/{category}")
    public ResponseEntity<List<DropdownMasterResponseForm>> getDropdownByCategory(@PathVariable String category) {
        return ResponseEntity.ok(dropdownService.getByCategory(category.toUpperCase()));
    }

    @PostMapping
    public ResponseEntity<DropdownMasterResponseForm> createDropdownOption(@Valid @RequestBody DropdownMasterForm form) {
        String user = securityContextHelper.getCurrentUser();
        DropdownMasterResponseForm created = dropdownService.createOption(form, user);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DropdownMasterResponseForm> updateDropdownOption(@PathVariable Long id,
                                                                         @Valid @RequestBody DropdownMasterForm form) {
        String user = securityContextHelper.getCurrentUser();
        DropdownMasterResponseForm updated = dropdownService.updateOption(id, form, user);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteDropdownOption(@PathVariable Long id) {
        dropdownService.deleteOption(id);
        return ResponseEntity.ok(Collections.singletonMap("status", "DELETED"));
    }
}

