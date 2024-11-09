package com.oasisnourish.models;

public class AuthToken {
    private final String token;
    private final int userId;
    private final long expires;
    private final String tokenType; 

    public AuthToken(String token, int userId, long expires, String tokenType) {
        this.token = token;
        this.userId = userId;
        this.expires = expires;
        this.tokenType = tokenType;
    }

    public String getToken() {
        return token;
    }

    public int getUserId() {
        return userId;
    }

    public long getExpires() {
        return expires;
    }

    public String getTokenType() {
        return tokenType;
    }
}
