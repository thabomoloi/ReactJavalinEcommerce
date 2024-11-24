package com.oasisnourish.dao.impl.users;

import java.util.List;
import java.util.Optional;

import com.oasisnourish.dao.impl.AbstractDao;
import com.oasisnourish.dao.mappers.EntityRowMapper;
import com.oasisnourish.dao.users.UserDao;
import com.oasisnourish.db.JdbcConnection;
import com.oasisnourish.enums.Role;
import com.oasisnourish.models.users.User;

/**
 * Implementation of the {@link UserDao} interface using JDBC. Provides CRUD
 * operations for {@link User} entities.
 */
public class UserDaoImpl extends AbstractDao<User> implements UserDao {

    private static final String FIND_BY_ID = "SELECT * FROM users WHERE id = ?";
    private static final String FIND_ALL = "SELECT * FROM users";
    private static final String INSERT = "INSERT INTO users (name, email, password, role) VALUES (?, ?, ?, ?)";
    private static final String UPDATE = "UPDATE users SET name = ?, email = ?, password = ?, role = ? WHERE id = ?";
    private static final String DELETE = "DELETE FROM users WHERE id = ?";
    private static final String FIND_BY_EMAIL = "SELECT * FROM users WHERE email = ?";
    private static final String VERIFY_EMAIL = "UPDATE users SET role = ? WHERE email = ?";

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
        return querySingle(FIND_BY_ID, ps -> ps.setInt(1, id));
    }

    @Override
    public List<User> findAll() {
        return queryList(FIND_ALL, _ -> {
        });
    }

    @Override
    public void save(User user) {
        executeUpdate(INSERT, ps -> entityRowMapper.mapToRow(ps, user, false), rs -> user.setId(rs.getInt(1)));
    }

    @Override
    public void update(User user) {
        executeUpdate(UPDATE, ps -> entityRowMapper.mapToRow(ps, user, true), null);
    }

    @Override
    public void delete(int id) {
        executeUpdate(DELETE, ps -> ps.setInt(1, id), null);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return querySingle(FIND_BY_EMAIL, ps -> ps.setString(1, email));
    }

    @Override
    public void verifyEmail(String email) {
        executeUpdate(VERIFY_EMAIL, ps -> {
            ps.setString(1, Role.USER.name());
            ps.setString(2, email);
        }, null);
    }
}
