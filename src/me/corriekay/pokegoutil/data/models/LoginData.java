package me.corriekay.pokegoutil.data.models;

import me.corriekay.pokegoutil.data.enums.LoginType;

public class LoginData {

    private String token;
    private String username;
    private String password;
    private LoginType loginType;
    private boolean isSavedToken;

    public LoginData() {
        this.loginType = LoginType.NONE;
    }

    public LoginData(String token) {
        this.token = token;
        this.loginType = LoginType.GOOGLE_AUTH;
    }

    public LoginData(String username, String password) {
        this.username = username;
        this.password = password;
        this.loginType = LoginType.PTC;
    }

    public LoginData(String username, String password, String token) {
        this.username = username;
        this.password = password;
        this.token = token;
        this.loginType = LoginType.ALL;
    }

    public LoginType getLoginType() {
        return loginType;
    }

    public String getPassword() {
        return password;
    }

    public String getToken() {
        return token;
    }

    public String getUsername() {
        return username;
    }

    public boolean hasPassword() {
        return password != null && password.length() > 0;
    }

    public boolean hasSavedCredentials() {
        return isValidPtcLogin() || isValidGoogleLogin();
    }

    public boolean hasToken() {
        return token != null && token.length() > 0;
    }

    public boolean hasUsername() {
        return username != null && username.length() > 0;
    }

    public boolean isSavedToken() {
        return isSavedToken;
    }

    public boolean isValidGoogleLogin() {
        return hasToken();
    }

    public boolean isValidPtcLogin() {
        return hasUsername() && hasPassword();
    }

    public void setLoginType(LoginType loginType) {
        this.loginType = loginType;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setSavedToken(boolean isSavedToken) {
        this.isSavedToken = isSavedToken;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        return String.format("Username: %s | Password: %s | Token: %s | LoginType: %s | isSavedToken %b",
                username, password, token, loginType, isSavedToken);
    }
}
