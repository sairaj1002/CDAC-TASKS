package com.cdac.egov.integration.gepnic.mapper;

import com.cdac.egov.integration.gepnic.forms.tender.TenderPushForm;
import com.cdac.egov.integration.gepnic.service.json.GePNICJsonBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Maps WAMIS internal tender structures into canonical GePNIC payloads.
 */
@Component
@RequiredArgsConstructor
public class WamisToGePNICMapper {

    private final GePNICJsonBuilder jsonBuilder;

    /**
     * Converts a TenderPushForm to canonical GePNIC tender JSON.
     */
    public String mapToGePNICTenderJson(TenderPushForm form, String uniqueRefNo, String uniqueFileNo, int actualOrgId) {
        List<Map<String, Object>> documents = new ArrayList<>();
        if (form.getDocuments() != null) {
            for (TenderPushForm.TenderDocumentItemForm doc : form.getDocuments()) {
                Map<String, Object> docMap = new HashMap<>();
                docMap.put("docCode", doc.getDocCode() != null ? doc.getDocCode() : "NITDOC");
                docMap.put("fileName", doc.getFileName());
                docMap.put("description", "Tender Attachment: " + doc.getFileName());
                docMap.put("mimeType", doc.getContentType() != null ? doc.getContentType() : ".pdf");
                docMap.put("size", doc.getFileSize() != null ? doc.getFileSize() : 15360L);
                documents.add(docMap);
            }
        }

        return jsonBuilder.buildTenderPushJson(
                uniqueRefNo,
                uniqueFileNo,
                actualOrgId,
                form.getWamisTenderId(),
                form.getTenderTitle(),
                form.getWorkDescription(),
                form.getEstimatedCost(),
                form.getTenderFee(),
                form.getEmdAmount(),
                form.getPublishDate(),
                form.getSubmissionEndDate(),
                documents
        );
    }
}
