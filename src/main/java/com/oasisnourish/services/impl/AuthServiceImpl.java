package com.oasisnourish.services.impl;

import java.util.Optional;

import com.oasisnourish.dto.UserInputDto;
import com.oasisnourish.exceptions.InvalidTokenException;
import com.oasisnourish.models.User;
import com.oasisnourish.services.AuthService;
import com.oasisnourish.services.EmailService;
import com.oasisnourish.services.TokenService;
import com.oasisnourish.services.UserService;
import com.oasisnourish.util.EmailContentBuilder;
import com.oasisnourish.util.PasswordUtil;

/**
 * Implementation of the {@link AuthService} for handling user authentication
 * and email confirmation.
 */
public class AuthServiceImpl implements AuthService {
    private final UserService userService;
    private final EmailService emailService;
    private final TokenService tokenService;
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
            EmailContentBuilder emailContentBuilder) {
        this.userService = userService;
        this.emailService = emailService;
        this.tokenService = tokenService;
        this.emailContentBuilder = emailContentBuilder;
    }

    @Override
    public Optional<User> signUpUser(UserInputDto userDto) {
        userService.createUser(userDto);
        return userService.findUserByEmail(userDto.getEmail());
    }

    @Override
    public Optional<User> signInUser(UserInputDto userDto) {
        Optional<User> user = userService.findUserByEmail(userDto.getEmail());
        if (user.isPresent()) {
            if (PasswordUtil.checkPassword(userDto.getPassword(), user.get().getPassword())) {
                return user;
            }
        }
        return Optional.empty();
    }

    @Override
    public void sendConfirmationToken(User user) {
        String token = tokenService.generateToken(user.getId(), "confirmation");
        var context = emailContentBuilder.buildConfirmationContext(user, token);
        emailService.sendEmail(user.getEmail(), "Confirm Your Email Address", "user/confirm", context);
    }

    @Override
    public void sendWelcomeEmail(User user) {
        String token = tokenService.generateToken(user.getId(), "confirmation");
        var context = emailContentBuilder.buildConfirmationContext(user, token);
        emailService.sendEmail(user.getEmail(), "Welcome to Oasis Nourish", "user/welcome", context);
    }

    @Override
    public void confirmAccount(int userId, String token) {
        boolean validToken = tokenService.verifyToken(userId, "confirmation", token);
        if (validToken) {
            userService.findUserById(userId).ifPresent(user -> {
                userService.verifyEmail(user.getEmail());
            });
            tokenService.revokeToken(userId, "confirmation");
        } else {
            throw new InvalidTokenException(
                    "The confirmation token is either invalid or has expired. Please requested another one.");
        }
    }

    @Override
    public void resetPassword(int userId, String token, String password) {
        boolean validToken = tokenService.verifyToken(userId, "reset-password", token);
        if (validToken) {
            userService.findUserById(userId).ifPresent(user -> {
                user.setPassword(PasswordUtil.hashPassword(password));
                userService.updatePassword(user);

            });
            tokenService.revokeToken(userId, "reset-password");
        } else {
            throw new InvalidTokenException(
                    "The reset-password token is either invalid or has expired. Please requested another one.");
        }
    }

    @Override
    public void sendResetPasswordToken(User user) {
        String token = tokenService.generateToken(user.getId(), "reset-password");
        var context = emailContentBuilder.buildConfirmationContext(user, token);
        emailService.sendEmail(user.getEmail(), "Reset your password", "user/reset-password", context);
    }

}
