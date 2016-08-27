package me.corriekay.pokegoutil.DATA.models;

public class LoginResult {
    private boolean success;
    private String errorMessage;

    public LoginResult() {
        success = true;
    }

    public LoginResult(String errorMessage) {
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
