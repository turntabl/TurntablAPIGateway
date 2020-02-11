package io.tuntabl;

public class ValidationResponse {
    private boolean isValid;
    private String error;

    public ValidationResponse() { }

    public ValidationResponse(boolean isValid, String error) {
        this.isValid = isValid;
        this.error = error;
    }

    public boolean isValid() {
        return isValid;
    }

    public void setValid(boolean valid) {
        isValid = valid;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    @Override
    public String toString() {
        return "{ \"error\": \"" + error + "\"}" ;
    }
}
