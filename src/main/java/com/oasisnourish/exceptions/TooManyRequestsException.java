package com.oasisnourish.exceptions;

/**
 * Exception thrown when a user exceeds the allowed number of requests within a
 * specified timeframe.
 * Typically used for rate-limiting functionality to prevent abuse or excessive
 * requests.
 */
public class TooManyRequestsException extends RuntimeException {

    /**
     * Constructs a new {@code TooManyRequestsException} with the specified detail
     * message.
     *
     * @param message the detail message explaining the reason for the exception.
     */
    public TooManyRequestsException(String message) {
        super(message);
    }
}
