package com.oasisnourish.models;

public class JsonWebToken extends Token {

    public JsonWebToken(String token, String tokenType, long tokenVersion, long expires, int userId) {
        super(token, "jwt", tokenType, tokenVersion, expires, userId);
    }

}
