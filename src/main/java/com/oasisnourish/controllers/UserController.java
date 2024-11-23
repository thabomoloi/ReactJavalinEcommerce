package com.oasisnourish.controllers;

import java.util.List;
import java.util.stream.Collectors;

import com.oasisnourish.dto.UserInputDto;
import com.oasisnourish.dto.UserResponseDto;
import com.oasisnourish.dto.validation.ValidatorFactory;
import com.oasisnourish.enums.Role;
import com.oasisnourish.exceptions.NotFoundException;
import com.oasisnourish.models.User;
import com.oasisnourish.services.UserService;

import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import io.javalin.http.UnauthorizedResponse;

/**
 * Controller for managing user-related endpoints.
 */
public class UserController {

    private final UserService userService;

    /**
     * Constructs a {@link UserController} with a given {@link UserService}.
     *
     * @param userService the {@link UserService} to handle user-related
     * business logic.
     */
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Handles the request to get all users.
     *
     * @param ctx the {@link Javalin} context object containing the HTTP request
     * and response.
     */
    public void findAllUsers(Context ctx) {
        List<UserResponseDto> users = userService.findAllUsers().stream()
                .map(UserResponseDto::new)
                .collect(Collectors.toList());
        ctx.status(HttpStatus.OK);
        ctx.json(users);
    }

    /**
     * Handles the request to get a user by their ID.
     *
     * @param ctx the {@link Javalin} context object containing the HTTP request
     * and response.
     * @throws NotFoundException if the user with the specified ID is not found.
     */
    public void findUserById(Context ctx) {
        int userId = ctx.pathParamAsClass("userId", Integer.class).get();
        User user = userService.findUserById(userId)
                .orElseThrow(() -> new NotFoundException("User does not exist."));

        ctx.status(HttpStatus.OK);
        ctx.json(new UserResponseDto(user));
    }

    /**
     * Handles the request to create a new user.
     *
     * @param ctx the {@link Javalin} context object containing the HTTP request
     * and response.
     */
    public void createUser(Context ctx) {
        UserInputDto userDto = ValidatorFactory.getValidator(ctx.bodyValidator(UserInputDto.class))
                .isNameRequired()
                .isEmailRequired()
                .isEmailValid()
                .isPasswordRequired()
                .isPasswordLengthValid()
                .isPasswordPatternValid()
                .get();
        userService.createUser(userDto);
        ctx.status(HttpStatus.CREATED);
        ctx.result("User has been created successfully.");
    }

    /**
     * Handles the request to update an existing user by ID.
     *
     * @param ctx the {@link Javalin} context object containing the HTTP request
     * and response.
     */
    public void updateUser(Context ctx) {
        // Only current user of admin can update
        User currentUser = ctx.sessionAttribute("currentUser");
        int userId = ctx.pathParamAsClass("userId", Integer.class).get();

        if (currentUser == null || !(currentUser.getId() == userId || currentUser.getRole() == Role.ADMIN)) {
            throw new UnauthorizedResponse("You have no permission to update this user.");
        }

        var userDto = ValidatorFactory.getValidator(ctx.bodyValidator(UserInputDto.class))
                .isNameRequired()
                .isEmailRequired()
                .isEmailValid()
                .get();

        userDto.setId(userId);
        userService.updateUser(userDto);
        ctx.status(HttpStatus.OK);
        ctx.result("User has been updated successfully.");
    }

    /**
     * Handles the request to delete a user by ID.
     *
     * @param ctx the {@link Javalin} context object containing the HTTP request
     * and response.
     */
    public void deleteUser(Context ctx) {
        User currentUser = ctx.sessionAttribute("currentUser");
        int userId = ctx.pathParamAsClass("userId", Integer.class).get();

        if (currentUser == null || !(currentUser.getId() == userId || currentUser.getRole() == Role.ADMIN)) {
            throw new UnauthorizedResponse("You have no permission to delete this user.");
        }

        userService.deleteUser(userId);
        ctx.status(HttpStatus.NO_CONTENT);
    }
}
