package com.oasisnourish.dao.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.oasisnourish.dao.UserDao;
import com.oasisnourish.dao.UserMockResultHelper;
import com.oasisnourish.db.JdbcConnection;
import com.oasisnourish.models.User;
import com.oasisnourish.enums.Role;

/**
 * Unit tests for {@link UserDaoImpl}.
 * 
 * <p>
 * This class tests the CRUD operations for {@link User} in the
 * {@link UserDaoImpl} class.
 * </p>
 */
public class UserDaoImplTest extends UserMockResultHelper {

    private static final String FIND_USER_BY_ID = "SELECT * FROM users WHERE id = ?";
    private static final String FIND_ALL_USERS = "SELECT * FROM users";
    private static final String INSERT_USER = "INSERT INTO users (name, email, password, role, email_verified) VALUES (?, ?, ?, ?, ?)";
    private static final String UPDATE_USER = "UPDATE users SET name = ?, email = ?, password = ?, role = ?, email_verified = ? WHERE id = ?";
    private static final String DELETE_USER_BY_ID = "DELETE FROM users WHERE id = ?";
    private static final String FIND_USER_BY_EMAIL = "SELECT * FROM users WHERE email = ?";
    private static final String VERIFY_EMAIL = "UPDATE users SET role = ?, email_verified = ? WHERE email = ?";

    private JdbcConnection mockJdbcConnection;
    private UserDao userDao;
    private Connection mockConnection;
    private PreparedStatement mockPreparedStatement;

    /**
     * Initializes mock dependencies and the {@link UserDaoImpl} instance before
     * each test.
     *
     * @throws SQLException if a SQL error occurs while setting up mocks.
     */
    @BeforeEach
    public void setUp() throws SQLException {
        mockJdbcConnection = mock(JdbcConnection.class);
        mockConnection = mock(Connection.class);
        mockPreparedStatement = mock(PreparedStatement.class);
        mockResultSet = mock(ResultSet.class);

        when(mockJdbcConnection.getConnection()).thenReturn(mockConnection);

        userDao = new UserDaoImpl(mockJdbcConnection);

    }

    /**
     * Tests retrieving all users from the database when a user exists.
     *
     * @throws SQLException if a SQL error occurs during the test.
     */
    @Test
    public void testFindById() throws SQLException {
        when(mockConnection.prepareStatement(FIND_USER_BY_ID)).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);

        User expectedUser = new User(1, "John Doe", "john.doe@test.com", "password123", Role.USER);
        expectedUser.setEmailVerified(LocalDateTime.of(2024, 1, 1, 0, 0));

        when(mockResultSet.next()).thenReturn(true);
        mockUserResultSet(expectedUser);

        Optional<User> actualUser = userDao.find(1);

        assertTrue(actualUser.isPresent());
        assertEquals(expectedUser, actualUser.get());

        verify(mockPreparedStatement).setInt(1, 1);
        verify(mockPreparedStatement).executeQuery();
    }

    /**
     * Tests retrieving all users from the database when users exist.
     *
     * @throws SQLException if a SQL error occurs during the test.
     */
    @Test
    public void testFindAll() throws SQLException {
        when(mockConnection.prepareStatement(FIND_ALL_USERS)).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);

        List<User> expectedUsers = Arrays.asList(
                new User(1, "John Doe", "john.doe@test.com", "password123", Role.USER),
                new User(2, "Jane Doe", "jane.doe@test.com", "password456", Role.ADMIN));

        when(mockResultSet.next()).thenReturn(true, true, false);
        mockUserResultSet(expectedUsers);

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
        when(mockConnection.prepareStatement(INSERT_USER, Statement.RETURN_GENERATED_KEYS))
                .thenReturn(mockPreparedStatement);

        when(mockPreparedStatement.executeUpdate()).thenReturn(1);

        when(mockPreparedStatement.getGeneratedKeys()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getInt(1)).thenReturn(1);

        User user = new User(0, "John Doe", "john.doe@example.com", "password123", Role.USER);
        userDao.save(user);

        verify(mockPreparedStatement).executeUpdate();
        assertEquals(1, user.getId());
    }

    /**
     * Tests updating an existing user in the database.
     *
     * @throws SQLException if a SQL error occurs during the test.
     */
    @Test
    public void testUpdate() throws SQLException {
        when(mockConnection.prepareStatement(UPDATE_USER, Statement.RETURN_GENERATED_KEYS))
                .thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);

        User user = new User(1, "John Doe", "john.doe@test.com", "password123", Role.USER);
        userDao.update(user);

        verify(mockPreparedStatement).executeUpdate();
    }

    /**
     * Tests deleting a user from the database by ID.
     *
     * @throws SQLException if a SQL error occurs during the test.
     */
    @Test
    public void testDelete() throws SQLException {
        when(mockConnection.prepareStatement(DELETE_USER_BY_ID, Statement.RETURN_GENERATED_KEYS))
                .thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);

        userDao.delete(1);

        verify(mockPreparedStatement).executeUpdate();
    }

    /**
     * Tests finding a user by email address when the user exists in the database.
     *
     * @throws SQLException if a SQL error occurs during the test.
     */
    @Test
    public void testFindByEmail() throws SQLException {
        when(mockConnection.prepareStatement(FIND_USER_BY_EMAIL)).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);

        User expectedUser = new User(1, "John Doe", "john.doe@test.com", "password123", Role.USER);
        when(mockResultSet.next()).thenReturn(true);
        mockUserResultSet(expectedUser);

        Optional<User> actualUser = userDao.findByEmail("john.doe@test.com");

        assertTrue(actualUser.isPresent());
        assertEquals(expectedUser, actualUser.get());
    }

    /**
     * Tests verifying a user's email in the database.
     *
     * @throws SQLException if a SQL error occurs during the test.
     */
    @Test
    public void testVerifyEmail() throws SQLException {
        when(mockConnection.prepareStatement(VERIFY_EMAIL, Statement.RETURN_GENERATED_KEYS))
                .thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);

        userDao.verifyEmail("john.doe@test.com");

        verify(mockPreparedStatement).executeUpdate();
    }
}
