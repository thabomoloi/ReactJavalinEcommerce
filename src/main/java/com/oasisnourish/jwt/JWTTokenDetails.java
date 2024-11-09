package com.oasisnourish.jwt;

public class JWTTokenDetails {

    private final String token;
    private final String tokenType;
    private final long tokenVersion;
    private final long expires;
    private final long maxExpires;

    public JWTTokenDetails(String token, String tokenType, long tokenVersion, long expires, long maxExpires) {
        this.token = token;
        this.tokenType = tokenType;
        this.tokenVersion = tokenVersion;
        this.expires = expires;
        this.maxExpires = maxExpires;
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

    public long getMaxExpires() {
        return maxExpires;
    }

}
