package com.oasisnourish.controllers;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.oasisnourish.dto.UserInputDto;
import com.oasisnourish.dto.UserResponseDto;
import com.oasisnourish.exceptions.NotFoundException;
import com.oasisnourish.models.User;
import com.oasisnourish.services.UserService;

import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import io.javalin.validation.BodyValidator;
import io.javalin.validation.Validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Optional;

/**
 * Unit tests for the {@link UserController} class.
 * This class tests the methods in UserController to ensure they behave as
 * expected.
 */
public class UserControllerTest {
    private UserService userService;
    private UserController userController;

    @Mock
    private Context ctx; // Use @Mock to initialize the Context

    @Mock
    private BodyValidator<UserInputDto> bodyValidator;

    @Mock
    private Validator<Integer> paramValidator;

    /**
     * Sets up the test environment before each test.
     * Mocks the UserService and Context to isolate the UserController during
     * testing.
     */
    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this); // Initialize the mocks
        userService = mock(UserService.class);
        userController = new UserController(userService);
    }

    /**
     * Tests the findAllUsers method to ensure it returns a list of users.
     */
    @Test
    public void testFindAllUsers() {
        // Arrange
        User user1 = new User("Alice", "alice@test.com", "Password123!");
        user1.setId(1);

        User user2 = new User("Bob", "bob@test.com", "Password456!");
        user2.setId(2);

        when(userService.findAllUsers()).thenReturn(Arrays.asList(user1, user2));

        // Act
        userController.findAllUsers(ctx);

        // Assert
        verify(ctx).json(anyList());
        verify(ctx).status(HttpStatus.OK);
    }

    /**
     * Tests the findUserById method when the user is found.
     */
    @Test
    public void testFindUserById_UserFound() {
        // Arrange
        User user = new User("Alice", "alice@test.com", "Password123!");
        user.setId(1);

        when(ctx.pathParamAsClass("userId", Integer.class)).thenReturn(paramValidator);
        when(paramValidator.get()).thenReturn(1);
        when(userService.findUserById(1)).thenReturn(Optional.of(user));

        // Act
        userController.findUserById(ctx);

        // Assert
        verify(ctx).json(UserResponseDto.fromModel(user));
        verify(ctx).status(HttpStatus.OK);
    }

    /**
     * Tests the findUserById method when the user is not found.
     */
    @Test
    public void testFindUserById_UserNotFound() {
        // Arrange
        when(ctx.pathParamAsClass("userId", Integer.class)).thenReturn(paramValidator);
        when(paramValidator.get()).thenReturn(1);

        when(userService.findUserById(1)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> userController.findUserById(ctx));
    }

    /**
     * Tests the createUser method to ensure a user can be created successfully.
     */
    @Test
    public void testCreateUser_Success() {
        // Arrange
        UserInputDto userDto = new UserInputDto(0, "Alice", "alice@test.com", "Password123!");
        when(ctx.bodyValidator(UserInputDto.class)).thenReturn(bodyValidator);
        when(bodyValidator.check(any(), anyString())).thenReturn(bodyValidator);
        when(bodyValidator.get()).thenReturn(userDto);

        // Act
        userController.createUser(ctx);

        // Assert
        verify(userService).createUser(userDto);
        verify(ctx).status(HttpStatus.CREATED);
    }

    /**
     * Tests the updateUser method to ensure a user can be updated successfully.
     */
    @Test
    public void testUpdateUser_Success() {
        // Arrange
        UserInputDto userDto = new UserInputDto(0, "Alice", "alice@test.com", "Password123!");

        when(ctx.pathParamAsClass("userId", Integer.class)).thenReturn(paramValidator);
        when(paramValidator.get()).thenReturn(1);

        when(ctx.bodyValidator(UserInputDto.class)).thenReturn(bodyValidator);
        when(bodyValidator.check(any(), anyString())).thenReturn(bodyValidator);
        when(bodyValidator.get()).thenReturn(userDto);

        // Act
        userController.updateUser(ctx);

        // Assert
        verify(userService).updateUser(userDto);
        verify(ctx).status(HttpStatus.OK);
    }

    /**
     * Tests the deleteUser method to ensure a user can be deleted successfully.
     */
    @Test
    public void testDeleteUser_Success() {
        // Arrange
        when(ctx.pathParamAsClass("userId", Integer.class)).thenReturn(paramValidator);
        when(paramValidator.get()).thenReturn(1);

        // Act
        userController.deleteUser(ctx);

        // Assert
        verify(userService).deleteUser(1);
        verify(ctx).status(HttpStatus.NO_CONTENT);
    }
}
