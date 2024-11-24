package com.oasisnourish.models.tokens;

import java.time.Instant;

import com.oasisnourish.enums.Tokens;

public class JsonWebToken extends Token {

    public JsonWebToken(String token, Tokens.Jwt tokenType, long tokenVersion, Instant expires, int userId) {
        super(token, Tokens.Category.JWT, tokenType, tokenVersion, expires, userId);
    }

}
