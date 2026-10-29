package com.cdac.egov.integration.gepnic.forms.aoc;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request form to query Award of Contract (AOC) data by created date.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AocPullForm {

    @NotBlank(message = "createdDate must not be blank (format: yyyyMMdd)")
    private String createdDate;

    private String tenderReference;
}
