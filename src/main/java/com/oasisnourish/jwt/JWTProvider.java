package com.oasisnourish.jwt;

import java.util.Optional;

import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.oasisnourish.config.JWTConfig;
import com.oasisnourish.models.User;

public class JWTProvider {

    private final Algorithm algorithm;
    private final JWTGenerator generator;
    private final JWTVerifier verifier;
    private final JWTConfig config;
    private long jwtCurrentTime;
    private long jwtMaxExpiryTime;

    public JWTProvider(Algorithm algorithm, JWTGenerator generator, JWTVerifier verifier, JWTConfig config) {
        this.algorithm = algorithm;
        this.generator = generator;
        this.verifier = verifier;
        this.config = config;
        jwtCurrentTime = 0;
        jwtMaxExpiryTime = 0;
    }

    public JWTTokenDetails generateToken(User user, String tokenType, long tokenVersion) {
        long jwtTokenExpires = switch (tokenType) {
            case "access" ->
                config.getJwtAccessTokenExpires() * 1000L;
            case "refresh" ->
                config.getJwtTokenMaxExpires() * 1000L;
            default ->
                0;
        };

        jwtTokenExpires = Math.min(jwtTokenExpires, jwtMaxExpiryTime);

        String token = generator.generate(user, algorithm, tokenType, tokenVersion, jwtCurrentTime, jwtTokenExpires);
        return new JWTTokenDetails(token, tokenType, tokenVersion, jwtTokenExpires, jwtTokenExpires);
    }

    public Optional<DecodedJWT> validateToken(String token) {
        try {
            return Optional.of(verifier.verify(token));
        } catch (JWTVerificationException ex) {
            return Optional.empty();
        }
    }

    public void updateJwtCurrentTime() {
        jwtCurrentTime = System.currentTimeMillis();
    }

    public void updateJwtMaxExpiryTime() {
        jwtMaxExpiryTime = jwtCurrentTime + config.getJwtTokenMaxExpires() * 1000;
    }
}
