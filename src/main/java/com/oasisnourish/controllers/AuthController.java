package com.oasisnourish.controllers;

import java.util.Map;

import org.jetbrains.annotations.NotNull;

import com.oasisnourish.dto.UserInputDto;
import com.oasisnourish.dto.validation.ValidatorFactory;
import com.oasisnourish.exceptions.EmailExistsException;
import com.oasisnourish.exceptions.NotFoundException;
import com.oasisnourish.models.User;
import com.oasisnourish.services.AuthService;
import com.oasisnourish.services.JWTService;
import com.oasisnourish.services.UserService;
import com.oasisnourish.util.RoleValidator;
import com.oasisnourish.util.SessionManager;

import io.javalin.http.Context;
import io.javalin.http.Handler;
import io.javalin.http.UnauthorizedResponse;

/**
 * Controller responsible for handling authentication-related operations.
 * This class provides endpoints for user signup, login, logout, and session
 * management.
 * It integrates various services to ensure secure authentication and
 * authorization flows.
 */
public class AuthController implements Handler {

    private final AuthService authService;
    private final JWTService jwtService;
    private final UserService userService;
    private final SessionManager sessionManager;
    private final RoleValidator roleValidator;

    /**
     * Constructs an AuthController with the necessary dependencies for handling
     * authentication and authorization processes.
     *
     * @param userService    Service for user-related operations.
     * @param authService    Service responsible for handling authentication.
     * @param jwtService     Service for handling JWT generation and validation.
     * @param sessionManager Manages user sessions and cookies.
     * @param roleValidator  Validates roles for authorization purposes.
     */
    public AuthController(UserService userService, AuthService authService,
            JWTService jwtService, SessionManager sessionManager,
            RoleValidator roleValidator) {
        this.jwtService = jwtService;
        this.userService = userService;
        this.authService = authService;
        this.sessionManager = sessionManager;
        this.roleValidator = roleValidator;
    }

    /**
     * Entry point for handling requests. Validates the user's role and session.
     *
     * @param ctx Javalin HTTP context.
     */
    @Override
    public void handle(@NotNull Context ctx) {
        sessionManager.validateAndSetUserSession(ctx, jwtService, userService);
        roleValidator.validateRole(ctx, jwtService, sessionManager.getJwtFromSession(ctx));
    }

    /**
     * Decodes the JWT from cookies to authenticate the user session.
     *
     * @param ctx Javalin HTTP context.
     */
    public void decodeJWTFromCookie(Context ctx) {
        sessionManager.decodeJWTFromCookie(ctx, jwtService);
    }

    /**
     * Retrieves the currently authenticated user.
     *
     * @param ctx Javalin HTTP context.
     */
    public void getCurrentUser(Context ctx) {
        sessionManager.getCurrentUser(ctx);
    }

    /**
     * Registers a new user. Validates the input data and sends a welcome email.
     *
     * @param ctx Javalin HTTP context.
     * @throws EmailExistsException if the email is already registered.
     */
    public void signUpUser(Context ctx) throws EmailExistsException {
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
            ctx.status(201).json("Account created. Check your email for the confirmation link.");
        });
    }

    /**
     * Authenticates an existing user. Validates email and password input.
     *
     * @param ctx Javalin HTTP context.
     * @throws UnauthorizedResponse if authentication fails.
     */
    public void signInUser(Context ctx) throws UnauthorizedResponse {
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

    /**
     * Refreshes the JWT token for the user session.
     *
     * @param ctx Javalin HTTP context.
     */
    public void refreshToken(Context ctx) {
        sessionManager.refreshToken(ctx, jwtService);
    }

    /**
     * Logs out the user by invalidating the session and removing the token.
     *
     * @param ctx Javalin HTTP context.
     */
    public void signOutUser(Context ctx) {
        sessionManager.invalidateSession(ctx);
        ctx.status(204).result("Sign out successful.");
    }

    public void generateConfirmationToken(Context ctx) {
        int userId = ctx.pathParamAsClass("userId", Integer.class).get();

        userService.findUserById(userId).ifPresentOrElse(user -> {
            authService.sendConfirmationToken(user);
            ctx.status(201).result("Confirmation token created. Check your email for the confirmation link.");
        }, () -> {
            throw new NotFoundException("User Not Found");
        });

    }

    public void confirmAccountToken(Context ctx) {
        int userId = ctx.pathParamAsClass("userId", Integer.class).get();
        String token = ctx.pathParamAsClass("token", String.class).get();
        authService.confirmAccount(userId, token);
        ctx.result("Your account has been verified");
    }

    public void updateUserIfChanged(Context ctx) {
        User currUser = ctx.sessionAttribute("currentUser");
        if (currUser != null) {
            userService.findUserById(currUser.getId()).ifPresent((user) -> {
                if (!currUser.equals(user)) {
                    Map<String, String> tokens = jwtService.generateTokens(user);
                    sessionManager.setTokensInCookies(ctx, tokens, jwtService);
                    sessionManager.decodeJWTFromCookie(ctx, jwtService);
                    sessionManager.validateAndSetUserSession(ctx, jwtService, userService);
                }
            });

        }
    }

}
