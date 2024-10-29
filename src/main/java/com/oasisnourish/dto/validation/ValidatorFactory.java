package com.oasisnourish.dto.validation;

import com.oasisnourish.dto.UserInputDto;

import io.javalin.validation.BodyValidator;

public class ValidatorFactory {
    public static UserInputDtoValidator getValidator(BodyValidator<UserInputDto> bodyValidator) {
        return new UserInputDtoValidator(new DtoValidatorImpl<>(bodyValidator));
    }
}
