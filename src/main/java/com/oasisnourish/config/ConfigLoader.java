package com.oasisnourish.config;

import io.github.cdimascio.dotenv.Dotenv;

public abstract class ConfigLoader {

    protected final Dotenv dotenv;

    public ConfigLoader(Dotenv dotenv) {
        this.dotenv = dotenv;
    }

    protected String getEnvVar(String key, String defaultValue) {
        return dotenv.get(key, defaultValue);
    }

    protected int getEnvVarInt(String key, int defaultValue) {
        String value = dotenv.get(key);
        if (value != null) {
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException ex) {
                throw new IllegalArgumentException("Invalid number format for " + key);
            }
        }
        return defaultValue;
    }
}
