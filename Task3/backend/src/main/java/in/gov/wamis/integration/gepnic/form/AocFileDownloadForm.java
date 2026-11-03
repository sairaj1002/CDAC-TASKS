package in.gov.wamis.integration.gepnic.form;

import jakarta.validation.constraints.NotBlank;

/**
 * Validated input for requesting AOC document file download.
 * Extracted from Section 6.8 of the NIC specification.
 */
public class AocFileDownloadForm {

    @NotBlank(message = "Document type is required (e.g. BOQ, LOA, NEGO, TIEBREAK, BOQCHART)")
    private String docType;

    @NotBlank(message = "Bid ID (bId) is required")
    private String bId;

    @NotBlank(message = "Document code (docCode) is required")
    private String docCode;

    @NotBlank(message = "File name (fileName) is required")
    private String fileName;

    public AocFileDownloadForm() {
    }

    public AocFileDownloadForm(String docType, String bId, String docCode, String fileName) {
        this.docType = docType;
        this.bId = bId;
        this.docCode = docCode;
        this.fileName = fileName;
    }

    public String getDocType() {
        return docType;
    }

    public void setDocType(String docType) {
        this.docType = docType;
    }

    public String getbId() {
        return bId;
    }

    public void setbId(String bId) {
        this.bId = bId;
    }

    public String getDocCode() {
        return docCode;
    }

    public void setDocCode(String docCode) {
        this.docCode = docCode;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}

