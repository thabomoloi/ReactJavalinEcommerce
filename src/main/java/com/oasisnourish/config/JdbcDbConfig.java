package com.oasisnourish.config;

import io.github.cdimascio.dotenv.Dotenv;

public class JdbcDbConfig extends ConfigLoader {

    private final String dbUrl;
    private final String dbUsername;
    private final String dbPassword;

    public JdbcDbConfig(Dotenv dotenv) {
        super(dotenv);
        this.dbUrl = getEnvVar("POSTGRES_DB_URL", null);
        if (dbUrl == null) {
            throw new IllegalArgumentException("Environment variable DB_URL is required but not set.");
        }

        this.dbUsername = getEnvVar("POSTGRES_USER", null);
        this.dbPassword = getEnvVar("POSTGRES_PASSWORD", null);
    }

    public String getDbUrl() {
        return dbUrl;
    }

    public String getDbUsername() {
        return dbUsername;
    }

    public String getDbPassword() {
        return dbPassword;
    }
}
