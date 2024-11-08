package com.oasisnourish.dao.mappers;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import com.oasisnourish.enums.Role;
import com.oasisnourish.models.User;

public class UserRowMapper implements EntityRowMapper<User> {

    public void mapToRow(PreparedStatement statement, User user, boolean includeId) throws SQLException {
        statement.setString(1, user.getName());
        statement.setString(2, user.getEmail());
        statement.setString(3, user.getPassword());
        statement.setString(4, user.getRole().name());
        statement.setTimestamp(5, user.getEmailVerified() == null ? null : Timestamp.valueOf(user.getEmailVerified()));

        if (includeId) {
            statement.setInt(6, user.getId());
        }
    }

    @Override
    public User mapToEntity(ResultSet resultSet) throws SQLException {
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
}