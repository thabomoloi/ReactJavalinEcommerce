package com.oasisnourish.services.users;

import java.util.List;
import java.util.Optional;

import com.oasisnourish.dto.users.UserInputDto;
import com.oasisnourish.models.users.User;

/**
 * Service interface for handling user-related operations.
 */
public interface UserService {

    /**
     * Finds a user by ID.
     *
     * @param id the ID of the user to find.
     * @return an {@link Optional} containing the found user, or empty if not
     * found.
     */
    Optional<User> findUserById(int id);

    /**
     * Retrieves all users.
     *
     * @return a list of all users.
     */
    List<User> findAllUsers();

    /**
     * Saves a new user.
     *
     * @param userDto the {@link UserInputDto} object containing the user's
     * details to save.
     */
    void createUser(UserInputDto userDto);

    /**
     * Updates an existing user.
     *
     * @param userDto the {@link UserInputDto} object containing the updated
     * details of the user.
     */
    void updateUser(UserInputDto userDto);

    /**
     * Deletes a user by ID.
     *
     * @param id the ID of the user to delete.
     */
    void deleteUser(int id);

    /**
     * Finds a user by email.
     *
     * @param email the email of the user to find.
     * @return an {@link Optional} containing the found user, or empty if not
     * found.
     */
    Optional<User> findUserByEmail(String email);

    /**
     * Verifies user's email.
     *
     * @param email the email of the user to verify.
     */
    void verifyEmail(String email);

    void updatePassword(int userId, String password);

}
