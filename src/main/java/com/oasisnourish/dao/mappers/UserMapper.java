package com.oasisnourish.dao.mappers;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import com.oasisnourish.enums.Role;
import com.oasisnourish.models.User;

/**
 * Responsible for mapping between {@link User} objects and database rows.
 * This includes converting {@link ResultSet} rows into User objects and
 * preparing {@link User} objects for database insertion and updates.
 */
public class UserMapper {

    /**
     * Maps a {@link ResultSet} to a {@link User} object.
     *
     * @param resultSet the {@link ResultSet} containing user data.
     * @return a {@link User} object populated with data from the {@link ResultSet}.
     * @throws SQLException if there is an error accessing the {@link ResultSet}.
     */
    public User mapToUser(ResultSet resultSet) throws SQLException {
        User user = new User();
        user.setId(resultSet.getInt("id"));
        user.setName(resultSet.getString("name"));
        user.setEmail(resultSet.getString("email"));
        user.setPassword(resultSet.getString("password"));
        user.setRole(Role.valueOf(resultSet.getString("role")));

        Timestamp emailVerifiedTs = resultSet.getTimestamp("email_verified");
        if (emailVerifiedTs != null) {
            user.setEmailVerified(emailVerifiedTs.toLocalDateTime());
        }

        return user;
    }

    /**
     * Maps a {@link User} object to a {@link PreparedStatement} for database
     * operations.
     *
     * @param statement the {@link PreparedStatement} to populate with user data.
     * @param user      the {@link User} object containing data to set in the
     *                  {@link PreparedStatement}.
     * @param includeId whether to include the user ID in the
     *                  {@link PreparedStatement}. If true, the user ID will be
     *                  included for update operations; otherwise, it is used for
     *                  insert operations.
     * @throws SQLException if there is an error setting values in the
     *                      {@link PreparedStatement}.
     */
    public void mapToRow(PreparedStatement statement, User user, boolean includeId) throws SQLException {
        statement.setString(1, user.getName());
        statement.setString(2, user.getEmail());
        statement.setString(3, user.getPassword());
        statement.setString(4, user.getRole().name());

        if (user.getEmailVerified() != null) {
            statement.setTimestamp(5, Timestamp.valueOf(user.getEmailVerified()));
        }

        if (includeId) {
            statement.setInt(6, user.getId());
        }
    }
}