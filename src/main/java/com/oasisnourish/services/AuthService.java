package com.oasisnourish.services;

import java.util.Optional;

import com.oasisnourish.dto.UserInputDto;
import com.oasisnourish.dto.UserInputDto;
import com.oasisnourish.models.User;

/**
 * Interface for authentication services related to user operations such as
 * sign-up, sign-in, and sending confirmation tokens.
 */
public interface AuthService {

    /**
     * Registers a new user in the system by validating and saving the provided user
     * data. After successful registration, a confirmation token is generated and
     * sent to the user’s email.
     * 
     * @param userDto The data transfer object containing the user's name, email,
     *                and password for registration. It must pass validation checks
     *                such as email format and password strength.
     * @return An {@link Optional} containing the registered {@link User} object if
     *         registration is successful, or an empty {@link Optional} if
     *         registration fails.
     */
    Optional<User> signUpUser(UserInputDto userDto);

    /**
     * Authenticates a user based on the provided email and password credentials.
     * Checks the provided password against the stored hashed password and returns
     * the authenticated user.
     *
     * @param userDto The data transfer object containing the user's email and
     *                password for authentication.
     * @return An {@link Optional} containing the authenticated {@link User} object
     *         if authentication is successful, or an empty {@link Optional} if
     *         authentication fails due to incorrect credentials.
     */
    Optional<User> signInUser(UserInputDto userDto);

    /**
     * Sends a confirmation token to the user's email address.
     *
     * @param user The user for whom the confirmation token is generated.
     */
    void sendConfirmationToken(User user);

    /**
     * Sends a welcome email to new user.
     *
     * @param user The new user.
     */
    void sendWelcomeEmail(User user);
}
