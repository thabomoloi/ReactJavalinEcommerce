package com.oasisnourish.controllers;

import java.util.List;
import java.util.stream.Collectors;

import com.oasisnourish.dto.UserInputDto;
import com.oasisnourish.dto.UserResponseDto;
import com.oasisnourish.exceptions.NotFoundException;
import com.oasisnourish.models.User;
import com.oasisnourish.services.UserService;

import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;

/**
 * Controller for managing user-related endpoints.
 */
public class UserController {
    private final UserService userService;

    /**
     * Constructs a {@link UserController} with a given {@link UserService}.
     *
     * @param userService the {@link UserService} to handle user-related business
     *                    logic.
     */
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Registers the routes for user-related actions.
     *
     * @param app the {@link Javalin} application instance where routes will be
     *            registered.
     */
    public void registerRoutes(Javalin app) {
        app.get("/users", this::findAllUsers);
        app.get("/users/{userId}", this::findUserById);
        app.post("/users", this::createUser);
        app.put("/users/{userId}", this::updateUser);
        app.delete("/users/{userId}", this::deleteUser);
    }

    /**
     * Handles the request to get all users.
     *
     * @param ctx the {@link Javalin} context object containing the HTTP request and
     *            response.
     */
    private void findAllUsers(Context ctx) {
        List<UserResponseDto> users = userService.findAllUsers().stream()
                .map(UserResponseDto::fromModel)
                .collect(Collectors.toList());
        ctx.json(users);
    }

    /**
     * Handles the request to get a user by their ID.
     *
     * @param ctx the {@link Javalin} context object containing the HTTP request and
     *            response.
     * @throws NotFoundException if the user with the specified ID is not found.
     */
    private void findUserById(Context ctx) {
        int userId = ctx.pathParamAsClass("userId", Integer.class).get();
        User user = userService.findUserById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        ctx.json(UserResponseDto.fromModel(user));
    }

    /**
     * Handles the request to create a new user.
     *
     * @param ctx the {@link Javalin} context object containing the HTTP request and
     *            response.
     */
    private void createUser(Context ctx) {
        UserInputDto userDto = ctx.bodyValidator(UserInputDto.class)
                .check(obj -> obj.getName() != null && !obj.getName().trim().isEmpty(), "Name is required.")
                .check(obj -> obj.getEmail() != null && !obj.getEmail().trim().isEmpty(), "Email is required.")
                .check(obj -> obj.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$"), "Invalid email address.")
                .check(obj -> {
                    String password = obj.getPassword();
                    return password != null && !password.trim().isEmpty();
                }, "Password is required.")
                .check(obj -> {
                    String password = obj.getPassword();
                    return password.length() >= 8 && password.length() <= 16;
                }, "Password must be between 8 and 16 characters.")
                .check(obj -> obj.getPassword().matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=!]).*$"),
                        "Password must contain at least one lowercase letter, one uppercase letter, one digit, and one special character (@, #, $, %, ^, &, +, =, !).")
                .get();
        userService.createUser(userDto);
        ctx.status(HttpStatus.CREATED);
    }

    /**
     * Handles the request to update an existing user by ID.
     *
     * @param ctx the {@link Javalin} context object containing the HTTP request and
     *            response.
     */
    private void updateUser(Context ctx) {
        int userId = ctx.pathParamAsClass("userId", Integer.class).get();
        UserInputDto userDto = ctx.bodyValidator(UserInputDto.class)
                .check(obj -> obj.getName() != null && !obj.getName().trim().isEmpty(), "Name is required.")
                .check(obj -> obj.getEmail() != null && !obj.getEmail().trim().isEmpty(), "Email is required.")
                .check(obj -> obj.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$"), "Invalid email address.")
                .check(obj -> {
                    String password = obj.getPassword();
                    return password != null && !password.trim().isEmpty() &&
                            password.length() >= 8 && password.length() <= 16;
                }, "Password must be between 8 and 16 characters.")
                .check(obj -> {
                    String password = obj.getPassword();
                    return password != null && !password.trim().isEmpty() &&
                            obj.getPassword().matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=!]).*$");
                }, "Password must contain at least one lowercase letter, one uppercase letter, one digit, and one special character (@, #, $, %, ^, &, +, =, !).")
                .get();
        userDto.setId(userId);
        userService.updateUser(userDto);
        ctx.status(HttpStatus.OK);
    }

    /**
     * Handles the request to delete a user by ID.
     *
     * @param ctx the {@link Javalin} context object containing the HTTP request and
     *            response.
     */
    private void deleteUser(Context ctx) {
        int userId = ctx.pathParamAsClass("userId", Integer.class).get();
        userService.deleteUser(userId);
        ctx.status(HttpStatus.NO_CONTENT);
    }
}
