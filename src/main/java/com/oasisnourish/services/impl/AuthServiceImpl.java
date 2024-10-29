package com.oasisnourish.services.impl;

import java.util.Optional;

import com.oasisnourish.dto.UserInputDto;
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
        var context = emailContentBuilder.buildWelcomeContext(user);
        emailService.sendEmail(user.getEmail(), "Welcome to Oasis Nourish", "user/welcome", context);
    }

}
