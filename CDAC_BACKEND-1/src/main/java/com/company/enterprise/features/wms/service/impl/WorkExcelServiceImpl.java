package com.company.enterprise.features.wms.service.impl;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.company.enterprise.core.exception.BusinessException;
import com.company.enterprise.features.wms.dto.request.WorkCreateRequest;
import com.company.enterprise.features.wms.dto.response.WorkImportError;
import com.company.enterprise.features.wms.dto.response.WorkImportPreview;
import com.company.enterprise.features.wms.dto.response.WorkImportResult;
import com.company.enterprise.features.wms.dto.response.WorkImportRow;
import com.company.enterprise.features.wms.dto.response.WorkResponse;
import com.company.enterprise.features.wms.service.WorkExcelService;
import com.company.enterprise.features.wms.service.WorkService;

@Service
public class WorkExcelServiceImpl implements WorkExcelService {

    private static final int MAX_ROWS = 10_000;
    private static final List<DateTimeFormatter> TEXT_DATE_FORMATS = List.of(
            DateTimeFormatter.ISO_LOCAL_DATE,
            DateTimeFormatter.ofPattern("d-M-uuuu", Locale.ROOT).withResolverStyle(ResolverStyle.STRICT),
            DateTimeFormatter.ofPattern("d/M/uuuu", Locale.ROOT).withResolverStyle(ResolverStyle.STRICT));

    private final WorkService workService;

    public WorkExcelServiceImpl(WorkService workService) {
        this.workService = workService;
    }

    @Override
    @Transactional(readOnly = true)
    public WorkImportPreview preview(MultipartFile file) {
        return parse(file);
    }

    @Override
    @Transactional
    public WorkImportResult importValidRows(MultipartFile file) {
        WorkImportPreview preview = parse(file);
        List<WorkCreateRequest> requests = preview.validRows().stream()
                .map(this::toRequest)
                .toList();
        List<WorkResponse> imported = workService.createMany(requests);
        return new WorkImportResult(imported.size(), preview.errors());
    }

    @Override
    public byte[] generateSampleWorkbook() {
        try (XSSFWorkbook workbook = new XSSFWorkbook();
             var output = new java.io.ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Sample Work Register");
            String[] headers = {
                    "Work Details",
                    "Contractor Name",
                    "Actual Start Date",
                    "Stipulated Start Date",
                    "Stipulated End Date"
            };
            Row header = sheet.createRow(0);
            for (int column = 0; column < headers.length; column++) {
                header.createCell(column).setCellValue(headers[column]);
            }

            String[][] sampleRows = {
                    {
                            "Sample road maintenance work",
                            "Sample Contractor A",
                            "2026-10-01",
                            "2026-10-01",
                            "2026-10-15"
                    },
                    {
                            "Sample drainage inspection",
                            "Sample Contractor B",
                            "",
                            "2026-10-05",
                            "2026-10-20"
                    }
            };
            for (int rowIndex = 0; rowIndex < sampleRows.length; rowIndex++) {
                Row row = sheet.createRow(rowIndex + 1);
                for (int column = 0; column < sampleRows[rowIndex].length; column++) {
                    row.createCell(column).setCellValue(sampleRows[rowIndex][column]);
                }
            }

            sheet.setAutoFilter(new CellRangeAddress(0, sampleRows.length, 0, headers.length - 1));
            sheet.createFreezePane(0, 1);
            for (int column = 0; column < headers.length; column++) {
                sheet.autoSizeColumn(column);
            }
            workbook.write(output);
            return output.toByteArray();
        } catch (IOException exception) {
            throw new BusinessException("The sample Excel workbook could not be generated");
        }
    }

    private WorkImportPreview parse(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("Choose a non-empty Excel workbook");
        }

