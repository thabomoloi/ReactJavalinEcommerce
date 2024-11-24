package com.oasisnourish.exceptions;

public class ProductCodeExistsException extends RuntimeException {

    public ProductCodeExistsException(String message) {
        super(message);
    }
}
