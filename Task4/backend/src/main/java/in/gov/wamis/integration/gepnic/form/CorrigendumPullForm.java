package in.gov.wamis.integration.gepnic.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * Validated input for pulling tender corrigendum information by published date.
 */
public class CorrigendumPullForm {

    @NotBlank(message = "Published date is required")
    @Pattern(regexp = "^\\d{8}$", message = "Published date must be in yyyyMMdd format (e.g. 20261102)")
    private String publishedDate;

    public CorrigendumPullForm() {
    }

    public CorrigendumPullForm(String publishedDate) {
        this.publishedDate = publishedDate;
    }

    public String getPublishedDate() {
        return publishedDate;
    }

    public void setPublishedDate(String publishedDate) {
        this.publishedDate = publishedDate;
    }
}

