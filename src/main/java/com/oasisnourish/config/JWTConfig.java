package com.oasisnourish.config;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;

import io.github.cdimascio.dotenv.Dotenv;

public class JWTConfig extends ConfigLoader {

    private final String jwtSecret;
    private final int jwtAccessTokenExpires;
    private final int jwtRefreshTokenExpires;
    private final int jwtTokenMaxExpires;
    private final Algorithm algorithm;
    private final JWTVerifier verifier;

    public JWTConfig(Dotenv dotenv) {
        super(dotenv);
        jwtSecret = getEnvVar("JWT_SECRET", null);
        if (jwtSecret == null) {
            throw new IllegalArgumentException("Environment variable JWT_SECRET is required but not set.");
        }

        jwtAccessTokenExpires = getEnvVarInt("JWT_ACCESS_TOKEN_EXPIRES", 3600);
        jwtRefreshTokenExpires = getEnvVarInt("JWT_REFRESH_TOKEN_EXPIRES", 86400);
        jwtTokenMaxExpires = getEnvVarInt("JWT_REQUIRE_FRESH_SIGN_IN", 259200);
        algorithm = Algorithm.HMAC256(jwtSecret);
        verifier = JWT.require(algorithm).build();
    }

    public String getJwtSecret() {
        return jwtSecret;
    }

    public int getJwtAccessTokenExpires() {
        return jwtAccessTokenExpires;
    }

    public int getJwtRefreshTokenExpires() {
        return jwtRefreshTokenExpires;
    }

    public int getJwtTokenMaxExpires() {
        return jwtTokenMaxExpires;
    }

    public JWTVerifier getVerifier() {
        return verifier;
    }

    public Algorithm getAlgorithm() {
        return algorithm;
    }
}
