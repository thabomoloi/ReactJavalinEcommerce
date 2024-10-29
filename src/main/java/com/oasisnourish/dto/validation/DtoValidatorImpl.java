package com.oasisnourish.dto.validation;

import io.javalin.validation.BodyValidator;

/**
 * A Javalin-specific implementation of the {@link DtoValidator} interface.
 *
 * @param <T> the type of object to validate
 */
public class DtoValidatorImpl<T> implements DtoValidator<T> {
    private final BodyValidator<T> validator;

    /**
     * Constructs a new {@link DtoValidatorImpl}.
     *
     * @param validator the Javalin {@link BodyValidator} to use for validation
     */
    public DtoValidatorImpl(BodyValidator<T> validator) {
        this.validator = validator;
    }

    @Override
    public T get() {
        return validator.get();
    }

    @Override
    public DtoValidator<T> check(String fieldName, Condition<T> condition, String message) {
        validator.check(fieldName, (user) -> condition.test(user), message);
        return this;
    }
}