package com.oasisnourish.config;

import io.github.cdimascio.dotenv.Dotenv;

public class AuthTokenConfig extends ConfigLoader {

    private final int maxTokensPerWindow;
    private final int rateLimitWindow;
    private final int tokenExpires;

    public AuthTokenConfig(Dotenv dotenv) {
        super(dotenv);
        maxTokensPerWindow = getEnvVarInt("AUTH_MAX_TOKENS_PER_DAY", 3);
        rateLimitWindow = getEnvVarInt("AUTH_TOKEN_RATE_LIMIT_WINDOW", 24 * 60 * 60);
        tokenExpires = getEnvVarInt("AUTH_TOKEN_EXPIRES", 30 * 60);
    }

    public int getMaxTokensPerWindow() {
        return maxTokensPerWindow;
    }

    public int getRateLimitWindow() {
        return rateLimitWindow;
    }

    public int getTokenExpires() {
        return tokenExpires;
    }

}
