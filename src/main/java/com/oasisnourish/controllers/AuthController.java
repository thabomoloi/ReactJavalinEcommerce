package com.oasisnourish.controllers;

import java.util.Map;

import org.jetbrains.annotations.NotNull;

import com.oasisnourish.dto.UserInputDto;
import com.oasisnourish.dto.validation.ValidatorFactory;
import com.oasisnourish.exceptions.EmailExistsException;
import com.oasisnourish.services.AuthService;
import com.oasisnourish.services.JWTService;
import com.oasisnourish.services.UserService;
import com.oasisnourish.util.RoleValidator;
import com.oasisnourish.util.SessionManager;

import io.javalin.http.Context;
import io.javalin.http.Handler;
import io.javalin.http.UnauthorizedResponse;
import jakarta.validation.ConstraintViolationException;

public class AuthController implements Handler {

    private final AuthService authService;
    private final JWTService jwtService;
    private final UserService userService;
    private final SessionManager sessionManager;
    private final RoleValidator roleValidator;

    public AuthController(UserService userService, AuthService authService,
            JWTService jwtService, SessionManager sessionManager,
            RoleValidator roleValidator) {
        this.jwtService = jwtService;
        this.userService = userService;
        this.authService = authService;
        this.sessionManager = sessionManager;
        this.roleValidator = roleValidator;
    }

    @Override
    public void handle(@NotNull Context ctx) {
        roleValidator.validateRole(ctx, sessionManager, jwtService);
        sessionManager.validateAndSetUserSession(ctx, jwtService, userService);
    }

    public void decodeJWTFromCookie(Context ctx) {
        sessionManager.decodeJWTFromCookie(ctx, jwtService);
    }

    public void getCurrentUser(Context ctx) {
        sessionManager.getCurrentUser(ctx);
    }

    public void signUpUser(Context ctx) throws ConstraintViolationException, EmailExistsException {
        UserInputDto userDto = ValidatorFactory.getValidator(ctx.bodyValidator(UserInputDto.class))
                .isNameRequired()
                .isEmailRequired()
                .isEmailValid()
                .isPasswordRequired()
                .isPasswordLengthValid()
                .isPasswordPatternValid()
                .get();

        authService.signUpUser(userDto).ifPresent(user -> {
            authService.sendWelcomeEmail(user);
            authService.sendConfirmationToken(user);
            ctx.status(201).json("Account created. Check your email for the confirmation link.");
        });
    }

    public void signInUser(Context ctx) throws ConstraintViolationException, UnauthorizedResponse {
        UserInputDto userDto = ValidatorFactory.getValidator(ctx.bodyValidator(UserInputDto.class))
                .isEmailRequired()
                .isEmailValid()
                .isPasswordRequired()
                .get();

        authService.signInUser(userDto).ifPresentOrElse(user -> {
            Map<String, String> tokens = jwtService.generateTokens(user);
            sessionManager.setTokensInCookies(ctx, tokens, jwtService);
            ctx.status(200).result("Login successful.");
        }, () -> {
            throw new UnauthorizedResponse("Invalid email or password");
        });
    }

    public void refreshToken(Context ctx) {
        sessionManager.refreshToken(ctx, jwtService);
    }

    public void signOut(Context ctx) {
        sessionManager.invalidateSession(ctx, jwtService);
        ctx.status(204).result("Sign out successful.");
    }
}
