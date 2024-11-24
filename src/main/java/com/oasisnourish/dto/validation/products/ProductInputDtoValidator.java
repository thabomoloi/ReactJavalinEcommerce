package com.oasisnourish.dto.validation.products;

import com.oasisnourish.dto.products.ProductInputDto;
import com.oasisnourish.dto.validation.DtoValidator;

public class ProductInputDtoValidator {

    private final DtoValidator<ProductInputDto> validator;

    public ProductInputDtoValidator(DtoValidator<ProductInputDto> validator) {
        this.validator = validator;
    }

    public ProductInputDto get() {
        return validator.get();
    }

    public ProductInputDtoValidator isNameRequired() {
        validator.check("name", (product) -> {
            String name = product.getName();
            return name != null && !name.trim().isEmpty();
        }, "Name is required.");
        return this;
    }

    public ProductInputDtoValidator isDescriptionRequired() {
        validator.check("description", (product) -> {
            String description = product.getDescription();
            return description != null && !description.trim().isEmpty();
        }, "Description is required.");
        return this;
    }

    public ProductInputDtoValidator isCodeRequired() {
        validator.check("code", (product) -> {
            String code = product.getCode();
            return code != null && !code.trim().isEmpty();
        }, "Code is required.");
        return this;
    }

    public ProductInputDtoValidator isRibbonRequired() {
        validator.check("ribbon", (product) -> {
            String ribbon = product.getRibbon();
            return ribbon != null && !ribbon.trim().isEmpty();
        }, "Ribbon is required.");
        return this;
    }

}
