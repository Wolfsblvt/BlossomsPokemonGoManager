package me.corriekay.pokegoutil.DATA.models;

import me.corriekay.pokegoutil.DATA.enums.LoginType;

public class LoginData {

    private String token;
    private String username;
    private String password;
    private LoginType loginType;
    private boolean savedToken;

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

    public boolean hasPassword(){
        return password != null;
    }

    public boolean hasToken(){
        return token != null;
    }

    public boolean hasUsername(){
        return username != null;
    }

    public boolean isSavedToken() {
        return savedToken;
    }

    public void setLoginType(LoginType loginType) {
        this.loginType = loginType;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    
    public void setSavedToken(boolean savedToken) {
        this.savedToken = savedToken;
    }
    
    public void setToken(String token) {
        this.token = token;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
}
