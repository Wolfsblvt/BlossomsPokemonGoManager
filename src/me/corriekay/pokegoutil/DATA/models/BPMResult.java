package me.corriekay.pokegoutil.DATA.models;

public class BpmResult {
    private boolean success;
    private String errorMessage;

    public BpmResult() {
        success = true;
    }

    public BpmResult(String errorMessage) {
        success = false;
        this.errorMessage = errorMessage;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
