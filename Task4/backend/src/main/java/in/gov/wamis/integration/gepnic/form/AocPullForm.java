package in.gov.wamis.integration.gepnic.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * Validated input for pulling Award of Contract (AOC) information by created date.
 */
public class AocPullForm {

    @NotBlank(message = "Created date is required")
    @Pattern(regexp = "^\\d{8}$", message = "Created date must be in yyyyMMdd format (e.g. 20261102)")
    private String createdDate;

    public AocPullForm() {
    }

    public AocPullForm(String createdDate) {
        this.createdDate = createdDate;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }
}

