package com.oasisnourish.db;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Interface for JDBC connection handling.
 * Provides a method to retrieve a database connection.
 */
public interface JdbcConnection {

    /**
     * Retrieves a connection to the database.
     *
     * @return a {@link Connection} object to interact with the database.
     * @throws SQLException if a database access error occurs.
     */
    Connection getConnection() throws SQLException;
}