package com.oasisnourish.dao.impl;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.oasisnourish.dao.UserDao;
import com.oasisnourish.dao.mappers.EntityRowMapper;
import com.oasisnourish.db.JdbcConnection;
import com.oasisnourish.enums.Role;
import com.oasisnourish.models.User;

/**
 * Implementation of the {@link UserDao} interface using JDBC.
 * Provides CRUD operations for {@link User} entities.
 */
public class UserDaoImpl extends AbstractDao<User> implements UserDao {

    private static final String FIND_USER_BY_ID = "SELECT * FROM users WHERE id = ?";
    private static final String FIND_ALL_USERS = "SELECT * FROM users";
    private static final String INSERT_USER = "INSERT INTO users (name, email, password, role, email_verified) VALUES (?, ?, ?, ?, ?)";
    private static final String UPDATE_USER = "UPDATE users SET name = ?, email = ?, password = ?, role = ?, email_verified = ? WHERE id = ?";
    private static final String DELETE_USER_BY_ID = "DELETE FROM users WHERE id = ?";
    private static final String FIND_USER_BY_EMAIL = "SELECT * FROM users WHERE email = ?";
    private static final String VERIFY_EMAIL = "UPDATE users SET role = ?, email_verified = ? WHERE email = ?";

    /**
     * Constructs a {@link UserDaoImpl} with the given database connection.
     *
     * @param connection the JDBC connection to use for database operations.
     */
    public UserDaoImpl(JdbcConnection jdbcConnection, EntityRowMapper<User> userRowMapper) {
        super(jdbcConnection, userRowMapper);
    }

    @Override
    public Optional<User> find(int id) {
        return querySingle(FIND_USER_BY_ID, ps -> ps.setInt(1, id));
    }

    @Override
    public List<User> findAll() {
        return queryList(FIND_ALL_USERS, _ -> {
        });
    }

    @Override
    public void save(User user) {
        executeUpdate(INSERT_USER, ps -> entityRowMapper.mapToRow(ps, user, false), rs -> user.setId(rs.getInt(1)));
    }

    @Override
    public void update(User user) {
        executeUpdate(UPDATE_USER, ps -> entityRowMapper.mapToRow(ps, user, true), null);
    }

    @Override
    public void delete(int id) {
        executeUpdate(DELETE_USER_BY_ID, ps -> ps.setInt(1, id), null);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return querySingle(FIND_USER_BY_EMAIL, ps -> ps.setString(1, email));
    }

    @Override
    public void verifyEmail(String email) {
        executeUpdate(VERIFY_EMAIL, ps -> {
            ps.setString(1, Role.USER.name());
            ps.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
            ps.setString(3, email);
        }, null);
    }
}
