package com.oasisnourish.util.jwt;

import java.time.Instant;
import java.util.UUID;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import com.oasisnourish.models.User;

public class JWTGenerator {

    public String generate(User user, Algorithm algorithm, String tokenType, long tokenVersion, Instant issuedAt, Instant expiresAt) {
        JWTCreator.Builder token = JWT.create()
                .withJWTId(UUID.randomUUID().toString())
                .withIssuedAt(issuedAt)
                .withExpiresAt(expiresAt)
                .withClaim("version", tokenVersion)
                .withClaim("type", tokenType)
                .withClaim("userId", user.getId())
                .withClaim("role", user.getRole().name().toLowerCase());
        return token.sign(algorithm);
    }
}
