package com.company.enterprise.features.wms.service.impl;

import java.awt.Color;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.enterprise.core.exception.BusinessException;
import com.company.enterprise.features.wms.dto.response.WorkReportSummary;
import com.company.enterprise.features.wms.dto.response.WorkResponse;
import com.company.enterprise.features.wms.mapper.WorkMapper;
import com.company.enterprise.features.wms.repository.WorkRepository;
import com.company.enterprise.features.wms.service.WorkReportService;
import com.company.enterprise.features.wms.specification.WorkSpecification;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.design.JRDesignBand;
import net.sf.jasperreports.engine.design.JRDesignExpression;
import net.sf.jasperreports.engine.design.JRDesignField;
import net.sf.jasperreports.engine.design.JRDesignSection;
import net.sf.jasperreports.engine.design.JRDesignStaticText;
import net.sf.jasperreports.engine.design.JRDesignTextField;
import net.sf.jasperreports.engine.design.JasperDesign;

@Service
@Transactional(readOnly = true)
public class WorkReportServiceImpl implements WorkReportService {

    private static final int PAGE_WIDTH = 842;
    private static final int PAGE_HEIGHT = 595;
    private static final int[] COLUMN_WIDTHS = {55, 185, 145, 95, 105, 105, 112};
    private static final String[] HEADERS = {
        "Work No.", "Work Details", "Contractor", "Actual Start",
        "Stipulated Start", "Stipulated End", "Created"
    };
    private static final String[] FIELDS = {
        "workRegNo", "workDetails", "contractorName", "actualStartDate",
        "stipulatedStartDate", "stipulatedEndDate", "createdAt"
    };
    private static final Class<?>[] FIELD_TYPES = {
        Long.class, String.class, String.class, LocalDate.class,
        LocalDate.class, LocalDate.class, java.time.LocalDateTime.class
    };

    private final WorkRepository workRepository;
    private final WorkMapper workMapper;

    public WorkReportServiceImpl(WorkRepository workRepository, WorkMapper workMapper) {
        this.workRepository = workRepository;
        this.workMapper = workMapper;
    }

    @Override
    public List<WorkResponse> getWorkRegister(LocalDate fromDate, LocalDate toDate, String contractor) {
        validateDateRange(fromDate, toDate);
        return workRepository.findAll(
                        WorkSpecification.matchingReportFilters(fromDate, toDate, contractor))
                .stream()
                .map(workMapper::toResponse)
                .sorted(Comparator.comparing(WorkResponse::getWorkRegNo))
                .toList();
    }

    @Override
    public List<WorkResponse> getContractorDetails(LocalDate fromDate, LocalDate toDate, String contractor) {
        validateDateRange(fromDate, toDate);
        if (contractor == null || contractor.isBlank()) {
            throw new BusinessException("Contractor is required");
        }
        return workRepository.findAll(
                        WorkSpecification.matchingContractorReportDetails(fromDate, toDate, contractor))
                .stream()
                .map(workMapper::toResponse)
                .sorted(Comparator.comparing(WorkResponse::getWorkRegNo))
                .toList();
    }

