package in.gov.wamis.integration.gepnic.form;

/**
 * External response envelope returned by GePNIC services.
 */
public class GePNICResponseForm {

    private Integer responseCode;
    private String responseMessage;

    public GePNICResponseForm() {
    }

    public GePNICResponseForm(Integer responseCode, String responseMessage) {
        this.responseCode = responseCode;
        this.responseMessage = responseMessage;
    }

    public Integer getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(Integer responseCode) {
        this.responseCode = responseCode;
    }

    public String getResponseMessage() {
        return responseMessage;
    }

    public void setResponseMessage(String responseMessage) {
        this.responseMessage = responseMessage;
    }
}

