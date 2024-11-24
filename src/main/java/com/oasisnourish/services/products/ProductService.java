package com.oasisnourish.services.products;

import java.util.List;
import java.util.Optional;

import com.oasisnourish.dto.products.ProductInputDto;
import com.oasisnourish.models.products.Product;

public interface ProductService {

    Optional<Product> findProductById(int id);

    Optional<Product> findProductByCode(String code);

    List<Product> findAllProducts();

    void createProduct(ProductInputDto productDto);

    void updateProduct(ProductInputDto productDto);

    void deleteProduct(int id);

}
