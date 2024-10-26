package com.oasisnourish.exceptions;

/**
 * Exception thrown when an email already exists in the database.
 * <p>
 * This exception is typically used during user registration or updates to
 * signal that the provided email address is already associated with an existing
 * user.
 * </p>
 */
public class EmailExistsException extends RuntimeException {
    /**
     * Constructs a new {@link EmailExistsException} with the specified detail
     * message.
     *
     * @param message the detail message explaining the reason for the exception
     *                (may be null).
     */
    public EmailExistsException(String message) {
        super(message);
    }
}