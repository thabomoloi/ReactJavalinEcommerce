package com.oasisnourish.dao.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.oasisnourish.dao.UserDao;
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
public class UserDaoImplTest {

    private JdbcConnection mockJdbcConnection;
    private UserDao userDao;
    private Connection mockConnection;
    private PreparedStatement mockPreparedStatement;
    private ResultSet mockResultSet;

    /**
     * Initializes mock dependencies and the {@link UserDaoImpl} instance before
     * each test.
     *
     * @throws SQLException if a SQL error occurs while setting up mocks.
     */
    @BeforeEach
    public void setUp() throws SQLException {
        mockJdbcConnection = mock(JdbcConnection.class);
        userDao = new UserDaoImpl(mockJdbcConnection);
        mockConnection = mock(Connection.class);
        mockPreparedStatement = mock(PreparedStatement.class);
        mockResultSet = mock(ResultSet.class);

        when(mockJdbcConnection.getConnection()).thenReturn(mockConnection);
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
    }

    /**
     * Tests finding a user by ID when the user exists in the database.
     *
     * @throws SQLException if a SQL error occurs during the test.
     */
    @Test
    public void testFind_UserExists() throws SQLException {
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);

        when(mockResultSet.getInt("id")).thenReturn(1);
        when(mockResultSet.getString("name")).thenReturn("John Doe");
        when(mockResultSet.getString("email")).thenReturn("john.doe@test.com");
        when(mockResultSet.getString("password")).thenReturn("password123");
        when(mockResultSet.getString("role")).thenReturn("USER");
        when(mockResultSet.getTimestamp("email_verified")).thenReturn(Timestamp.valueOf("2024-10-26 12:00:00"));

        Optional<User> user = userDao.find(1);

        assertTrue(user.isPresent());
        assertEquals(1, user.get().getId());
        assertEquals("John Doe", user.get().getName());
    }

    /**
     * Tests finding a user by ID when the user does not exist in the database.
     *
     * @throws SQLException if a SQL error occurs during the test.
     */
    @Test
    public void testFind_UserDoesNotExist() throws SQLException {
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false);

        Optional<User> user = userDao.find(1);
        assertFalse(user.isPresent());
    }

    /**
     * Tests retrieving all users from the database when users exist.
     *
     * @throws SQLException if a SQL error occurs during the test.
     */
    @Test
    public void testFindAll_UsersExist() throws SQLException {
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true, true, false);

        when(mockResultSet.getInt("id")).thenReturn(1).thenReturn(2);
        when(mockResultSet.getString("name")).thenReturn("John Doe").thenReturn("Jane Doe");
        when(mockResultSet.getString("email")).thenReturn("john.doe@test.com").thenReturn("jane.doe@test.com");
        when(mockResultSet.getString("password")).thenReturn("password123").thenReturn("password456");
        when(mockResultSet.getString("role")).thenReturn("USER").thenReturn("ADMIN");
        when(mockResultSet.getTimestamp("email_verified")).thenReturn(Timestamp.valueOf("2024-10-26 12:00:00"));

        List<User> users = userDao.findAll();

        assertEquals(2, users.size());
        assertEquals("John Doe", users.get(0).getName());
        assertEquals("Jane Doe", users.get(1).getName());
    }

    /**
     * Tests saving a new user to the database.
     *
     * @throws SQLException if a SQL error occurs during the test.
     */
    @Test
    public void testSave_UserInserted() throws SQLException {

        // Simulate the execution update
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);

        // Set up the ResultSet for generated keys
        when(mockPreparedStatement.getGeneratedKeys()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getInt(1)).thenReturn(1);

        User user = new User();
        user.setName("John Doe");
        user.setEmail("john.doe@example.com");
        user.setPassword("password123");
        user.setRole(Role.USER);

        userDao.save(user);

        // Verify that executeUpdate was called on the prepared statement
        verify(mockPreparedStatement).executeUpdate();
        // Verify that the user ID was set
        assertEquals(1, user.getId());
    }

    /**
     * Tests updating an existing user in the database.
     *
     * @throws SQLException if a SQL error occurs during the test.
     */
    @Test
    public void testUpdate_UserUpdated() throws SQLException {
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);

        User user = new User();
        user.setId(1);
        user.setName("John Doe");
        user.setEmail("john.doe@test.com");
        user.setPassword("password123");
        user.setRole(Role.USER);

        userDao.update(user);

        verify(mockPreparedStatement).executeUpdate();
    }

    /**
     * Tests deleting a user from the database by ID.
     *
     * @throws SQLException if a SQL error occurs during the test.
     */
    @Test
    public void testDelete_UserDeleted() throws SQLException {
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
    public void testFindByEmail_UserExists() throws SQLException {
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);

        when(mockResultSet.getInt("id")).thenReturn(1);
        when(mockResultSet.getString("name")).thenReturn("John Doe");
        when(mockResultSet.getString("email")).thenReturn("john.doe@test.com");
        when(mockResultSet.getString("password")).thenReturn("password123");
        when(mockResultSet.getString("role")).thenReturn("USER");

        Optional<User> user = userDao.findByEmail("john.doe@test.com");

        assertTrue(user.isPresent());
        assertEquals("John Doe", user.get().getName());
    }

    /**
     * Tests verifying a user's email in the database.
     *
     * @throws SQLException if a SQL error occurs during the test.
     */
    @Test
    public void testVerifyEmail_EmailVerified() throws SQLException {
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);

        userDao.verifyEmail("john.doe@test.com");

        verify(mockPreparedStatement).executeUpdate();
    }
}
