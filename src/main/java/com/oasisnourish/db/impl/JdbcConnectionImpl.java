package com.oasisnourish.db.impl;

import java.sql.Connection;
import java.sql.SQLException;

import com.oasisnourish.config.EnvConfig;
import com.oasisnourish.config.JdbcDbConfig;
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
     * @throws IllegalStateException if database environment variables are not
     * set.
     */
    private static HikariDataSource setUpJdbcConnection() {
        JdbcDbConfig jdbcDbConfig = new JdbcDbConfig(dotenv);

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(jdbcDbConfig.getDbUrl());
        config.setUsername(jdbcDbConfig.getDbUsername());
        config.setPassword(jdbcDbConfig.getDbPassword());
        return new HikariDataSource(config);
    }

    @Override
    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }
}
