package com.company.enterprise.features.wms.controller;

import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.company.enterprise.core.response.ApiResponse;
import com.company.enterprise.features.wms.dto.response.WorkImportPreview;
import com.company.enterprise.features.wms.dto.response.WorkImportResult;
import com.company.enterprise.features.wms.service.WorkExcelService;

@RestController
@RequestMapping("/api/work/excel")
public class WorkExcelController {

    private final WorkExcelService excelService;

    public WorkExcelController(WorkExcelService excelService) {
        this.excelService = excelService;
    }

    @GetMapping(value = "/sample", produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    public ResponseEntity<byte[]> downloadSampleWorkbook() {
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment().filename("sample-work-register.xlsx").build().toString())
                .body(excelService.generateSampleWorkbook());
    }

    @PostMapping(value = "/preview", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<WorkImportPreview> preview(@RequestPart("file") MultipartFile file) {
        return ApiResponse.success("Excel workbook validated", excelService.preview(file));
    }

    @PostMapping(value = "/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<WorkImportResult> importRows(@RequestPart("file") MultipartFile file) {
        return ApiResponse.success("Valid Excel rows imported", excelService.importValidRows(file));
    }
}
