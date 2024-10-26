package com.oasisnourish.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.oasisnourish.dao.UserDao;
import com.oasisnourish.dao.mappers.UserMapper;
import com.oasisnourish.db.JdbcConnection;
import com.oasisnourish.exceptions.DatabaseAccessException;
import com.oasisnourish.models.User;

/**
 * Implementation of the {@link UserDao} interface using JDBC.
 * Provides CRUD operations for {@link User} entities.
 */
public class UserDaoImpl implements UserDao {
    private final JdbcConnection jdbcConnection;
    private final UserMapper userMapper;

    /**
     * Constructs a {@link UserDaoImpl} with the given database connection.
     *
     * @param connection the JDBC connection to use for database operations.
     */
    public UserDaoImpl(JdbcConnection jdbcConnection) {
        this.jdbcConnection = jdbcConnection;
        userMapper = new UserMapper();
    }

    @Override
    public Optional<User> find(int id) {
        String sql = "SELECT * FROM users WHERE id = ?";

        try (Connection connection = jdbcConnection.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    User user = userMapper.mapToUser(rs);
                    return Optional.of(user);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseAccessException("Failed to find user with id " + id, e);
        }
        return Optional.empty();
    }

    @Override
    public List<User> findAll() {
        String sql = "SELECT * FROM users";
        List<User> users = new ArrayList<>();

        try (Connection connection = jdbcConnection.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                users.add(userMapper.mapToUser(rs));
            }

        } catch (SQLException e) {
            throw new DatabaseAccessException("Failed to find all users", e);
        }
        return users;
    }

    @Override
    public void save(User user) {
        String sql = "INSERT INTO users (name, email, password, role, email_verified) VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = jdbcConnection.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)) {
            userMapper.mapToRow(ps, user, false);
            int affectedRows = ps.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Saving user failed, no rows affected.");
            }

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    user.setId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Saving user failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            throw new DatabaseAccessException("Error saving user: " + user.getName(), e);
        }
    }

    @Override
    public void update(User user) {
        String sql = "UPDATE users SET name = ?, email = ?, password = ?, role = ?, email_verified = ? WHERE id = ?";

        try (Connection connection = jdbcConnection.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)) {
            userMapper.mapToRow(ps, user, true);
            int affectedRows = ps.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Updating user failed, no rows affected.");
            }
        } catch (SQLException e) {
            throw new DatabaseAccessException("Error updating user: " + user.getName(), e);
        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM users WHERE id = ?";

        try (Connection connection = jdbcConnection.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            int affectedRows = ps.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Deleting user failed, no rows affected.");
            }
        } catch (SQLException e) {
            throw new DatabaseAccessException("Error deleting user with id: " + id, e);
        }

    }

    @Override
    public Optional<User> findByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email = ?";

        try (Connection connection = jdbcConnection.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    User user = userMapper.mapToUser(rs);
                    return Optional.of(user);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseAccessException("Failed to find user with email " + email, e);
        }
        return Optional.empty();
    }

    @Override
    public void verifyEmail(String email) {
        String sql = "UPDATE users SET email_verified = ? WHERE email = ?";

        try (Connection connection = jdbcConnection.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)) {
            LocalDateTime emailVerified = LocalDateTime.now();
            ps.setTimestamp(1, Timestamp.valueOf(emailVerified));
            ps.setString(2, email);
            int affectedRows = ps.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Verifying user failed, no rows affected.");
            }
        } catch (SQLException e) {
            throw new DatabaseAccessException("Error verifying email: " + email, e);
        }
    }

}
