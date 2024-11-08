package com.oasisnourish.dao.mappers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

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
public class UserRowMapperTest {
    private UserRowMapper userRowMapper;
    private PreparedStatement mockPreparedStatement;
    private ResultSet mockResultSet;

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
        userRowMapper = new UserRowMapper();
        mockResultSet = mock(ResultSet.class);
        mockPreparedStatement = mock(PreparedStatement.class);
    }

    /**
     * Tests the {@link UserMapper#mapToEntity(ResultSet)} method to ensure that
     * a {@link User} object is correctly populated from a {@link ResultSet}.
     *
     * @throws SQLException if there is an error accessing the mock
     *                      {@link ResultSet}.
     */
    @Test
    public void testMapToEntity() throws SQLException {
        User expectedUser = new User(1, "John Doe", "john.doe@test.com", "password123", Role.USER);
        expectedUser.setEmailVerified(LocalDateTime.of(2024, 1, 1, 0, 0));

        when(mockResultSet.getInt("id")).thenReturn(expectedUser.getId());
        when(mockResultSet.getString("name")).thenReturn(expectedUser.getName());
        when(mockResultSet.getString("email")).thenReturn(expectedUser.getEmail());
        when(mockResultSet.getString("password")).thenReturn(expectedUser.getPassword());
        when(mockResultSet.getString("role")).thenReturn(expectedUser.getRole().name());
        when(mockResultSet.getTimestamp("email_verified"))
                .thenReturn(expectedUser.getEmailVerified() == null ? null
                        : Timestamp.valueOf(expectedUser.getEmailVerified()));

        User actualUser = userRowMapper.mapToEntity(mockResultSet);
        assertEquals(expectedUser, actualUser);
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
        User user = new User(0, "Jane Doe", "jane.doe@test.com", "password456", Role.ADMIN);
        user.setEmailVerified(Timestamp.valueOf("2024-10-26 12:00:00").toLocalDateTime());

        userRowMapper.mapToRow(mockPreparedStatement, user, false);

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
        User user = new User(2, "Jane Doe", "jane.doe@test.com", "password456", Role.ADMIN);
        user.setEmailVerified(Timestamp.valueOf("2024-10-26 12:00:00").toLocalDateTime());

        userRowMapper.mapToRow(mockPreparedStatement, user, true);

        verify(mockPreparedStatement).setString(1, "Jane Doe");
        verify(mockPreparedStatement).setString(2, "jane.doe@test.com");
        verify(mockPreparedStatement).setString(3, "password456");
        verify(mockPreparedStatement).setString(4, "ADMIN");
        verify(mockPreparedStatement).setTimestamp(5, Timestamp.valueOf("2024-10-26 12:00:00"));
        verify(mockPreparedStatement).setInt(6, 2);
    }
}
