package com.oasisnourish.exceptions;

/**
 * Exception thrown when there is a database access error.
 * <p>
 * This exception is typically thrown when an operation on the database fails,
 * such as when a connection cannot be established or an SQL operation results
 * in an error. It wraps the underlying cause of the failure.
 * </p>
 */
public class DatabaseAccessException extends RuntimeException {
    /**
     * Constructs a new {@link DatabaseAccessException} with the specified detail
     * message and cause.
     *
     * @param message the detail message explaining the reason for the exception
     *                (may be null).
     * @param cause   the underlying cause of the exception (may be null).
     */
    public DatabaseAccessException(String message, Throwable cause) {
        super(message, cause);
    }
}