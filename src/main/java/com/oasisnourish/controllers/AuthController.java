package com.oasisnourish.controllers;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.jetbrains.annotations.NotNull;
import org.thymeleaf.context.WebContext;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.oasisnourish.config.EnvConfig;
import com.oasisnourish.dto.*;
import com.oasisnourish.dto.validation.DtoValidatorImpl;
import com.oasisnourish.dto.validation.UserInputDtoValidator;
import com.oasisnourish.dto.validation.ValidatorFactory;
import com.oasisnourish.enums.Role;
import com.oasisnourish.exceptions.*;
import com.oasisnourish.models.User;
import com.oasisnourish.services.*;
import io.github.cdimascio.dotenv.Dotenv;
import io.javalin.http.Context;
import io.javalin.http.Cookie;
import io.javalin.http.Handler;
import io.javalin.http.SameSite;
import io.javalin.http.UnauthorizedResponse;
import io.javalin.security.RouteRole;
import jakarta.validation.ConstraintViolationException;

/**
 * Controller for managing user authentication, handling JWT tokens, and
 * securing routes based on user roles. This controller provides endpoints for
 * user sign-up, login, logout, token refresh, and role-based route handling.
 */
public class AuthController /* implements Handler */ {

    // private static final String JWT_ACCESS_KEY = "JWTAccessToken";
    // private static final String JWT_REFRESH_KEY = "JWTRefreshToken";

    // private final Dotenv dotenv;
    // private final JWTService jwtService;
    // private final AuthService authService;
    // private final UserService userService;

    // private final Map<String, RouteRole> rolesMapping = Map.of(
    // "user", Role.USER,
    // "admin", Role.ADMIN);

    // /**
    // * Constructs an AuthController with specified services for user, auth, and
    // JWT
    // * management.
    // *
    // * @param userService the user service for managing user-related data.
    // * @param authService the authentication service for handling user sign-up and
    // * login.
    // * @param jwtService the JWT service for handling token generation and
    // * validation.
    // */
    // public AuthController(UserService userService, AuthService authService,
    // JWTService jwtService) {
    // this.dotenv = EnvConfig.getDotenv();
    // this.jwtService = jwtService;
    // this.userService = userService;
    // this.authService = authService;
    // }

    // /**
    // * Decodes JWT access and refresh tokens from cookies and sets them in the
    // * session if valid.
    // *
    // * @param ctx the Javalin HTTP context.
    // */
    // public void decodeJWTFromCookie(Context ctx) {
    // Optional.ofNullable(ctx.cookie(JWT_ACCESS_KEY))
    // .flatMap(jwtService::getToken)
    // .ifPresent(jwt -> ctx.sessionAttribute(JWT_ACCESS_KEY, jwt));

    // Optional.ofNullable(ctx.cookie(JWT_REFRESH_KEY))
    // .flatMap(jwtService::getToken)
    // .ifPresent(jwt -> ctx.sessionAttribute(JWT_REFRESH_KEY, jwt));
    // }

    // /**
    // * Retrieves the current authenticated user from the session and returns the
    // * user data as JSON.
    // *
    // * @param ctx the Javalin HTTP context.
    // */
    // public void getCurrentUser(Context ctx) {
    // Optional.ofNullable(ctx.sessionAttribute("currentUser"))
    // .ifPresentOrElse(
    // user -> ctx.json(UserResponseDto.fromModel((User) user)),
    // () -> ctx.status(404).result("No user is currently logged in."));
    // }

    // /**
    // * Handles user sign-up by validating input and creating a new user.
    // *
    // * @param ctx the Javalin HTTP context.
    // * @throws ConstraintViolationException if there are validation issues with
    // the
    // * input.
    // * @throws EmailExistsException if the email is already associated with
    // * an account.
    // */
    // public void signUpUser(Context ctx) throws ConstraintViolationException,
    // EmailExistsException {
    // UserInputDto userDto =
    // ValidatorFactory.getValidator(ctx.bodyValidator(UserInputDto.class))
    // .isNameRequired()
    // .isEmailRequired()
    // .isEmailValid()
    // .isPasswordRequired()
    // .isPasswordLengthValid()
    // .isPasswordPatternValid()
    // .get();

    // authService.signUpUser(userDto).ifPresent((user) -> {
    // authService.sendWelcomeEmail(user);
    // authService.sendConfirmationToken(user);
    // ctx.status(201).json("Account created, check your email account for the
    // confirmation link");
    // });
    // }

    // /**
    // * Handles user login, generates JWT tokens, and sets them as cookies.
    // *
    // * @param ctx the Javalin HTTP context.
    // */
    // public void signInUser(Context ctx) throws ConstraintViolationException,
    // EmailExistsException {
    // UserInputDto userDto = ctx.bodyValidator(UserInputDto.class)
    // .check(obj -> obj.getEmail() != null && !obj.getEmail().trim().isEmpty(),
    // "Email is required.")
    // .check(obj -> obj.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$"), "Invalid
    // email address.")
    // .check(obj -> {
    // String password = obj.getPassword();
    // return password != null && !password.trim().isEmpty();
    // }, "Password is required.")
    // .get();

