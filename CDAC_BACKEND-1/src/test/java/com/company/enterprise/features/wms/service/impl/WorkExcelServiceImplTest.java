package com.company.enterprise.features.wms.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockMultipartFile;

import com.company.enterprise.features.wms.dto.response.WorkResponse;
import com.company.enterprise.features.wms.service.WorkService;

class WorkExcelServiceImplTest {

    @Test
    void previewsValidRowsAndReportsInvalidRows() throws Exception {
        WorkExcelServiceImpl service = new WorkExcelServiceImpl(Mockito.mock(WorkService.class));
        MockMultipartFile workbook = createWorkbook();

        var preview = service.preview(workbook);

        assertEquals(1, preview.validRows().size());
        assertEquals(2, preview.validRows().getFirst().rowNumber());
        assertEquals("Maintenance work", preview.validRows().getFirst().workDetails());
        assertEquals(LocalDate.of(2026, 10, 1), preview.validRows().getFirst().stipulatedStartDate());
        assertEquals(1, preview.errors().size());
        assertEquals(3, preview.errors().getFirst().rowNumber());
    }

    @Test
    void importsOnlyValidRowsAfterWorkbookIsConfirmed() throws Exception {
        WorkService workService = Mockito.mock(WorkService.class);
        when(workService.createMany(anyList())).thenReturn(java.util.List.of(new WorkResponse()));
        WorkExcelServiceImpl service = new WorkExcelServiceImpl(workService);

        var result = service.importValidRows(createWorkbook());

        assertEquals(1, result.importedCount());
        assertEquals(1, result.errors().size());
        verify(workService).createMany(Mockito.argThat(requests -> requests.size() == 1));
    }

    @Test
    void generatedSampleWorkbookCanBePreviewedForImport() {
        WorkExcelServiceImpl service = new WorkExcelServiceImpl(Mockito.mock(WorkService.class));
        MockMultipartFile sample = new MockMultipartFile(
                "file",
                "sample-work-register.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                service.generateSampleWorkbook());

        var preview = service.preview(sample);

        assertEquals(2, preview.validRows().size());
        assertEquals(0, preview.errors().size());
        assertEquals("Sample Contractor A", preview.validRows().getFirst().contractorName());
    }

    private MockMultipartFile createWorkbook() throws Exception {
        try (XSSFWorkbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            var sheet = workbook.createSheet("Works");
            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("Work Details");
            header.createCell(1).setCellValue("Contractor Name");
            header.createCell(2).setCellValue("Actual Start Date");
            header.createCell(3).setCellValue("Stipulated Start Date");
            header.createCell(4).setCellValue("Stipulated End Date");

            Row valid = sheet.createRow(1);
            valid.createCell(0).setCellValue("Maintenance work");
            valid.createCell(1).setCellValue("Example Contractor");
            valid.createCell(2).setCellValue("2026-10-01");
            valid.createCell(3).setCellValue("2026-10-01");
            valid.createCell(4).setCellValue("2026-10-15");

            Row invalid = sheet.createRow(2);
            invalid.createCell(0).setCellValue("Invalid date range");
            invalid.createCell(1).setCellValue("Example Contractor");
            invalid.createCell(3).setCellValue("2026-10-15");
            invalid.createCell(4).setCellValue("2026-10-01");

            workbook.write(output);
            return new MockMultipartFile(
                    "file", "works.xlsx",
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                    output.toByteArray());
        }
    }
}
