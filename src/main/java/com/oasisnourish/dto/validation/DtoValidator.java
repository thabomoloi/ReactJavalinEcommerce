package com.oasisnourish.dto.validation;

/**
 * A generic interface for validating objects.
 *
 * @param <T> the type of object to validate
 */
public interface DtoValidator<T> {

    /**
     * Retrieves the validated object.
     *
     * @return the validated object
     */
    T get();

    /**
     * Adds a validation check with a specified condition and error message.
     *
     * @param condition the condition to check
     * @param message   the error message if the condition fails
     * @return the current instance of the Validator for method chaining
     */
    DtoValidator<T> check(Condition<T> condition, String message);

    /**
     * Functional interface representing a condition for validation.
     *
     * @param <T> the type of object to validate
     */
    @FunctionalInterface
    interface Condition<T> {

        /**
         * Tests the specified object against the condition.
         *
         * @param t the object to test
         * @return true if the object meets the condition; false otherwise
         */
        boolean test(T t);
    }
}