    // authService.signInUser(userDto).ifPresentOrElse((user) -> {
    // Map<String, String> tokens = jwtService.generateTokens(user);
    // ctx.cookie(createTokenCookie("access", tokens.get(JWT_ACCESS_KEY)));
    // ctx.cookie(createTokenCookie("refresh", tokens.get(JWT_REFRESH_KEY)));
    // ctx.status(200).result("Login successful.");
    // }, () -> {
    // throw new UnauthorizedResponse("Invalid email or password");
    // });

    // }

    // /**
    // * Refreshes the JWT access token if the refresh token is valid.
    // *
    // * @param ctx the Javalin HTTP context.
    // * @throws UnauthorizedResponse if the token version is invalid or user not
    // * found.
    // */
    // public void refreshToken(Context ctx) {
    // DecodedJWT jwt = ctx.sessionAttribute(JWT_REFRESH_KEY);
    // if (jwt != null) {
    // int userId = jwt.getClaim("userId").asInt();
    // long version = jwt.getClaim("version").asLong();

    // try {
    // if (version != jwtService.getTokenVersion(userId)) {
    // throw new UnauthorizedResponse();
    // }
    // User user = userService.getUserById(userId);
    // Map<String, String> tokens = jwtService.generateTokens(user);
    // ctx.cookie(createTokenCookie("access", tokens.get(JWT_ACCESS_KEY)));
    // ctx.cookie(createTokenCookie("refresh", tokens.get(JWT_REFRESH_KEY)));
    // ctx.status(200).result("Tokens refresh successful.");
    // } catch (NotFoundException e) {
    // throw new UnauthorizedResponse();
    // }
    // }
    // }

    // /**
    // * Logs out the user by invalidating the JWT tokens and session.
    // *
    // * @param ctx the Javalin HTTP context.
    // */
    // public void signOut(Context ctx) {
    // String accessToken = ctx.cookie(JWT_ACCESS_KEY);
    // String refreshToken = ctx.cookie(JWT_REFRESH_KEY);
    // if (accessToken == null) {
    // jwtService.deleteToken(accessToken);
    // }
    // if (refreshToken == null) {
    // jwtService.deleteToken(refreshToken);
    // }
    // ctx.removeCookie(JWT_ACCESS_KEY);
    // ctx.removeCookie(JWT_REFRESH_KEY);
    // invalidateSession(ctx);
    // ctx.status(200).result("Sign out successful.");
    // }

    // /**
    // * Main handler for securing routes, checking roles, and validating JWT
    // tokens.
    // *
    // * @param ctx the Javalin HTTP context.
    // * @throws UnauthorizedResponse if the user's role is not permitted for the
    // * route.
    // */
    // @Override
    // public void handle(@NotNull Context ctx) {
    // DecodedJWT jwt = ctx.sessionAttribute(JWT_ACCESS_KEY);

    // String userRole = Optional.ofNullable(jwt)
    // .map(token -> token.getClaim("role").asString())
    // .orElse("");
    // RouteRole role = rolesMapping.getOrDefault(userRole, Role.ANYONE);

    // Set<RouteRole> permittedRoles = ctx.routeRoles();

    // if (!permittedRoles.contains(role)) {
    // throw new UnauthorizedResponse();
    // }

    // validateAndSetUserSession(ctx, jwt, permittedRoles);
    // }

    // /**
    // * Validates the JWT version and sets the current user in the session.
    // *
    // * @param ctx the Javalin HTTP context.
    // * @param jwt the decoded JWT token.
    // * @param permittedRoles the roles permitted for the current route.
    // * @throws UnauthorizedResponse if token version is invalid or user not found.
    // */
    // private void validateAndSetUserSession(Context ctx, DecodedJWT jwt,
    // Set<RouteRole> permittedRoles) {
    // if (jwt != null && !permittedRoles.contains(Role.ANYONE)) {
    // int userId = jwt.getClaim("userId").asInt();
    // long version = jwt.getClaim("version").asLong();

    // try {
    // if (version != jwtService.getTokenVersion(userId)) {
    // invalidateSession(ctx);
    // throw new UnauthorizedResponse();
    // }
    // User user = ctx.sessionAttribute("currentUser");
    // if (user == null || user.getId() != userId) {
    // user = userService.getUserById(userId);
    // ctx.sessionAttribute("currentUser", user);
    // }
    // } catch (NotFoundException e) {
    // invalidateSession(ctx);
    // throw new UnauthorizedResponse();
    // }
    // }
    // }

    // /**
    // * Invalidates the current session by removing the currentUser attribute.
    // *
    // * @param ctx the Javalin HTTP context.
    // */
    // private void invalidateSession(Context ctx) {
    // ctx.sessionAttribute("currentUser", null);
    // }

    // /**
    // * Creates a secure cookie for storing JWT tokens based on environment and
    // token
    // * type.
    // *
    // * @param tokenType the type of token, either "access" or "refresh".
    // * @param token the JWT token string.
    // * @return a configured Cookie instance for storing the JWT token.
    // */
    // private Cookie createTokenCookie(String tokenType, String token) {
    // String environment = dotenv.get("ENV", "development");

    // Cookie cookie = new Cookie("access".equals(tokenType) ? JWT_ACCESS_KEY :
    // JWT_REFRESH_KEY, token);
    // cookie.setHttpOnly(true);
    // cookie.setSecure("production".equals(environment));
    // cookie.setSameSite(SameSite.STRICT);
    // cookie.setMaxAge(jwtService.getTokenExpires(tokenType) * 60);
    // return cookie;
    // }
}
