package com.oasisnourish.config;

import io.github.cdimascio.dotenv.Dotenv;

public class JWTConfig extends ConfigLoader {

    private final String jwtSecret;
    private final int jwtAccessTokenExpires;
    private final int jwtRefreshTokenExpires;
    private final int jwtTokenMaxExpires;

    public JWTConfig(Dotenv dotenv) {
        super(dotenv);
        this.jwtSecret = getEnvVar("JWT_SECRET", null);
        if (jwtSecret == null) {
            throw new IllegalArgumentException("Environment variable JWT_SECRET is required but not set.");
        }

        this.jwtAccessTokenExpires = getEnvVarInt("JWT_ACCESS_TOKEN_EXPIRES", 3600);
        this.jwtRefreshTokenExpires = getEnvVarInt("JWT_REFRESH_TOKEN_EXPIRES", 86400);
        this.jwtTokenMaxExpires = getEnvVarInt("JWT_REQUIRE_FRESH_SIGN_IN", 259200);
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
}
