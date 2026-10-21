package com.company.enterprise.features.wms.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.company.enterprise.core.response.ApiResponse;
import com.company.enterprise.features.wms.dto.response.WorkReportSummary;
import com.company.enterprise.features.wms.dto.response.WorkResponse;
import com.company.enterprise.features.wms.service.WorkReportService;

@RestController
@RequestMapping("/api/work/reports")
public class WorkReportController {

    private final WorkReportService reportService;

    public WorkReportController(WorkReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/summary")
    public ApiResponse<WorkReportSummary> getSummary(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            @RequestParam(required = false) String contractor) {
        return ApiResponse.success(
                "Work report summary generated successfully",
                reportService.getSummary(fromDate, toDate, contractor));
    }

    @GetMapping("/details")
    public ApiResponse<List<WorkResponse>> getContractorDetails(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            @RequestParam String contractor) {
        return ApiResponse.success(
                "Contractor work details retrieved successfully",
                reportService.getContractorDetails(fromDate, toDate, contractor));
    }

    @GetMapping(value = "/work-register.pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> downloadWorkRegister(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            @RequestParam(required = false) String contractor) {
        byte[] pdf = reportService.generateWorkRegisterPdf(fromDate, toDate, contractor);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment().filename("work-register.pdf").build().toString())
                .body(pdf);
    }
}
