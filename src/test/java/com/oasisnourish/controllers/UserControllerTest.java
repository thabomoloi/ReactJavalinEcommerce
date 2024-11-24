package com.oasisnourish.controllers;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.oasisnourish.dto.UserInputDto;
import com.oasisnourish.dto.UserResponseDto;
import com.oasisnourish.exceptions.NotFoundException;
import com.oasisnourish.models.users.User;
import com.oasisnourish.services.users.UserService;

import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import io.javalin.validation.BodyValidator;
import io.javalin.validation.Validator;

/**
 * Unit tests for the {@link UserController} class. This class tests the methods
 * in UserController to ensure they behave as expected.
 */
@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private Context ctx;

    @Mock
    private BodyValidator<UserInputDto> bodyValidator;

    @Mock
    private Validator<Integer> paramValidator;

    @InjectMocks
    private UserController userController;

    /**
     * Tests the findAllUsers method to ensure it returns a list of users.
     */
    @Test
    public void testFindAllUsers() {
        User user1 = new User(1, "Alice", "alice@test.com", "Password123!");
        User user2 = new User(2, "Bob", "bob@test.com", "Password456!");
        List<User> usersList = Arrays.asList(user1, user2);
        when(userService.findAllUsers()).thenReturn(usersList);

        userController.findAllUsers(ctx);

        verify(ctx).status(HttpStatus.OK);
        verify(ctx).json(usersList.stream().map(UserResponseDto::new).collect(Collectors.toList()));
    }

    /**
     * Tests the findUserById method when the user is found.
     */
    @Test
    public void testFindUserById_UserFound() {
        User user = new User(1, "Alice", "alice@test.com", "Password123!");
        when(ctx.pathParamAsClass("userId", Integer.class)).thenReturn(paramValidator);
        when(paramValidator.get()).thenReturn(1);
        when(userService.findUserById(1)).thenReturn(Optional.of(user));

        userController.findUserById(ctx);

        verify(ctx).json(new UserResponseDto(user));
        verify(ctx).status(HttpStatus.OK);
    }

    /**
     * Tests the findUserById method when the user is not found.
     */
    @Test
    public void testFindUserById_UserNotFound() {
        when(ctx.pathParamAsClass("userId", Integer.class)).thenReturn(paramValidator);
        when(paramValidator.get()).thenReturn(1);
        when(userService.findUserById(1)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> userController.findUserById(ctx));
        assertEquals("User does not exist.", exception.getMessage());

    }

    /**
     * Tests the createUser method to ensure a user can be created successfully.
     */
    @Test
    public void testCreateUser() {
        UserInputDto userDto = new UserInputDto(0, "Alice", "alice@test.com", "Password123!");
        when(ctx.bodyValidator(UserInputDto.class)).thenReturn(bodyValidator);
        when(bodyValidator.check(anyString(), any(), anyString())).thenReturn(bodyValidator);
        when(bodyValidator.get()).thenReturn(userDto);

        userController.createUser(ctx);

        verify(userService).createUser(userDto);
        verify(ctx).status(HttpStatus.CREATED);
        verify(ctx).result("User has been created successfully.");
    }

    /**
     * Tests the updateUser method to ensure a user can be updated successfully.
     */
    @Test
    public void testUpdateUser() {
        when(ctx.sessionAttribute("currentUser")).thenReturn(new User(1, "", "", ""));

        UserInputDto userDto = new UserInputDto(1, "Alice", "alice@test.com", "Password123!");
        when(ctx.pathParamAsClass("userId", Integer.class)).thenReturn(paramValidator);
        when(paramValidator.get()).thenReturn(1);
        when(ctx.bodyValidator(UserInputDto.class)).thenReturn(bodyValidator);
        when(bodyValidator.check(anyString(), any(), anyString())).thenReturn(bodyValidator);
        when(bodyValidator.get()).thenReturn(userDto);

        userController.updateUser(ctx);

        verify(userService).updateUser(userDto);
        verify(ctx).status(HttpStatus.OK);
        verify(ctx).result("User has been updated successfully.");
    }

    /**
     * Tests the deleteUser method to ensure a user can be deleted successfully.
     */
    @Test
    public void testDeleteUser() {
        when(ctx.sessionAttribute("currentUser")).thenReturn(new User(1, "", "", ""));
        when(ctx.pathParamAsClass("userId", Integer.class)).thenReturn(paramValidator);
        when(paramValidator.get()).thenReturn(1);

        userController.deleteUser(ctx);

        verify(userService).deleteUser(1);
        verify(ctx).status(HttpStatus.NO_CONTENT);
    }
}