    @Override
    public WorkReportSummary getSummary(LocalDate fromDate, LocalDate toDate, String contractor) {
        List<WorkResponse> works = getWorkRegister(fromDate, toDate, contractor);
        Map<String, Long> contractorCounts = works.stream()
                .collect(Collectors.groupingBy(
                        WorkResponse::getContractorName,
                        TreeMap::new,
                        Collectors.counting()));
        List<Map.Entry<String, Long>> sortedCounts = contractorCounts.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed()
                        .thenComparing(Map.Entry.comparingByKey(String.CASE_INSENSITIVE_ORDER)))
                .toList();
        return new WorkReportSummary(
                works.size(),
                sortedCounts.stream().map(Map.Entry::getKey).toList(),
                sortedCounts.stream().map(Map.Entry::getValue).toList());
    }

    @Override
    public byte[] generateWorkRegisterPdf(LocalDate fromDate, LocalDate toDate, String contractor) {
        List<WorkResponse> works = getWorkRegister(fromDate, toDate, contractor);
        try {
            JasperReport report = JasperCompileManager.compileReport(createDesign(fromDate, toDate, contractor));
            JasperPrint print = JasperFillManager.fillReport(
                    report,
                    new HashMap<>(),
                    new JRBeanCollectionDataSource(works));
            return JasperExportManager.exportReportToPdf(print);
        } catch (JRException exception) {
            throw new IllegalStateException("Unable to generate the work register PDF", exception);
        }
    }

    private JasperDesign createDesign(LocalDate fromDate, LocalDate toDate, String contractor)
            throws JRException {
        JasperDesign design = new JasperDesign();
        design.setName("work-register");
        design.setPageWidth(PAGE_WIDTH);
        design.setPageHeight(PAGE_HEIGHT);
        design.setOrientation(net.sf.jasperreports.engine.type.OrientationEnum.LANDSCAPE);
        design.setLeftMargin(20);
        design.setRightMargin(20);
        design.setTopMargin(20);
        design.setBottomMargin(20);
        design.setColumnWidth(PAGE_WIDTH - design.getLeftMargin() - design.getRightMargin());

        for (int index = 0; index < FIELDS.length; index++) {
            JRDesignField field = new JRDesignField();
            field.setName(FIELDS[index]);
            field.setValueClass(FIELD_TYPES[index]);
            design.addField(field);
        }

        JRDesignBand title = new JRDesignBand();
        title.setHeight(32);
        JRDesignStaticText titleText = new JRDesignStaticText();
        titleText.setX(0);
        titleText.setY(0);
        titleText.setWidth(design.getColumnWidth());
        titleText.setHeight(24);
        titleText.setText("Work Register" + formatFilters(fromDate, toDate, contractor));
        titleText.setBold(true);
        titleText.setFontSize(16f);
        title.addElement(titleText);
        design.setTitle(title);

        JRDesignBand header = new JRDesignBand();
        header.setHeight(24);
        JRDesignBand detail = new JRDesignBand();
        detail.setHeight(28);
        int x = 0;
        for (int index = 0; index < COLUMN_WIDTHS.length; index++) {
            JRDesignStaticText heading = new JRDesignStaticText();
            heading.setX(x);
            heading.setY(0);
            heading.setWidth(COLUMN_WIDTHS[index]);
            heading.setHeight(24);
            heading.setText(HEADERS[index]);
            heading.setBold(true);
            heading.setForecolor(Color.WHITE);
            heading.setBackcolor(new Color(18, 64, 107));
            heading.setMode(net.sf.jasperreports.engine.type.ModeEnum.OPAQUE);
            header.addElement(heading);

            JRDesignTextField value = new JRDesignTextField();
            value.setX(x);
            value.setY(0);
            value.setWidth(COLUMN_WIDTHS[index]);
            value.setHeight(28);
            value.setBlankWhenNull(true);
            value.setExpression(new JRDesignExpression("$F{" + FIELDS[index] + "}"));
            value.setFontSize(8f);
            detail.addElement(value);
            x += COLUMN_WIDTHS[index];
        }

        design.setColumnHeader(header);
        ((JRDesignSection) design.getDetailSection()).addBand(detail);
        return design;
    }

    private String formatFilters(LocalDate fromDate, LocalDate toDate, String contractor) {
        StringBuilder filters = new StringBuilder();
        if (fromDate != null || toDate != null) {
            filters.append(" (Stipulated start: ")
                    .append(fromDate == null ? "any" : fromDate)
                    .append(" to ")
                    .append(toDate == null ? "any" : toDate)
                    .append(')');
        }
        if (contractor != null && !contractor.isBlank()) {
            filters.append(" (Contractor: ").append(contractor.trim()).append(')');
        }
        return filters.toString();
    }

    private void validateDateRange(LocalDate fromDate, LocalDate toDate) {
        if (fromDate != null && toDate != null && fromDate.isAfter(toDate)) {
            throw new BusinessException("From date must be on or before to date");
        }
    }
}
