package com.oasisnourish.dao.impl;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import static org.mockito.Mockito.verify;
import org.mockito.junit.jupiter.MockitoExtension;

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
public class UserDaoImplTest extends DaoTestHelper<User> {

    private static final String FIND_BY_ID = "SELECT * FROM users WHERE id = ?";
    private static final String FIND_ALL = "SELECT * FROM users";
    private static final String INSERT = "INSERT INTO users (name, email, password, role) VALUES (?, ?, ?, ?)";
    private static final String UPDATE = "UPDATE users SET name = ?, email = ?, password = ?, role = ? WHERE id = ?";
    private static final String DELETE = "DELETE FROM users WHERE id = ?";
    private static final String FIND_BY_EMAIL = "SELECT * FROM users WHERE email = ?";
    private static final String VERIFY_EMAIL = "UPDATE users SET role = ? WHERE email = ?";

    @InjectMocks
    private UserDaoImpl userDao;

    private final List<User> users = Arrays.asList(
            new User(1, "John Doe", "john.doe@test.com", "password123", Role.USER),
            new User(2, "Jane Doe", "jane.doe@test.com", "password456", Role.ADMIN));

    @BeforeEach
    @Override
    public void setUp() throws SQLException {
        super.setUp();
    }

    @Test
    void testFindById_Exists() throws SQLException {
        User expected = users.get(0);
        mockEntityExists(FIND_BY_ID, expected);
        assertEntityExists(expected, userDao.find(expected.getId()));

    }

    @Test
    void testFindById_DoesNotExist() throws SQLException {
        mockEntityDoesNotExist(FIND_BY_ID);
        assertEntityDoesNotExist(userDao.find(1));
    }

    @Test
    void testFindByEmail_Exists() throws SQLException {
        User expected = users.get(0);
        mockEntityExists(FIND_BY_EMAIL, expected);
        assertEntityExists(expected, userDao.findByEmail(expected.getEmail()));

    }

    @Test
    void testFindByUrl_DoesNotExist() throws SQLException {
        mockEntityDoesNotExist(FIND_BY_EMAIL);
        assertEntityDoesNotExist(userDao.findByEmail("nouser@test.com"));
    }

    @Test
    void testFindAll_Exists() throws SQLException {
        mockEntityList(FIND_ALL, users);
        assertEntityListEquals(users, userDao.findAll());
    }

    @Test
    void testFindAll_DoesNotExist() throws SQLException {
        mockEntityEmpyList(FIND_ALL);
        assertEntityListEquals(Arrays.asList(), userDao.findAll());
    }

    @Test
    void testSave() throws SQLException {
        int expectedId = 5;
        var user = new User(0, "Johhny Doe", "johny@test.com", "password123");

        mockEntitySave(INSERT, expectedId);
        userDao.save(user);
        assertEntitySaved(expectedId, user.getId(), user);

    }

    @Test
    void testUpdate() throws SQLException {
        var user = users.get(0);
        mockEntityUpdate(UPDATE);
        userDao.update(user);
        assertEntityUpdated(user);
    }

    @Test
    void testDelete() throws SQLException {
        mockEntityDelete(DELETE);
        userDao.delete(1);
        assertEntityDeleted(1);
    }

    @Test
    public void testVerifyEmail() throws SQLException {
        mockEntityUpdate(VERIFY_EMAIL);
        String email = "john.doe@test.com";
        userDao.verifyEmail(email);
        verify(ps).setString(1, Role.USER.name());
        verify(ps).setString(2, email);
        verify(ps).executeUpdate();
    }
}
