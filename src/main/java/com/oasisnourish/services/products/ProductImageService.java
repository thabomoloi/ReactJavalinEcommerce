package com.oasisnourish.services.products;

import java.util.List;
import java.util.Optional;

import com.oasisnourish.dto.products.ProductImageDto;
import com.oasisnourish.models.products.ProductImage;

import io.javalin.http.UploadedFile;

public interface ProductImageService {

    Optional<ProductImage> findProductImageById(int productImageId);

    List<ProductImage> findAllProductImages(int productId);

    void createProductImages(List<UploadedFile> productImages, int productId);

    void updateProductImages(List<ProductImageDto> productImages);

    void deleteProductImage(int productImageId);
}
