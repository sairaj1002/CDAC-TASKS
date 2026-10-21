package com.company.enterprise.features.wms.service.impl;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.nio.charset.StandardCharsets;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.company.enterprise.features.wms.mapper.WorkMapper;
import com.company.enterprise.features.wms.repository.WorkRepository;

class WorkReportServiceImplTest {

    @Test
    void generatesAValidPdfWorkRegister() {
        WorkRepository repository = Mockito.mock(WorkRepository.class);
        when(repository.findAll(any(org.springframework.data.jpa.domain.Specification.class)))
                .thenReturn(List.of());
        WorkReportServiceImpl service = new WorkReportServiceImpl(repository, Mockito.mock(WorkMapper.class));

        byte[] pdf = service.generateWorkRegisterPdf(null, null, null);

        assertTrue(new String(pdf, StandardCharsets.US_ASCII).startsWith("%PDF-"));
    }
}
