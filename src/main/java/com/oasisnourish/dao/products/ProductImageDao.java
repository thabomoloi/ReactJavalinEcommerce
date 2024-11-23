package com.oasisnourish.dao.products;

import java.util.List;
import java.util.Optional;

import com.oasisnourish.dao.Dao;
import com.oasisnourish.models.products.ProductImage;

public interface ProductImageDao extends Dao<ProductImage> {

    List<ProductImage> findByProductId(int productId);

    Optional<ProductImage> findByUrl(String url);
}
