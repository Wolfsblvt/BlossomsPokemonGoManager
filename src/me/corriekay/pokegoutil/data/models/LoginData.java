package me.corriekay.pokegoutil.data.models;

import me.corriekay.pokegoutil.data.enums.LoginType;

public class LoginData {

    private String token;
    private String username;
    private String password;
    private String googleUsername;
    private String googlePassword;
    private String hashKey;
    private LoginType loginType;
    private boolean isSavedToken;

    public LoginData() {
        this.loginType = LoginType.NONE;
    }

    public LoginData(final String token) {
        this.token = token;
        this.loginType = LoginType.GOOGLE_AUTH;
    }

    public LoginData(final String username, final String password) {
        this.username = username;
        this.password = password;
        this.loginType = LoginType.PTC;
    }

    public LoginData(final String username, final String password, final String token) {
        this.username = username;
        this.password = password;
        this.token = token;
        this.loginType = LoginType.ALL;
    }
    
    public LoginData(final String username, final String password, final String token, final String googleUsername, final String googlePassword, final String hashKey) {
        this.username = username;
        this.password = password;
        this.token = token;
        this.googleUsername = googleUsername;
        this.googlePassword = googlePassword;
        this.hashKey = hashKey;
        this.loginType = LoginType.ALL;
    }

    public LoginType getLoginType() {
        return loginType;
    }

    public String getPassword() {
        return password;
    }
    
    public String getGooglePassword() {
        return googlePassword;
    }

    public String getToken() {
        return token;
    }

    public String getUsername() {
        return username;
    }
    
    public String getGoogleUsername() {
        return googleUsername;
    }
    
    public String getHashKey() {
        return hashKey;
    }

    public boolean hasPassword() {
        return password != null && password.length() > 0;
    }
    
    public boolean hasGooglePassword() {
        return googlePassword != null && googlePassword.length() > 0;
    }

    public boolean hasSavedCredentials() {
        return isValidPtcLogin() || isValidGoogleLogin() || isValidGoogleAppLogin();
    }

    public boolean hasToken() {
        return token != null && token.length() > 0;
    }

    public boolean hasUsername() {
        return username != null && username.length() > 0;
    }
    
    public boolean hasGoogleUsername() {
        return googleUsername != null && googleUsername.length() > 0;
    }
    
    public boolean hasHashKey() {
        return hashKey != null && hashKey.length() > 0;
    }

    public boolean isSavedToken() {
        return isSavedToken;
    }

    public boolean isValidGoogleLogin() {
        return hasToken();
    }
    
    public boolean isValidGoogleAppLogin() {
        return hasGoogleUsername() && hasGooglePassword();
    }

    public boolean isValidPtcLogin() {
        return hasUsername() && hasPassword();
    }

    public void setLoginType(final LoginType loginType) {
        this.loginType = loginType;
    }

    public void setPassword(final String password) {
        this.password = password;
    }
    
    public void setGooglePassword(final String passwordParam) {
        this.googlePassword = passwordParam;
    }

    public void setSavedToken(final boolean savedToken) {
        this.isSavedToken = savedToken;
    }

    public void setToken(final String token) {
        this.token = token;
    }

    public void setUsername(final String username) {
        this.username = username;
    }
    
    public void setGoogleUsername(final String usernameParam) {
        this.googleUsername = usernameParam;
    }

    public void setHashKey(final String hashKey) {
        this.hashKey = hashKey;
    }
    
    @Override
    public String toString() {
        return String.format("Username: %s | Password: %s | Token: %s | LoginType: %s | isSavedToken: %b",
                username, password, token, loginType, isSavedToken);
    }

}
