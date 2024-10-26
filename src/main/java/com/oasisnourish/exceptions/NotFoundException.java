package com.oasisnourish.exceptions;

/**
 * Exception thrown when an entity is not found in the database.
 * <p>
 * This exception is typically used to indicate that a requested entity does not
 * exist in the system, for example, when trying to retrieve user details by ID.
 * </p>
 */
public class NotFoundException extends RuntimeException {
    /**
     * Constructs a new {@link NotFoundException} with the specified detail message.
     *
     * @param message the detail message explaining the reason for the exception
     *                (may be null).
     */
    public NotFoundException(String message) {
        super(message);
    }
}