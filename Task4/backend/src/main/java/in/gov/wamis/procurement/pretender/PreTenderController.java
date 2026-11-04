package in.gov.wamis.procurement.pretender;

import in.gov.wamis.common.constants.ApiConstants;
import in.gov.wamis.common.constants.CommonConstants;
import in.gov.wamis.common.enums.PortalCode;
import in.gov.wamis.integration.common.IntegrationResult;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * REST controller for WAMIS PreTender operations and lifecycle management.
 * Strictly returns Response Forms and Results; never returns JPA entities directly.
 */
@RestController
@RequestMapping(ApiConstants.PRE_TENDERS)
public class PreTenderController {

    private final PreTenderService preTenderService;

    public PreTenderController(PreTenderService preTenderService) {
        this.preTenderService = preTenderService;
    }

    @GetMapping
    public ResponseEntity<List<PreTenderResponseForm>> getAllPreTenders() {
        return ResponseEntity.ok(preTenderService.getAllPreTenders());
    }

    @PostMapping
    public ResponseEntity<PreTenderResponseForm> createPreTender(@Valid @RequestBody PreTenderForm form) {
        PreTenderResponseForm created = preTenderService.createPreTender(form);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PreTenderResponseForm> getPreTender(@PathVariable Long id) {
        PreTenderResponseForm response = preTenderService.getPreTender(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PreTenderResponseForm> updatePreTender(@PathVariable Long id, @Valid @RequestBody PreTenderForm form) {
        PreTenderResponseForm updated = preTenderService.updatePreTender(id, form);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deletePreTender(@PathVariable Long id) {
        preTenderService.deletePreTender(id);
        return ResponseEntity.ok(Collections.singletonMap("status", "DELETED"));
    }

    @PostMapping("/{id}/validate")
    public ResponseEntity<Map<String, String>> validatePreTender(@PathVariable Long id) {
        preTenderService.validatePreTender(id);
        return ResponseEntity.ok(Collections.singletonMap("status", "VALID"));
    }

    // the GePNIC tender JSON of this pre-tender, for viewing (nothing is sent)
    @GetMapping("/{id}/gepnic-json")
    public ResponseEntity<Map<String, Object>> getGePNICPayload(@PathVariable Long id) {
        return ResponseEntity.ok(preTenderService.getGePNICPayload(id));
    }

    @PostMapping("/{id}/integration-jobs")
    public ResponseEntity<IntegrationResult> submitIntegrationJob(
            @PathVariable Long id,
            @RequestParam(required = false, defaultValue = "GEPNIC") String portalCode,
            @RequestHeader(value = CommonConstants.HEADER_CORRELATION_ID, required = false) String correlationId,
            @RequestHeader(value = CommonConstants.HEADER_IDEMPOTENCY_KEY, required = false) String idempotencyKey) {

        PortalCode portal = PortalCode.fromString(portalCode);
        IntegrationResult result = preTenderService.submitPreTender(id, portal, correlationId, idempotencyKey);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/batch")
    public ResponseEntity<PreTenderBatchResponseForm> submitBatch(
            @Valid @RequestBody PreTenderBatchForm batchForm,
            @RequestHeader(value = CommonConstants.HEADER_CORRELATION_ID, required = false) String correlationId) {

        PreTenderBatchResponseForm response = preTenderService.submitBatch(batchForm, correlationId);
        return ResponseEntity.ok(response);
    }
}

