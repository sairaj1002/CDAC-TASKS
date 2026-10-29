package com.cdac.egov.integration.gepnic.service.tender;

import com.cdac.egov.integration.gepnic.forms.common.IntegrationResultForm;
import com.cdac.egov.integration.gepnic.forms.tender.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * High-level orchestration facade for all Tender and Corrigendum operations.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TenderIntegrationService {

    private final TenderPushService pushService;
    private final TenderPullService pullService;

    public IntegrationResultForm pushTender(TenderPushForm form) {
        return pushService.pushTender(form);
    }

    public List<TenderResponseForm> pullTenders(TenderPullForm form) {
        return pullService.pullTendersByDate(form);
    }

    public List<CorrigendumResponseForm> pullCorrigendums(CorrigendumPullForm form) {
        return pullService.pullCorrigendumsByDate(form);
    }
}
