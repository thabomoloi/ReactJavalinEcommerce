package com.oasisnourish.models;

public class AuthToken extends Token {

    public AuthToken(String token, String tokenType, long tokenVersion, long expires, int userId) {
        super(token, "auth", tokenType, tokenVersion, expires, userId);
    }
}
