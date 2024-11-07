package com.example.eatathome.Rider.Model;

public class TokenRider {

    public String token;
    public boolean isServerToken;

    public TokenRider(){

    }

    public TokenRider(String token, boolean isServerToken) {
        this.token = token;
        this.isServerToken = isServerToken;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public boolean isServerToken() {
        return isServerToken;
    }

    public void setServerToken(boolean serverToken) {
        isServerToken = serverToken;
    }
}
