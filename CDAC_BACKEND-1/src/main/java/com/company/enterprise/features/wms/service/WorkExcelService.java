package com.company.enterprise.features.wms.service;

import org.springframework.web.multipart.MultipartFile;

import com.company.enterprise.features.wms.dto.response.WorkImportPreview;
import com.company.enterprise.features.wms.dto.response.WorkImportResult;

public interface WorkExcelService {

    WorkImportPreview preview(MultipartFile file);

    WorkImportResult importValidRows(MultipartFile file);

    byte[] generateSampleWorkbook();
}
