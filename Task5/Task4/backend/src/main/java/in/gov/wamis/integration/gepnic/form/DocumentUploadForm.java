package in.gov.wamis.integration.gepnic.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.web.multipart.MultipartFile;

/**
 * Validated form for uploading tender documents (NIT, BOQ, Additional Documents) to GePNIC.
 * As defined in Section 6.4 of the development specification.
 */
public class DocumentUploadForm {

    private String uniqueUserId;

    @NotBlank(message = "Unique file number is required")
    private String uniqueFileNo;

    @NotNull(message = "Document unique ID is required")
    @Positive(message = "Document unique ID must be positive")
    private Long docuniqueid;

    private MultipartFile file;

    public DocumentUploadForm() {
    }

    public DocumentUploadForm(String uniqueUserId, String uniqueFileNo, Long docuniqueid, MultipartFile file) {
        this.uniqueUserId = uniqueUserId;
        this.uniqueFileNo = uniqueFileNo;
        this.docuniqueid = docuniqueid;
        this.file = file;
    }

    public String getUniqueUserId() {
        return uniqueUserId;
    }

    public void setUniqueUserId(String uniqueUserId) {
        this.uniqueUserId = uniqueUserId;
    }

    public String getUniqueFileNo() {
        return uniqueFileNo;
    }

    public void setUniqueFileNo(String uniqueFileNo) {
        this.uniqueFileNo = uniqueFileNo;
    }

    public Long getDocuniqueid() {
        return docuniqueid;
    }

    public void setDocuniqueid(Long docuniqueid) {
        this.docuniqueid = docuniqueid;
    }

    public MultipartFile getFile() {
        return file;
    }

    public void setFile(MultipartFile file) {
        this.file = file;
    }
}

