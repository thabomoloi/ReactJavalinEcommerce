package com.oasisnourish.dao.mappers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.oasisnourish.enums.Role;
import com.oasisnourish.models.User;

/**
 * Unit tests for the {@link UserMapper} class.
 * 
 * <p>
 * This class tests the mapping functionality between {@link User} objects
 * and database rows by verifying the correct behavior of the {@link UserMapper}
 * methods.
 * </p>
 */
public class UserMapperTest {
    private UserMapper userMapper;
    private ResultSet mockResultSet;
    private PreparedStatement mockPreparedStatement;

    /**
     * Sets up the test environment by initializing a new {@link UserMapper}
     * and creating mocked instances of {@link ResultSet} and
     * {@link PreparedStatement}.
     * This method is called before each test method in this class.
     *
     * @throws SQLException if there is an error initializing the mock objects.
     */
    @BeforeEach
    public void setUp() throws SQLException {
        userMapper = new UserMapper();
        mockResultSet = mock(ResultSet.class);
        mockPreparedStatement = mock(PreparedStatement.class);
    }

    /**
     * Tests the {@link UserMapper#mapToUser(ResultSet)} method to ensure that
     * a {@link User} object is correctly populated from a {@link ResultSet}.
     *
     * @throws SQLException if there is an error accessing the mock
     *                      {@link ResultSet}.
     */
    @Test
    public void testMapToUser() throws SQLException {
        // Prepare mock ResultSet data
        when(mockResultSet.getInt("id")).thenReturn(1);
        when(mockResultSet.getString("name")).thenReturn("John Doe");
        when(mockResultSet.getString("email")).thenReturn("john.doe@test.com");
        when(mockResultSet.getString("password")).thenReturn("password123");
        when(mockResultSet.getString("role")).thenReturn("USER");
        when(mockResultSet.getTimestamp("email_verified")).thenReturn(Timestamp.valueOf("2024-10-26 12:00:00"));

        // Call method under test
        User user = userMapper.mapToUser(mockResultSet);

        // Assertions
        assertEquals(1, user.getId());
        assertEquals("John Doe", user.getName());
        assertEquals("john.doe@test.com", user.getEmail());
        assertEquals("password123", user.getPassword());
        assertEquals(Role.USER, user.getRole());
        assertNotNull(user.getEmailVerified());
    }

    /**
     * Tests the {@link UserMapper#mapToRow(PreparedStatement, User, boolean)}
     * method for insert operations, ensuring the {@link PreparedStatement} is
     * populated correctly without the user ID.
     *
     * @throws SQLException if there is an error setting values in the mock
     *                      {@link PreparedStatement}.
     */
    @Test
    public void testMapToRow_Insert() throws SQLException {
        User user = new User(
                "Jane Doe",
                "jane.doe@test.com",
                "password456",
                Role.ADMIN);

        user.setEmailVerified(Timestamp.valueOf("2024-10-26 12:00:00").toLocalDateTime());

        // Call method under test
        userMapper.mapToRow(mockPreparedStatement, user, false);

        // Verify that the PreparedStatement was set correctly
        verify(mockPreparedStatement).setString(1, "Jane Doe");
        verify(mockPreparedStatement).setString(2, "jane.doe@test.com");
        verify(mockPreparedStatement).setString(3, "password456");
        verify(mockPreparedStatement).setString(4, "ADMIN");
        verify(mockPreparedStatement).setTimestamp(5, Timestamp.valueOf("2024-10-26 12:00:00"));
    }

    /**
     * Tests the {@link UserMapper#mapToRow(PreparedStatement, User, boolean)}
     * method for update operations, ensuring the {@link PreparedStatement} is
     * populated correctly with the user ID.
     *
     * @throws SQLException if there is an error setting values in the mock
     *                      {@link PreparedStatement}.
     */
    @Test
    public void testMapToRow_Update() throws SQLException {
        User user = new User(
                "Jane Doe",
                "jane.doe@test.com",
                "password456",
                Role.ADMIN);
        user.setId(2);
        user.setEmailVerified(Timestamp.valueOf("2024-10-26 12:00:00").toLocalDateTime());

        // Call method under test
        userMapper.mapToRow(mockPreparedStatement, user, true);

        // Verify that the PreparedStatement was set correctly, including ID
        verify(mockPreparedStatement).setString(1, "Jane Doe");
        verify(mockPreparedStatement).setString(2, "jane.doe@test.com");
        verify(mockPreparedStatement).setString(3, "password456");
        verify(mockPreparedStatement).setString(4, "ADMIN");
        verify(mockPreparedStatement).setTimestamp(5, Timestamp.valueOf("2024-10-26 12:00:00"));
        verify(mockPreparedStatement).setInt(6, 2);
    }
}
