package com.oasisnourish.services.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.oasisnourish.dao.UserDao;
import com.oasisnourish.dto.UserInputDto;
import com.oasisnourish.exceptions.EmailExistsException;
import com.oasisnourish.exceptions.NotFoundException;
import com.oasisnourish.models.User;

public class UserServiceImplTest {

    @Mock
    private UserDao userDao;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testFindUserById_UserExists() {
        User user = new User("John Doe", "john.doe@test.com", "hashedPassword");
        user.setId(1);
        when(userDao.find(1)).thenReturn(Optional.of(user));

        Optional<User> result = userService.findUserById(1);

        assertTrue(result.isPresent());
        assertEquals("John Doe", result.get().getName());
    }

    @Test
    public void testFindUserById_UserDoesNotExist() {
        when(userDao.find(1)).thenReturn(Optional.empty());

        // assertThrows(NotFoundException.class, () -> userService.findUserById(1));
    }

    @Test
    public void testCreateUser_EmailAlreadyExists() {
        UserInputDto userDto = new UserInputDto("John Doe", "john.doe@test.com",
                "password123");

        when(userDao.findByEmail(anyString())).thenReturn(Optional.of(new User()));

        assertThrows(EmailExistsException.class, () -> userService.createUser(userDto));
    }

    @Test
    public void testCreateUser_Success() {
        UserInputDto userDto = new UserInputDto("John Doe", "john.doe@test.com",
                "password123");

        when(userDao.findByEmail(anyString())).thenReturn(Optional.empty());
        doAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(1); // Simulate setting an ID after saving
            return null; // No need to return anything for void methods
        }).when(userDao).save(any(User.class));

        userService.createUser(userDto);

        verify(userDao).save(any(User.class));
    }

    @Test
    public void testUpdateUser_UserExists() {
        UserInputDto userDto = new UserInputDto("John Doe", "john.doe@test.com",
                "newPassword");
        userDto.setId(1);
        User existingUser = new User("Old Name", "old.email@test.com",
                "oldPassword");
        existingUser.setId(1);
        when(userDao.find(1)).thenReturn(Optional.of(existingUser));

        userService.updateUser(userDto);

        verify(userDao).update(any(User.class));
        assertEquals("John Doe", existingUser.getName());
    }

    @Test
    public void testUpdateUser_UserDoesNotExist() {
        UserInputDto userDto = new UserInputDto("John Doe", "john.doe@test.com",
                "newPassword");

        when(userDao.find(1)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.updateUser(userDto));
    }

    @Test
    public void testDeleteUser_UserExists() {
        User existingUser = new User("John Doe", "john.doe@test.com",
                "hashedPassword");
        existingUser.setId(1);

        when(userDao.find(1)).thenReturn(Optional.of(existingUser));

        userService.deleteUser(1);

        verify(userDao).delete(1);
    }

    @Test
    public void testDeleteUser_UserDoesNotExist() {
        when(userDao.find(1)).thenReturn(Optional.empty());

        // assertThrows(NotFoundException.class, () -> userService.deleteUser(1));
    }

    @Test
    public void testFindUserByEmail_UserExists() {
        User user = new User("John Doe", "john.doe@test.com", "hashedPassword");
        user.setId(1);

        when(userDao.findByEmail("john.doe@test.com")).thenReturn(Optional.of(user));

        Optional<User> result = userService.findUserByEmail("john.doe@test.com");

        assertTrue(result.isPresent());
        assertEquals("John Doe", result.get().getName());
    }

    @Test
    public void testFindUserByEmail_UserDoesNotExist() {
        when(userDao.findByEmail("john.doe@test.com")).thenReturn(Optional.empty());

        // assertThrows(NotFoundException.class, () ->
        // userService.findUserByEmail("john.doe@test.com"));
    }

    @Test
    public void testVerifyEmail_Success() {
        String email = "john.doe@test.com";

        userService.verifyEmail(email);

        verify(userDao).verifyEmail(email);
    }
}
