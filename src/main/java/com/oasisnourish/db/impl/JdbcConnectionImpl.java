package com.oasisnourish.db.impl;

import java.sql.Connection;
import java.sql.SQLException;

import com.oasisnourish.config.EnvConfig;
import com.oasisnourish.db.JdbcConnection;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import io.github.cdimascio.dotenv.Dotenv;

/**
 * Implementation of {@link JdbcConnection} using HikariCP for database
 * connections. This class handles the setup and retrieval of database
 * connections.
 */
public class JdbcConnectionImpl implements JdbcConnection {
    private static final Dotenv dotenv = EnvConfig.getDotenv();
    private static final HikariDataSource dataSource;

    static {
        dataSource = setUpJdbcConnection();
    }

    /**
     * Sets up the JDBC connection using HikariCP configuration.
     *
     * @return a {@link HikariDataSource} configured for the database.
     * @throws IllegalStateException if database environment variables are not set.
     */
    private static HikariDataSource setUpJdbcConnection() {
        String url = dotenv.get("DB_URL");
        String username = dotenv.get("DB_USERNAME");
        String password = dotenv.get("DB_PASSWORD");

        if (url == null || username == null || password == null) {
            throw new IllegalStateException("Database environment variables are not set.");
        }

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(url);
        config.setUsername(username);
        config.setPassword(password);
        return new HikariDataSource(config);
    }

    @Override
    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }
}
