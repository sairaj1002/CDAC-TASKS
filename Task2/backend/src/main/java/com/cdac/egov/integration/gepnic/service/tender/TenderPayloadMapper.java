package com.cdac.egov.integration.gepnic.service.tender;

import com.cdac.egov.integration.gepnic.forms.tender.TenderPushForm;
import com.cdac.egov.integration.gepnic.mapper.WamisToGePNICMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Service to map and assemble tender payload data for GePNIC submission.
 */
@Service
@RequiredArgsConstructor
public class TenderPayloadMapper {

    private final WamisToGePNICMapper wamisToGePNICMapper;

    /**
     * Maps the form to the canonical GePNIC JSON string with generated correlation IDs.
     */
    public String mapToGePNICJson(TenderPushForm form) {
        String uniqueRefNo = "WAMIS-REF-" + System.currentTimeMillis();
        String uniqueFileNo = "FILE-" + form.getWamisTenderId() + "-" + UUID.randomUUID().toString().substring(0, 4);
        int orgId = 2297; // NIC Organization identifier

        return wamisToGePNICMapper.mapToGePNICTenderJson(form, uniqueRefNo, uniqueFileNo, orgId);
    }
}
