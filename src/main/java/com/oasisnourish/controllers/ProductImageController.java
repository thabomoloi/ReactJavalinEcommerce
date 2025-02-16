package com.oasisnourish.controllers;

import com.oasisnourish.services.products.ProductImageService;

import io.javalin.apibuilder.CrudHandler;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;

public class ProductImageController implements CrudHandler {

    private final ProductImageService productImageService;

    public ProductImageController(ProductImageService productImageService) {
        this.productImageService = productImageService;
    }

    @Override
    public void getAll(Context ctx) {
        int productId = ctx.pathParamAsClass("productId", Integer.class).get();
        var products = productImageService.findAllProductImages(productId);
        ctx.status(HttpStatus.OK);
        ctx.json(products);
    }

    @Override
    public void getOne(Context ctx, String string) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void create(Context ctx) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void update(Context ctx, String string) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void delete(Context ctx, String string) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
