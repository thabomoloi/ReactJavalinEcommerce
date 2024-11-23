package com.oasisnourish.dao.mappers;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.oasisnourish.enums.Role;
import com.oasisnourish.models.User;

/**
 * Unit tests for the {@link UserMapper} class.
 *
 * <p>
 * This class tests the mapping functionality between {@link User} objects and
 * database rows by verifying the correct behavior of the {@link UserMapper}
 * methods.
 * </p>
 */
@ExtendWith(MockitoExtension.class)
public class UserRowMapperTest {

    @Mock
    private PreparedStatement preparedStatement;

    @Mock
    private ResultSet resultSet;

    @InjectMocks
    private UserRowMapper userRowMapper;

    /**
     * Tests the {@link UserMapper#mapToEntity(ResultSet)} method to ensure that
     * a {@link User} object is correctly populated from a {@link ResultSet}.
     *
     * @throws SQLException if there is an error accessing the mock
     * {@link ResultSet}.
     */
    @Test
    public void testMapToEntity() throws SQLException {
        User expectedUser = new User(1, "John Doe", "john.doe@test.com", "password123", Role.USER);

        when(resultSet.getInt("id")).thenReturn(expectedUser.getId());
        when(resultSet.getString("name")).thenReturn(expectedUser.getName());
        when(resultSet.getString("email")).thenReturn(expectedUser.getEmail());
        when(resultSet.getString("password")).thenReturn(expectedUser.getPassword());
        when(resultSet.getString("role")).thenReturn(expectedUser.getRole().name());

        User actualUser = userRowMapper.mapToEntity(resultSet);
        assertEquals(expectedUser, actualUser);
    }

    /**
     * Tests the {@link UserMapper#mapToRow(PreparedStatement, User, boolean)}
     * method for insert operations, ensuring the {@link PreparedStatement} is
     * populated correctly without the user ID.
     *
     * @throws SQLException if there is an error setting values in the mock
     * {@link PreparedStatement}.
     */
    @Test
    public void testMapToRow_Insert() throws SQLException {
        User user = new User(0, "Jane Doe", "jane.doe@test.com", "password456", Role.ADMIN);

        userRowMapper.mapToRow(preparedStatement, user, false);

        verify(preparedStatement).setString(1, "Jane Doe");
        verify(preparedStatement).setString(2, "jane.doe@test.com");
        verify(preparedStatement).setString(3, "password456");
        verify(preparedStatement).setString(4, "ADMIN");
    }

    /**
     * Tests the {@link UserMapper#mapToRow(PreparedStatement, User, boolean)}
     * method for update operations, ensuring the {@link PreparedStatement} is
     * populated correctly with the user ID.
     *
     * @throws SQLException if there is an error setting values in the mock
     * {@link PreparedStatement}.
     */
    @Test
    public void testMapToRow_Update() throws SQLException {
        User user = new User(2, "Jane Doe", "jane.doe@test.com", "password456", Role.ADMIN);

        userRowMapper.mapToRow(preparedStatement, user, true);

        verify(preparedStatement).setString(1, "Jane Doe");
        verify(preparedStatement).setString(2, "jane.doe@test.com");
        verify(preparedStatement).setString(3, "password456");
        verify(preparedStatement).setString(4, "ADMIN");
        verify(preparedStatement).setInt(5, 2);
    }
}
