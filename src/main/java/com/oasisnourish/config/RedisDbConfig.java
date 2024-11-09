package com.oasisnourish.config;

import io.github.cdimascio.dotenv.Dotenv;

public class RedisDbConfig extends ConfigLoader {

    private final String host;
    private final int port;

    public RedisDbConfig(Dotenv dotenv) {
        super(dotenv);
        host = getEnvVar("REDIS_HOST", null);
        port = getEnvVarInt("REDIS_PORT", 6379);
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }
}
