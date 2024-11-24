package com.oasisnourish.dto.validation;

import com.oasisnourish.dto.users.UserInputDto;
import com.oasisnourish.dto.users.UserInputDtoValidator;

import io.javalin.validation.BodyValidator;

public class ValidatorFactory {

    public static UserInputDtoValidator getValidator(BodyValidator<UserInputDto> bodyValidator) {
        return new UserInputDtoValidator(new DtoValidatorImpl<>(bodyValidator));
    }
}
