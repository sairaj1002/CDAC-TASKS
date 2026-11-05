package in.gov.wamis.procurement.pretender;

import java.time.LocalDateTime;

/**
 * A Pre-Tender document as shown in the documents dialog: its details and whether its file is stored.
 */
public class PreTenderDocumentResponseForm {

    private Long id;
    private Long docUniqueId;
    private String docCode;
    private String docName;
    private String docDescription;
    private String docMimeType;
    private Long docSize;
    private Integer docPriority;
    private Boolean uploaded;
    private LocalDateTime uploadedAt;

    public static PreTenderDocumentResponseForm fromEntity(PreTenderDocument doc, boolean fileStored) {
        PreTenderDocumentResponseForm form = new PreTenderDocumentResponseForm();
        form.setId(doc.getId());
        form.setDocUniqueId(doc.getDocUniqueId());
        form.setDocCode(doc.getDocCode());
        form.setDocName(doc.getDocName());
        form.setDocDescription(doc.getDocDescription());
        form.setDocMimeType(doc.getDocMimeType());
        form.setDocSize(doc.getDocSize());
        form.setDocPriority(doc.getDocPriority());
        form.setUploaded(fileStored);
        form.setUploadedAt(doc.getCreatedAt());
        return form;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Boolean getUploaded() {
        return uploaded;
    }

    public void setUploaded(Boolean uploaded) {
        this.uploaded = uploaded;
    }

    public LocalDateTime getUploadedAt() {
        return uploadedAt;
    }

    public void setUploadedAt(LocalDateTime uploadedAt) {
        this.uploadedAt = uploadedAt;
    }
}
