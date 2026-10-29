package com.cdac.egov.integration.gepnic.forms.encryption;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Standard encrypted envelope defined by the GePNIC integration specification.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EncryptedEnvelopeForm {

    @NotBlank(message = "uniqueUserId must not be blank")
    private String uniqueUserId;

    @NotBlank(message = "encryptData must not be blank")
    private String encryptData;

    @NotBlank(message = "signData must not be blank")
    private String signData;

    @NotBlank(message = "encryptKey must not be blank")
    private String encryptKey;

    @NotBlank(message = "keyId must not be blank")
    private String keyId;
}
