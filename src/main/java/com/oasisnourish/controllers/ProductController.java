package com.oasisnourish.controllers;

import com.oasisnourish.dto.products.ProductInputDto;
import com.oasisnourish.dto.validation.ValidatorFactory;
import com.oasisnourish.exceptions.NotFoundException;
import com.oasisnourish.services.products.ProductService;

import io.javalin.apibuilder.CrudHandler;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;

public class ProductController implements CrudHandler {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @Override
    public void getAll(Context ctx) {
        var products = productService.findAllProducts();
        ctx.status(HttpStatus.OK);
        ctx.json(products);
    }

    @Override
    public void getOne(Context ctx, String id) {
        int productId = ctx.pathParamAsClass("productId", Integer.class).get();
        var product = productService.findProductById(productId).orElseThrow(() -> new NotFoundException("Product does not exist."));
        ctx.status(HttpStatus.OK);
        ctx.json(product);
    }

    @Override
    public void create(Context ctx) {
        var productDto = ValidatorFactory.productInputDtoValidator(ctx.bodyValidator(ProductInputDto.class))
                .isCodeRequired()
                .isNameRequired()
                .get();
        productService.createProduct(productDto);
        ctx.result("Product created successfully.");
    }

    @Override
    public void update(Context ctx, String id) {
        int productId = ctx.pathParamAsClass("productId", Integer.class).get();
        var productDto = ValidatorFactory.productInputDtoValidator(ctx.bodyValidator(ProductInputDto.class))
                .isCodeRequired()
                .isNameRequired()
                .get();
        productDto.setId(productId);
        productService.updateProduct(productDto);
        ctx.result("Product updated successfully.");
    }

    @Override
    public void delete(Context ctx, String id) {
        int productId = ctx.pathParamAsClass("productId", Integer.class).get();
        productService.deleteProduct(productId);
        ctx.status(HttpStatus.NO_CONTENT);
    }

}
