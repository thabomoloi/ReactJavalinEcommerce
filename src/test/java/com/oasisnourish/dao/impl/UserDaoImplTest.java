package com.oasisnourish.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.AdditionalAnswers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.oasisnourish.dao.mappers.EntityRowMapper;
import com.oasisnourish.db.JdbcConnection;
import com.oasisnourish.enums.Role;
import com.oasisnourish.models.User;

/**
 * Unit tests for {@link UserDaoImpl}.
 *
 * <p>
 * This class tests the CRUD operations for {@link User} in the
 * {@link UserDaoImpl} class.
 * </p>
 */
@ExtendWith(MockitoExtension.class)
public class UserDaoImplTest {

    private static final String FIND_USER_BY_ID = "SELECT * FROM users WHERE id = ?";
    private static final String FIND_ALL_USERS = "SELECT * FROM users";
    private static final String INSERT_USER = "INSERT INTO users (name, email, password, role, email_verified) VALUES (?, ?, ?, ?, ?)";
    private static final String UPDATE_USER = "UPDATE users SET name = ?, email = ?, password = ?, role = ?, email_verified = ? WHERE id = ?";
    private static final String DELETE_USER_BY_ID = "DELETE FROM users WHERE id = ?";
    private static final String FIND_USER_BY_EMAIL = "SELECT * FROM users WHERE email = ?";
    private static final String VERIFY_EMAIL = "UPDATE users SET role = ?, email_verified = ? WHERE email = ?";

    @Mock
    private JdbcConnection jdbcConnection;

    @Mock
    private Connection connection;

    @Mock
    private PreparedStatement preparedStatement;

    @Mock
    private ResultSet resultSet;

    @Mock
    private EntityRowMapper<User> userRowMapper;

    @InjectMocks
    private UserDaoImpl userDao;

    /**
     * Initializes mock dependencies and the {@link UserDaoImpl} instance before
     * each test.
     *
     * @throws SQLException if a SQL error occurs while setting up mocks.
     */
    @BeforeEach
    public void setUp() throws SQLException {
        when(jdbcConnection.getConnection()).thenReturn(connection);
    }

    /**
     * Tests retrieving all users from the database when a user exists.
     *
     * @throws SQLException if a SQL error occurs during the test.
     */
    @Test
    public void testFindById() throws SQLException {
        int userId = 1;
        User mockUser = new User(userId, "John Doe", "john.doe@test.com", "password123", Role.USER);
        mockUser.setEmailVerified(LocalDateTime.of(2024, 1, 1, 0, 0));

        when(connection.prepareStatement(FIND_USER_BY_ID)).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(userRowMapper.mapToEntity(resultSet)).thenReturn(mockUser);

        Optional<User> result = userDao.find(userId);
        assertTrue(result.isPresent());
        assertEquals(mockUser, result.get());
    }

    /**
     * Tests retrieving all users from the database when users exist.
     *
     * @throws SQLException if a SQL error occurs during the test.
     */
    @Test
    public void testFindAll() throws SQLException {
        when(connection.prepareStatement(FIND_ALL_USERS)).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);

        List<User> expectedUsers = Arrays.asList(
                new User(1, "John Doe", "john.doe@test.com", "password123", Role.USER),
                new User(2, "Jane Doe", "jane.doe@test.com", "password456", Role.ADMIN));

        when(resultSet.next()).thenReturn(true, true, false);
        when(userRowMapper.mapToEntity(resultSet)).thenAnswer(AdditionalAnswers.returnsElementsOf(expectedUsers));

        List<User> actualUsers = userDao.findAll();

        assertEquals(expectedUsers.size(), actualUsers.size());
        assertEquals(expectedUsers, actualUsers);
    }

    /**
     * Tests saving a new user to the database.
     *
     * @throws SQLException if a SQL error occurs during the test.
     */
    @Test
    public void testSave() throws SQLException {
        User user = new User(0, "John Doe", "john.doe@testcom", "password123", Role.USER);

        when(connection.prepareStatement(INSERT_USER, PreparedStatement.RETURN_GENERATED_KEYS))
                .thenReturn(preparedStatement);
        when(preparedStatement.getGeneratedKeys()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getInt(1)).thenReturn(1);

        when(preparedStatement.executeUpdate()).thenReturn(1);

        userDao.save(user);

        verify(userRowMapper).mapToRow(preparedStatement, user, false);
        assertEquals(1, user.getId());
    }

    /**
     * Tests updating an existing user in the database.
     *
     * @throws SQLException if a SQL error occurs during the test.
     */
    @Test
    public void testUpdate() throws SQLException {

        User user = new User(1, "John Doe", "john.doe@test.com", "password123", Role.USER);

        when(connection.prepareStatement(UPDATE_USER, Statement.RETURN_GENERATED_KEYS)).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(1);

        userDao.update(user);
        verify(userRowMapper).mapToRow(preparedStatement, user, true);
        verify(preparedStatement, times(1)).executeUpdate();
    }

    /**
     * Tests deleting a user from the database by ID.
     *
     * @throws SQLException if a SQL error occurs during the test.
     */
    @Test
    public void testDelete() throws SQLException {
        when(connection.prepareStatement(DELETE_USER_BY_ID, Statement.RETURN_GENERATED_KEYS))
                .thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(1);

        userDao.delete(1);

        verify(preparedStatement, times(1)).executeUpdate();
    }

    /**
     * Tests finding a user by email address when the user exists in the
     * database.
     *
     * @throws SQLException if a SQL error occurs during the test.
     */
    @Test
    public void testFindByEmail() throws SQLException {
        User mockUser = new User(1, "John Doe", "john.doe@test.com", "password123", Role.USER);
        mockUser.setEmailVerified(LocalDateTime.of(2024, 1, 1, 0, 0));

        when(connection.prepareStatement(FIND_USER_BY_EMAIL)).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(userRowMapper.mapToEntity(resultSet)).thenReturn(mockUser);

        Optional<User> result = userDao.findByEmail(mockUser.getEmail());
        assertTrue(result.isPresent());
        assertEquals(mockUser, result.get());
    }

    /**
     * Tests verifying a user's email in the database.
     *
     * @throws SQLException if a SQL error occurs during the test.
     */
    @Test
    public void testVerifyEmail() throws SQLException {
        when(connection.prepareStatement(VERIFY_EMAIL, Statement.RETURN_GENERATED_KEYS))
                .thenReturn(preparedStatement);

        when(preparedStatement.executeUpdate()).thenReturn(1);

        userDao.verifyEmail("john.doe@test.com");

        verify(preparedStatement, times(1)).executeUpdate();
    }
}
