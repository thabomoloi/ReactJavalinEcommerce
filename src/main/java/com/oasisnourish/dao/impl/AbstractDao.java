package com.oasisnourish.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.oasisnourish.dao.Consumer.PreparedStatementConsumer;
import com.oasisnourish.dao.Consumer.ResultSetConsumer;
import com.oasisnourish.dao.mappers.EntityRowMapper;
import com.oasisnourish.db.JdbcConnection;
import com.oasisnourish.exceptions.DatabaseAccessException;

public abstract class AbstractDao<T> {
    protected final JdbcConnection jdbcConnection;
    protected final EntityRowMapper<T> entityRowMapper;

    public AbstractDao(JdbcConnection jdbcConnection, EntityRowMapper<T> entityRowMapper) {
        this.jdbcConnection = jdbcConnection;
        this.entityRowMapper = entityRowMapper;
    }

    protected Optional<T> querySingle(String sql, PreparedStatementConsumer consumer) {
        try (Connection connection = jdbcConnection.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)) {
            consumer.accept(ps); // Invokes lambda with ps as argument
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(entityRowMapper.mapToEntity(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new DatabaseAccessException("Database query error", e);
        }
    }

    protected List<T> queryList(String sql, PreparedStatementConsumer consumer) {
        List<T> result = new ArrayList<>();
        try (Connection connection = jdbcConnection.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)) {
            consumer.accept(ps);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(entityRowMapper.mapToEntity(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseAccessException("Database query error", e);
        }
        return result;
    }

    protected void executeUpdate(String sql, PreparedStatementConsumer consumer, ResultSetConsumer resultSetConsumer) {
        try (Connection connection = jdbcConnection.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            // Set parameters using the provided consumer (lambda)
            consumer.accept(ps);

            // Execute the update (INSERT, UPDATE, DELETE)
            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("No rows affected.");
            }

            // process the generated keys (e.g., after INSERT)
            if (resultSetConsumer != null) {
                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        resultSetConsumer.accept(generatedKeys);
                    }
                }
            }
        } catch (SQLException e) {
            throw new DatabaseAccessException("Database update error", e);
        }
    }

}