        try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
            if (workbook.getNumberOfSheets() == 0) {
                throw new BusinessException("The workbook does not contain a worksheet");
            }
            Sheet sheet = workbook.getSheetAt(0);
            if (sheet.getLastRowNum() > MAX_ROWS) {
                throw new BusinessException("The workbook cannot contain more than 10,000 data rows");
            }
            return parseSheet(sheet, workbook.getCreationHelper().createFormulaEvaluator());
        } catch (IOException exception) {
            throw new BusinessException("The Excel workbook could not be read");
        } catch (EncryptedDocumentException exception) {
            throw new BusinessException("Password-protected workbooks are not supported");
        }
    }

    private WorkImportPreview parseSheet(Sheet sheet, FormulaEvaluator evaluator) {
        Row header = sheet.getRow(sheet.getFirstRowNum());
        if (header == null) {
            throw new BusinessException("The Excel workbook is missing its header row");
        }

        DataFormatter formatter = new DataFormatter(Locale.ROOT);
        Map<String, Integer> columns = new HashMap<>();
        for (Cell cell : header) {
            columns.put(normalize(formatter.formatCellValue(cell, evaluator)), cell.getColumnIndex());
        }
        requireColumn(columns, "workdetails");
        requireColumn(columns, "contractorname");
        requireColumn(columns, "stipulatedstartdate");
        requireColumn(columns, "stipulatedenddate");

        List<WorkImportRow> validRows = new ArrayList<>();
        List<WorkImportError> errors = new ArrayList<>();
        for (int index = header.getRowNum() + 1; index <= sheet.getLastRowNum(); index++) {
            Row row = sheet.getRow(index);
            if (row == null || isBlank(row, formatter, evaluator)) {
                continue;
            }
            int rowNumber = index + 1;
            try {
                String details = readText(row, columns.get("workdetails"), formatter, evaluator);
                String contractor = readText(row, columns.get("contractorname"), formatter, evaluator);
                LocalDate actualStart = readDate(
                        row, columns.getOrDefault("actualstartdate", -1), formatter, evaluator);
                LocalDate stipulatedStart = readDate(
                        row, columns.get("stipulatedstartdate"), formatter, evaluator);
                LocalDate stipulatedEnd = readDate(
                        row, columns.get("stipulatedenddate"), formatter, evaluator);
                validateRow(details, contractor, stipulatedStart, stipulatedEnd);
                validRows.add(new WorkImportRow(
                        rowNumber, details.trim(), actualStart, contractor.trim(), stipulatedStart, stipulatedEnd));
            } catch (IllegalArgumentException exception) {
                errors.add(new WorkImportError(rowNumber, exception.getMessage()));
            }
        }
        return new WorkImportPreview(List.copyOf(validRows), List.copyOf(errors));
    }

    private void validateRow(
            String details, String contractor, LocalDate stipulatedStart, LocalDate stipulatedEnd) {
        if (details.isBlank()) {
            throw new IllegalArgumentException("Work Details is required");
        }
        if (details.trim().length() > 500) {
            throw new IllegalArgumentException("Work Details cannot exceed 500 characters");
        }
        if (contractor.isBlank()) {
            throw new IllegalArgumentException("Contractor Name is required");
        }
        if (contractor.trim().length() > 150) {
            throw new IllegalArgumentException("Contractor Name cannot exceed 150 characters");
        }
        if (stipulatedStart == null) {
            throw new IllegalArgumentException("Stipulated Start Date is required");
        }
        if (stipulatedEnd == null) {
            throw new IllegalArgumentException("Stipulated End Date is required");
        }
        if (stipulatedEnd.isBefore(stipulatedStart)) {
            throw new IllegalArgumentException("Stipulated End Date must be on or after Start Date");
        }
    }

    private LocalDate readDate(Row row, int column, DataFormatter formatter, FormulaEvaluator evaluator) {
        if (column < 0) {
            return null;
        }
        Cell cell = row.getCell(column);
        if (cell == null || cell.getCellType() == CellType.BLANK) {
            return null;
        }

        if (cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
            return DateUtil.getLocalDateTime(cell.getNumericCellValue()).toLocalDate();
        }
        if (cell.getCellType() == CellType.FORMULA) {
            var value = evaluator.evaluate(cell);
            if (value != null && value.getCellType() == CellType.NUMERIC
                    && DateUtil.isCellDateFormatted(cell)) {
                return DateUtil.getLocalDateTime(value.getNumberValue()).toLocalDate();
            }
        }

        String value = formatter.formatCellValue(cell, evaluator).trim();
        if (value.isEmpty()) {
            return null;
        }
        for (DateTimeFormatter dateFormat : TEXT_DATE_FORMATS) {
            try {
                return LocalDate.parse(value, dateFormat);
            } catch (DateTimeParseException ignored) {
                // Try the next accepted spreadsheet date format.
            }
        }
        throw new IllegalArgumentException("Date values must be formatted as yyyy-MM-dd or d-M-yyyy");
    }

    private String readText(Row row, int column, DataFormatter formatter, FormulaEvaluator evaluator) {
        Cell cell = row.getCell(column);
        return cell == null ? "" : formatter.formatCellValue(cell, evaluator).trim();
    }

    private boolean isBlank(Row row, DataFormatter formatter, FormulaEvaluator evaluator) {
        for (Cell cell : row) {
            if (!formatter.formatCellValue(cell, evaluator).isBlank()) {
                return false;
            }
        }
        return true;
    }

    private String normalize(String header) {
        return header.toLowerCase(Locale.ROOT).replaceAll("[\\s_-]+", "");
    }

    private void requireColumn(Map<String, Integer> columns, String name) {
        if (!columns.containsKey(name)) {
            throw new BusinessException("The Excel header row is missing the required column: " + name);
        }
    }

    private WorkCreateRequest toRequest(WorkImportRow row) {
        WorkCreateRequest request = new WorkCreateRequest();
        request.setWorkDetails(row.workDetails());
        request.setContractorName(row.contractorName());
        request.setActualStartDate(row.actualStartDate());
        request.setStipulatedStartDate(row.stipulatedStartDate());
        request.setStipulatedEndDate(row.stipulatedEndDate());
        return request;
    }
}
