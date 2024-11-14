package com.oasisnourish.util.jwt;

import java.time.Instant;
import java.util.Optional;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.oasisnourish.config.JWTConfig;
import com.oasisnourish.enums.Tokens;
import com.oasisnourish.models.JsonWebToken;
import com.oasisnourish.models.User;

public class JWTProvider {

    private final JWTGenerator generator;
    private final JWTConfig config;
    private Instant jwtCurrentTime;
    private Instant jwtMaxExpiryTime;

    public JWTProvider(JWTGenerator generator, JWTConfig config) {
        this.generator = generator;
        this.config = config;
        jwtCurrentTime = Instant.now();
        jwtMaxExpiryTime = jwtCurrentTime.plusSeconds(config.getJwtTokenMaxExpires());
    }

    public JsonWebToken generateToken(User user, Tokens.Jwt tokenType, long tokenVersion) {
        Instant jwtTokenExpires = switch (tokenType) {
            case ACCESS_TOKEN ->
                jwtCurrentTime.plusSeconds(config.getJwtAccessTokenExpires());
            case REFRESH_TOKEN ->
                jwtCurrentTime.plusSeconds(config.getJwtRefreshTokenExpires());
        };

        if (jwtTokenExpires.isAfter(jwtMaxExpiryTime)) {
            jwtTokenExpires = jwtMaxExpiryTime;
        }

        String token = generator.generate(user, config.getAlgorithm(), tokenType, tokenVersion, jwtCurrentTime, jwtTokenExpires);
        return new JsonWebToken(token, tokenType, tokenVersion, jwtTokenExpires, user.getId());
    }

    public Optional<DecodedJWT> validateToken(String token) {
        try {
            return Optional.of(config.getVerifier().verify(token));
        } catch (JWTVerificationException ex) {
            return Optional.empty();
        }
    }

    public void updateJwtCurrentTime() {
        jwtCurrentTime = Instant.now();
    }

    public void updateJwtMaxExpiryTime() {
        jwtMaxExpiryTime = jwtCurrentTime.plusSeconds(config.getJwtTokenMaxExpires());
    }

    public Instant getJwtCurrentTime() {
        return jwtCurrentTime;
    }

    public Instant getJwtMaxExpiryTime() {
        return jwtMaxExpiryTime;
    }
}
