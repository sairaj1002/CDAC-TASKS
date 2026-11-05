package in.gov.wamis.integration.gepnic.form;

/**
 * GePNIC security envelope model as defined in Section 6.2 of the NIC integration specification.
 */
public class GePNICEnvelopeForm {

    private String encryptData;
    private String signData;
    private String uniqueUserId;
    private String encryptKey;
    private String keyId;

    public GePNICEnvelopeForm() {
    }

    public GePNICEnvelopeForm(String encryptData, String signData, String uniqueUserId, String encryptKey, String keyId) {
        this.encryptData = encryptData;
        this.signData = signData;
        this.uniqueUserId = uniqueUserId;
        this.encryptKey = encryptKey;
        this.keyId = keyId;
    }

    public String getEncryptData() {
        return encryptData;
    }

    public void setEncryptData(String encryptData) {
        this.encryptData = encryptData;
    }

    public String getSignData() {
        return signData;
    }

    public void setSignData(String signData) {
        this.signData = signData;
    }

    public String getUniqueUserId() {
        return uniqueUserId;
    }

    public void setUniqueUserId(String uniqueUserId) {
        this.uniqueUserId = uniqueUserId;
    }

    public String getEncryptKey() {
        return encryptKey;
    }

    public void setEncryptKey(String encryptKey) {
        this.encryptKey = encryptKey;
    }

    public String getKeyId() {
        return keyId;
    }

    public void setKeyId(String keyId) {
        this.keyId = keyId;
    }
}

