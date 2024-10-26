package com.oasisnourish.dao;

import java.util.Optional;

import com.oasisnourish.models.User;

/**
 * Data Access Object (DAO) interface for {@link User} entity.
 * Extends the generic {@link Dao} interface to provide additional
 * methods specific to {@link User} data access.
 */
public interface UserDao extends Dao<User> {

    /**
     * Finds a {@link User} by their email address.
     *
     * @param email the email address of the user to find.
     * @return an Optional containing the {@link User} if found, or an empty
     *         Optional if not found.
     */
    Optional<User> findByEmail(String email);

    /**
     * Verfies a user&apos;s email address (or account).
     * 
     * @param email the email address to verify.
     */
    void verifyEmail(String email);
}