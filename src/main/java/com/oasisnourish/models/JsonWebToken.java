package com.oasisnourish.models;

import java.time.Instant;

public class JsonWebToken extends Token {

    public JsonWebToken(String token, String tokenType, long tokenVersion, Instant expires, int userId) {
        super(token, "jwt", tokenType, tokenVersion, expires, userId);
    }

}
