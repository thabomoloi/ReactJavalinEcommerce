package com.oasisnourish.services.impl;

import java.util.Map;
import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;

import com.oasisnourish.dto.UserInputDto;
import com.oasisnourish.exceptions.NotFoundException;
import com.oasisnourish.models.User;
import com.oasisnourish.services.AuthService;
import com.oasisnourish.services.EmailService;
import com.oasisnourish.services.JWTService;
import com.oasisnourish.services.TokenService;
import com.oasisnourish.services.UserService;
import com.oasisnourish.util.EmailContentBuilder;

import io.javalin.http.UnauthorizedResponse;

/**
 * Implementation of the {@link AuthService} for handling user authentication
 * and email confirmation.
 */
public class AuthServiceImpl implements AuthService {
    private final UserService userService;
    private final EmailService emailService;
    private final TokenService tokenService;
    private final JWTService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final EmailContentBuilder emailContentBuilder;

    /**
     * Constructs an {@link AuthServiceImpl} with the necessary services.
     *
     * @param userService         The service for user-related operations.
     * @param emailService        The service for sending emails.
     * @param tokenService        The service for token generation and validation.
     * @param emailContentBuilder The utility for building email contexts.
     */
    public AuthServiceImpl(UserService userService, EmailService emailService, TokenService tokenService,
            JWTService jwtService, PasswordEncoder passwordEncoder, EmailContentBuilder emailContentBuilder) {
        this.userService = userService;
        this.emailService = emailService;
        this.jwtService = jwtService;
        this.tokenService = tokenService;
        this.passwordEncoder = passwordEncoder;
        this.emailContentBuilder = emailContentBuilder;
    }

    @Override
    public void signUpUser(UserInputDto userDto) {
        userService.createUser(userDto);
        sendWelcomeEmail(userDto.getId());
    }

    @Override
    public Map<String, String> signInUser(UserInputDto userDto) {
        var user = userService.findUserByEmail(userDto.getEmail())
                .filter(u -> passwordEncoder.matches(userDto.getPassword(), u.getPassword()))
                .orElseThrow(() -> new UnauthorizedResponse("Invalid email or password"));

        Map<String, String> tokens = jwtService.generateTokens(user);
        return tokens;

    }

    @Override
    public void sendConfirmationToken(int userId) {
        var user = userService.findUserById(userId).orElseThrow(() -> new NotFoundException("User does not exist."));
        sendTokenEmail(user, "confirmation", "Confirm Your Email Address", "user/confirm");
    }

    @Override
    public void sendWelcomeEmail(int userId) {
        var user = userService.findUserById(userId).orElseThrow(() -> new NotFoundException("User does not exist."));
        sendTokenEmail(user, "confirmation", "Welcome to Oasis Nourish", "user/welcome");
    }

    @Override
    public void confirmAccount(int userId, String token) {
        tokenService.verifyTokenOrThrow(userId, "confirmation", token);

        userService.findUserById(userId).ifPresent(user -> {
            userService.verifyEmail(user.getEmail());
            tokenService.revokeToken(userId, "confirmation");
        });
    }

    @Override
    public void sendResetPasswordToken(String email) {
        var user = userService.findUserByEmail(email).orElseThrow(() -> new NotFoundException("User does not exist."));
        sendTokenEmail(user, "reset-password", "Reset your password", "user/reset-password");
    }

    @Override
    public void resetPassword(int userId, String token, String password) {
        tokenService.verifyTokenOrThrow(userId, "reset-password", token);

        userService.findUserById(userId).ifPresent(user -> {
            userService.updatePassword(user.getId(), password);
            tokenService.revokeToken(user.getId(), "reset-password");
        });
    }

    /**
     * Sends an email with a generated token to the user.
     *
     * @param user     the user to whom the email is sent
     * @param type     the type of token to generate
     * @param subject  the subject of the email
     * @param template the template to use for the email body
     */
    private void sendTokenEmail(User user, String type, String subject, String template) {
        String token = tokenService.generateToken(user.getId(), type);
        var context = emailContentBuilder.buildConfirmationContext(user, token);
        emailService.sendEmail(user.getEmail(), subject, template, context);
    }

    @Override
    public Optional<Map<String, String>> updateSignedInUserIfChanged(User signedInUser) {
        if (signedInUser != null) {
            var user = userService.findUserById(signedInUser.getId());
            if (user.isPresent() && !signedInUser.equals(signedInUser)) {
                Map<String, String> tokens = jwtService.generateTokens(user.get());
                return Optional.of(tokens);
            }
        }
        return Optional.empty();
    }
}
