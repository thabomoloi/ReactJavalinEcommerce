package com.oasisnourish.models;

public class JWTToken {

    private final String token;
    private final String tokenType;
    private final long tokenVersion;
    private final long expires;

    public JWTToken(String token, String tokenType, long tokenVersion, long expires) {
        this.token = token;
        this.tokenType = tokenType;
        this.tokenVersion = tokenVersion;
        this.expires = expires;
    }

    public String getToken() {
        return token;
    }

    public String getTokenType() {
        return tokenType;
    }

    public long getTokenVersion() {
        return tokenVersion;
    }

    public long getExpires() {
        return expires;
    }

}
