package com.oasisnourish.exceptions;

/**
 * Exception thrown when there is a database access error.
 */
public class DatabaseAccessException extends RuntimeException {
    public DatabaseAccessException(String message, Throwable cause) {
        super(message, cause);
    }
}