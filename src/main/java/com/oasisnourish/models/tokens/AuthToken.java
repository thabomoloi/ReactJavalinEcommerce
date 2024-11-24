package com.oasisnourish.models.tokens;

import java.time.Instant;

import com.oasisnourish.enums.Tokens;

public class AuthToken extends Token {

    public AuthToken(String token, Tokens.Auth tokenType, long tokenVersion, Instant expires, int userId) {
        super(token, Tokens.Category.AUTH, tokenType, tokenVersion, expires, userId);
    }
}
