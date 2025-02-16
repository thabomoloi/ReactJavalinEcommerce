package com.oasisnourish.services.impl.products;

import java.util.List;
import java.util.Optional;

import com.oasisnourish.dao.products.ProductImageDao;
import com.oasisnourish.dto.products.ProductImageDto;
import com.oasisnourish.exceptions.NotFoundException;
import com.oasisnourish.models.products.ProductImage;
import com.oasisnourish.services.products.ProductImageService;

import io.javalin.http.UploadedFile;

public class ProductImageServiceImpl implements ProductImageService {

    private final ProductImageDao productImageDao;

    public ProductImageServiceImpl(ProductImageDao productImageDao) {
        this.productImageDao = productImageDao;
    }

    @Override
    public Optional<ProductImage> findProductImageById(int productImageId) {
        return productImageDao.find(productImageId);
    }

    @Override
    public List<ProductImage> findAllProductImages(int productId) {
        return productImageDao.findByProductId(productId);
    }

    @Override
    public void createProductImages(List<UploadedFile> productImages, int productId) {
        // TODO: upload to cloudinary
        // take the url save save the product image
    }

    @Override
    public void updateProductImages(List<ProductImageDto> productImages) {
        ProductImage productImage;
        ProductImageDto productImageDto;

        for (int i = 1, n = productImages.size(); i <= n; i++) {
            productImageDto = productImages.get(i);
            productImage = productImageDao.findByUrl(productImageDto.getUrl()).orElseThrow(() -> new NotFoundException("The product image does not exist."));
            productImage.setNumber(i);
            productImageDao.save(productImage);
        }
    }

    @Override
    public void deleteProductImage(int productImageId) {
        productImageDao.find(productImageId).orElseThrow(() -> new NotFoundException("The product image does not exist."));
        productImageDao.delete(productImageId);
    }

}
