package com.oasisnourish.models;

public abstract class Token {

    private final String token;
    private final long tokenVersion;
    private final long expires;
    private final String tokenType;
    private final String tokenCategory;
    private final int userId;

    public Token(String token, String tokenCategory, String tokenType, long tokenVersion, long expires, int userId) {
        this.expires = expires;
        this.token = token;
        this.tokenType = tokenType;
        this.tokenVersion = tokenVersion;
        this.tokenCategory = tokenCategory;
        this.userId = userId;
    }

    public String getToken() {
        return token;
    }

    public long getTokenVersion() {
        return tokenVersion;
    }

    public long getExpires() {
        return expires;
    }

    public String getTokenType() {
        return tokenType;
    }

    public String getTokenCategory() {
        return tokenCategory;
    }

    public int getUserId() {
        return userId;
    }

}
