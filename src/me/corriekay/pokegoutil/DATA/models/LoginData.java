package me.corriekay.pokegoutil.DATA.models;

public class LoginData {

    private String token;
    private String username;
    private String password;

    public String getPassword() {
        return password;
    }

    public String getToken() {
        return token;
    }

    public String getUsername() {
        return username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setUsername(String username) {
        this.username = username;
    }
    
    public boolean hasUsername(){
        return username != null;
    }
    
    public boolean hasPassword(){
        return password != null;
    }
    public boolean hasToken(){
        return token != null;
    }
}
