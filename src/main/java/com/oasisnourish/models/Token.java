package com.oasisnourish.models;

import java.time.Instant;
import java.util.Objects;

import com.oasisnourish.enums.Tokens;

public abstract class Token {

    private final String token;
    private final long tokenVersion;
    private final Instant expires;
    private final Tokens.Type tokenType;
    private final Tokens.Category tokenCategory;
    private final int userId;

    public Token(String token, Tokens.Category tokenCategory, Tokens.Type tokenType, long tokenVersion, Instant expires, int userId) {
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

    public Instant getExpires() {
        return expires;
    }

    public Tokens.Type getTokenType() {
        return tokenType;
    }

    public Tokens.Category getTokenCategory() {
        return tokenCategory;
    }

    public int getUserId() {
        return userId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Token otherToken = (Token) o;
        return tokenVersion == otherToken.tokenVersion
                && userId == otherToken.userId
                && tokenType == otherToken.tokenType
                && tokenCategory == otherToken.tokenCategory
                && Objects.equals(token, otherToken.token)
                && Objects.equals(expires, otherToken.expires);
    }

    @Override
    public int hashCode() {
        return Objects.hash(token, tokenVersion, expires, tokenType, tokenCategory, userId);
    }
}
