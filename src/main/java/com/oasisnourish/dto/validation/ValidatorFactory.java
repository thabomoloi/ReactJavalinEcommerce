package com.oasisnourish.dto.validation;

import com.oasisnourish.dto.products.ProductInputDto;
import com.oasisnourish.dto.products.ProductInputDtoValidator;
import com.oasisnourish.dto.users.UserInputDto;
import com.oasisnourish.dto.users.UserInputDtoValidator;

import io.javalin.validation.BodyValidator;

public class ValidatorFactory {

    public static UserInputDtoValidator userInputDtoValidator(BodyValidator<UserInputDto> bodyValidator) {
        return new UserInputDtoValidator(new DtoValidatorImpl<>(bodyValidator));
    }

    public static ProductInputDtoValidator productInputDtoValidator(BodyValidator<ProductInputDto> bodyValidator) {
        return new ProductInputDtoValidator(new DtoValidatorImpl<>(bodyValidator));
    }

}
