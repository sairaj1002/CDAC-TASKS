package com.company.enterprise.features.wms.service;

import java.time.LocalDate;
import java.util.List;

import com.company.enterprise.features.wms.dto.response.WorkReportSummary;
import com.company.enterprise.features.wms.dto.response.WorkResponse;

public interface WorkReportService {

    List<WorkResponse> getWorkRegister(LocalDate fromDate, LocalDate toDate, String contractor);

    List<WorkResponse> getContractorDetails(LocalDate fromDate, LocalDate toDate, String contractor);

    WorkReportSummary getSummary(LocalDate fromDate, LocalDate toDate, String contractor);

    byte[] generateWorkRegisterPdf(LocalDate fromDate, LocalDate toDate, String contractor);
}
