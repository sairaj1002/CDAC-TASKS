package in.gov.wamis.procurement.pretender;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public class PreTenderDocumentForm {

    @NotNull(message = "Document unique ID is required")
    @Positive(message = "Document unique ID must be positive")
    private Long docUniqueId;

    @NotBlank(message = "Document code is required (e.g. NITDOC, BOQDOC)")
    @Size(max = 50, message = "Document code cannot exceed 50 characters")
    private String docCode;

    @NotBlank(message = "Document name is required")
    @Size(max = 255, message = "Document name cannot exceed 255 characters")
    private String docName;

    @Size(max = 1000, message = "Document description cannot exceed 1000 characters")
    private String docDescription;

    @NotBlank(message = "Document MIME type / extension is required (e.g. .pdf, .xls)")
    @Size(max = 50, message = "Document MIME type cannot exceed 50 characters")
    private String docMimeType;

    @NotNull(message = "Document size is required")
    @Positive(message = "Document size must be positive")
    private Long docSize;

    @NotNull(message = "Document priority is required")
    @Positive(message = "Document priority must be positive")
    private Integer docPriority;

    public PreTenderDocumentForm() {
    }

    public PreTenderDocumentForm(Long docUniqueId, String docCode, String docName, String docDescription,
                                String docMimeType, Long docSize, Integer docPriority) {
        this.docUniqueId = docUniqueId;
        this.docCode = docCode;
        this.docName = docName;
        this.docDescription = docDescription;
        this.docMimeType = docMimeType;
        this.docSize = docSize;
        this.docPriority = docPriority;
    }

    public Long getDocUniqueId() {
        return docUniqueId;
    }

    public void setDocUniqueId(Long docUniqueId) {
        this.docUniqueId = docUniqueId;
    }

    public String getDocCode() {
        return docCode;
    }

    public void setDocCode(String docCode) {
        this.docCode = docCode;
    }

    public String getDocName() {
        return docName;
    }

    public void setDocName(String docName) {
        this.docName = docName;
    }

    public String getDocDescription() {
        return docDescription;
    }

    public void setDocDescription(String docDescription) {
        this.docDescription = docDescription;
    }

    public String getDocMimeType() {
        return docMimeType;
    }

    public void setDocMimeType(String docMimeType) {
        this.docMimeType = docMimeType;
    }

    public Long getDocSize() {
        return docSize;
    }

    public void setDocSize(Long docSize) {
        this.docSize = docSize;
    }

    public Integer getDocPriority() {
        return docPriority;
    }

    public void setDocPriority(Integer docPriority) {
        this.docPriority = docPriority;
    }
}

