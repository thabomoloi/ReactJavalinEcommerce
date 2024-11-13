package com.oasisnourish.util.jwt;

import java.time.Instant;
import java.util.Optional;

import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.oasisnourish.config.JWTConfig;
import com.oasisnourish.models.JsonWebToken;
import com.oasisnourish.models.User;

public class JWTProvider {

    private final Algorithm algorithm;
    private final JWTGenerator generator;
    private final JWTVerifier verifier;
    private final JWTConfig config;
    private Instant jwtCurrentTime;
    private Instant jwtMaxExpiryTime;

    public JWTProvider(Algorithm algorithm, JWTGenerator generator, JWTVerifier verifier, JWTConfig config) {
        this.algorithm = algorithm;
        this.generator = generator;
        this.verifier = verifier;
        this.config = config;
        jwtCurrentTime = Instant.now();
        jwtMaxExpiryTime = jwtCurrentTime.plusSeconds(config.getJwtTokenMaxExpires());
    }

    public JsonWebToken generateToken(User user, String tokenType, long tokenVersion) {
        Instant jwtTokenExpires = switch (tokenType.toLowerCase()) {
            case "access" ->
                jwtCurrentTime.plusSeconds(config.getJwtAccessTokenExpires());
            case "refresh" ->
                jwtCurrentTime.plusSeconds(config.getJwtRefreshTokenExpires());
            default ->
                jwtCurrentTime.plusSeconds(0);
        };

        if (jwtTokenExpires.isAfter(jwtMaxExpiryTime)) {
            jwtTokenExpires = jwtMaxExpiryTime;
        }

        String token = generator.generate(user, algorithm, tokenType, tokenVersion, jwtCurrentTime, jwtTokenExpires);
        return new JsonWebToken(token, tokenType, tokenVersion, jwtTokenExpires, user.getId());
    }

    public Optional<DecodedJWT> validateToken(String token) {
        try {
            return Optional.of(verifier.verify(token));
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
