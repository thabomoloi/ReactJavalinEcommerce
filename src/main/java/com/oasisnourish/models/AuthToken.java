package com.oasisnourish.models;

import java.time.Instant;

public class AuthToken extends Token {

    public AuthToken(String token, String tokenType, long tokenVersion, Instant expires, int userId) {
        super(token, "auth", tokenType, tokenVersion, expires, userId);
    }
}
