package com.oasisnourish.dto.products;

import com.oasisnourish.dto.validation.DtoValidator;

public class ProductImageDtoValidator {

    private final DtoValidator<ProductImageDto> validator;

    public ProductImageDtoValidator(DtoValidator<ProductImageDto> validator) {
        this.validator = validator;
    }

    public ProductImageDto get() {
        return validator.get();
    }

    public ProductImageDtoValidator isUrlRequired() {
        validator.check("url", (productImage) -> {
            String url = productImage.getUrl();
            return url != null && !url.trim().isEmpty();
        }, "Product image URL is required");
        return this;
    }

}
