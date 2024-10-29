package com.oasisnourish.dto.validation;

import com.oasisnourish.dto.UserInputDto;

/**
 * Validator for {@link UserInputDto} objects, extending
 * {@link UserInputDtoValidator}.
 */
public class UserInputDtoValidator {

    private final DtoValidator<UserInputDto> validator;

    /**
     * Constructs a new {@link UserInputDtoValidator}.
     *
     * @param validator the {@link DtoValidator} to use for validation
     */
    public UserInputDtoValidator(DtoValidator<UserInputDto> validator) {
        this.validator = validator;
    }

    /**
     * Checks if the name field is required and non-empty.
     *
     * @return the current instance of {@link UserInputDtoValidator} for method
     *         chaining
     */
    public UserInputDtoValidator isNameRequired() {
        validator.check((user) -> {
            String name = user.getName(); // Assuming UserInputDto has a getName() method
            return name != null && !name.trim().isEmpty();
        }, "Name is required.");
        return this;
    }

    /**
     * Retrieves the validated {@link UserInputDto} object.
     *
     * @return the validated {@link UserInputDto}
     */
    public UserInputDto get() {
        return validator.get();
    }

    /**
     * Checks if the email field is required and non-empty.
     *
     * @return the current instance of {@link UserInputDtoValidator} for method
     *         chaining
     */
    public UserInputDtoValidator isEmailRequired() {
        validator.check((user) -> {
            String email = user.getEmail();
            return email != null && !email.trim().isEmpty();
        }, "Email is required.");
        return this;
    }

    /**
     * Validates the format of the email address.
     *
     * @return the current instance of {@link UserInputDtoValidator} for method
     *         chaining
     */
    public UserInputDtoValidator isEmailValid() {
        validator.check((user) -> {
            String email = user.getEmail();
            return email != null && email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
        }, "Invalid email address.");
        return this;
    }

    /**
     * Checks if the password field is required and non-empty.
     *
     * @return the current instance of {@link UserInputDtoValidator} for method
     *         chaining
     */
    public UserInputDtoValidator isPasswordRequired() {
        validator.check((user) -> {
            String password = user.getPassword();
            return password != null && !password.trim().isEmpty();
        }, "Password is required.");
        return this;
    }

    /**
     * Validates the length of the password.
     *
     * @return the current instance of {@link UserInputDtoValidator} for method
     *         chaining
     */
    public UserInputDtoValidator isPasswordLengthValid() {
        validator.check((user) -> {
            String password = user.getPassword();
            return password != null && password.length() >= 8 && password.length() <= 16;
        }, "Password must be between 8 and 16 characters.");
        return this;
    }

    /**
     * Validates the format of the password.
     *
     * @return the current instance of {@link UserInputDtoValidator} for method
     *         chaining
     */
    public UserInputDtoValidator isPasswordPatternValid() {
        validator.check((user) -> {
            String password = user.getPassword();
            return password != null && password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=!]).*$");
        }, "Password must contain at least one lowercase letter, one uppercase letter, one digit, and one special character (@, #, $, %, ^, &, +, =, !).");
        return this;
    }
}
