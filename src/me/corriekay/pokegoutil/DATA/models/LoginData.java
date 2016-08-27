package me.corriekay.pokegoutil.DATA.models;

import me.corriekay.pokegoutil.DATA.enums.LoginType;

public class LoginData {

    private String token;
    private String username;
    private String password;
    private LoginType loginType;
    private boolean isSavedToken;
    
    public LoginData(){
        this.loginType = LoginType.NONE;
    }
    
    public LoginData(String token){
        this.token = token;
        this.loginType = LoginType.GOOGLE;
    }
    
    public LoginData(String username, String password){
        this.username = username;
        this.password = password;
        this.loginType = LoginType.PTC;        
    }
    
    public LoginData(String username, String password, String token){
        this.username = username;
        this.password = password;
        this.token = token;
        this.loginType = LoginType.BOTH;
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

    public boolean hasSavedCredentials(){
        return isValidPTCLogin() || isValidGoogleLogin();
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

    public boolean isValidPTCLogin() {
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
}
