package com.oasisnourish.services.impl.users;

import java.util.Map;
import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.thymeleaf.context.IContext;

import com.oasisnourish.dto.users.UserInputDto;
import com.oasisnourish.enums.Tokens;
import com.oasisnourish.exceptions.InvalidTokenException;
import com.oasisnourish.exceptions.NotFoundException;
import com.oasisnourish.models.tokens.AuthToken;
import com.oasisnourish.models.tokens.JsonWebToken;
import com.oasisnourish.models.users.User;
import com.oasisnourish.services.EmailService;
import com.oasisnourish.services.tokens.AuthTokenService;
import com.oasisnourish.services.tokens.JWTService;
import com.oasisnourish.services.users.AuthService;
import com.oasisnourish.services.users.UserService;
import com.oasisnourish.util.EmailContentBuilder;

import io.javalin.http.UnauthorizedResponse;

/**
 * Implementation of the {@link AuthService} for handling user authentication
 * and email confirmation.
 */
public class AuthServiceImpl implements AuthService {

    private final UserService userService;
    private final EmailService emailService;
    private final AuthTokenService authTokenService;
    private final JWTService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final EmailContentBuilder emailContentBuilder;

    /**
     * Constructs an {@link AuthServiceImpl} with the necessary services.
     *
     * @param userService The service for user-related operations.
     * @param emailService The service for sending emails.
     * @param tokenService The service for token generation and validation.
     * @param emailContentBuilder The utility for building email contexts.
     */
    public AuthServiceImpl(UserService userService, EmailService emailService, AuthTokenService authTokenService,
            JWTService jwtService, PasswordEncoder passwordEncoder, EmailContentBuilder emailContentBuilder) {
        this.userService = userService;
        this.emailService = emailService;
        this.jwtService = jwtService;
        this.authTokenService = authTokenService;
        this.passwordEncoder = passwordEncoder;
        this.emailContentBuilder = emailContentBuilder;
    }

    @Override
    public void signUpUser(UserInputDto userDto) {
        userService.createUser(userDto);
        sendWelcomeEmail(userDto.getEmail());
    }

    @Override
    public Map<String, JsonWebToken> signInUser(UserInputDto userDto) {
        var user = userService.findUserByEmail(userDto.getEmail())
                .filter(u -> passwordEncoder.matches(userDto.getPassword(), u.getPassword()))
                .orElseThrow(() -> new UnauthorizedResponse("Invalid email or password."));

        Map<String, JsonWebToken> tokens = jwtService.createTokens(user);
        return tokens;

    }

    @Override
    public void sendConfirmationToken(int userId) {
        var user = userService.findUserById(userId).orElseThrow(() -> new NotFoundException("User does not exist."));
        sendTokenEmail(user, Tokens.Auth.ACCOUNT_CONFIRMATION_TOKEN, "Confirm Your Email Address", "user/confirm");
    }

    @Override
    public void sendWelcomeEmail(String email) {
        var user = userService.findUserByEmail(email).orElseThrow(() -> new NotFoundException("User does not exist."));
        sendTokenEmail(user, Tokens.Auth.ACCOUNT_CONFIRMATION_TOKEN, "Welcome to Oasis Nourish", "user/welcome");
    }

    @Override
    public void confirmAccount(String token) {
        AuthToken authToken = authTokenService.findToken(token).orElseThrow(() -> new InvalidTokenException("The authentication token is either invalid or has expired. Please request a new one."));
        userService.findUserById(authToken.getUserId()).ifPresent(user -> {
            userService.verifyEmail(user.getEmail());
            authTokenService.deleteToken(authToken.getToken());
        });
    }

    @Override
    public void sendResetPasswordToken(String email) {
        var user = userService.findUserByEmail(email).orElseThrow(() -> new NotFoundException("User does not exist."));
        sendTokenEmail(user, Tokens.Auth.PASSWORD_RESET_TOKEN, "Reset your password", "user/reset-password");
    }

    @Override
    public void resetPassword(String token, String password) {
        AuthToken authToken = authTokenService.findToken(token).orElseThrow(() -> new InvalidTokenException("The authentication token is either invalid or has expired. Please request a new one."));
        userService.findUserById(authToken.getUserId()).ifPresent(user -> {
            userService.updatePassword(user.getId(), password);
            authTokenService.deleteToken(authToken.getToken());
        });
    }

    /**
     * Sends an email with a generated token to the user.
     *
     * @param user the user to whom the email is sent
     * @param type the type of token to generate
     * @param subject the subject of the email
     * @param template the template to use for the email body
     */
    private void sendTokenEmail(User user, Tokens.Auth type, String subject, String template) {
        AuthToken token = authTokenService.createToken(user.getId(), type);
        IContext context = emailContentBuilder.buildEmailTokenContext(user, token);
        emailService.sendEmail(user.getEmail(), subject, template, context);
    }

    @Override
    public Optional<Map<String, JsonWebToken>> updateSignedInUserIfChanged(User signedInUser) {
        if (signedInUser != null) {
            var user = userService.findUserById(signedInUser.getId());
            if (user.isPresent() && !signedInUser.equals(signedInUser)) {
                Map<String, JsonWebToken> tokens = jwtService.createTokens(user.get());
                return Optional.of(tokens);
            }
        }
        return Optional.empty();
    }
}
