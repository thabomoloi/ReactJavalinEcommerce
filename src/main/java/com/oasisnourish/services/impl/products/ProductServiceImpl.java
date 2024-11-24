package com.oasisnourish.services.impl.products;

import java.util.List;
import java.util.Optional;

import com.oasisnourish.dao.products.ProductDao;
import com.oasisnourish.dto.products.ProductInputDto;
import com.oasisnourish.exceptions.NotFoundException;
import com.oasisnourish.exceptions.ProductCodeExistsException;
import com.oasisnourish.models.products.Product;
import com.oasisnourish.services.products.ProductService;

public class ProductServiceImpl implements ProductService {

    private final ProductDao productDao;

    public ProductServiceImpl(ProductDao productDao) {
        this.productDao = productDao;
    }

    @Override
    public Optional<Product> findProductById(int id) {
        return productDao.find(id);
    }

    @Override
    public Optional<Product> findProductByCode(String code) {
        return productDao.findByCode(code);
    }

    @Override
    public List<Product> findAllProducts() {
        return productDao.findAll();
    }

    @Override
    public void createProduct(ProductInputDto productDto) {
        productDao.findByCode(productDto.getCode()).ifPresent((product) -> {
            throw new ProductCodeExistsException("The product with code " + product.getCode() + " already exists.");
        });

        Product product = new Product(0, productDto.getCode(), productDto.getName(), productDto.getDescription(), productDto.getRibbon());
        productDao.save(product);
    }

    @Override
    public void updateProduct(ProductInputDto productDto) {
        Product product = productDao.find(productDto.getId()).orElseThrow(() -> new NotFoundException("The product does not exist."));

        product.setCode(productDto.getCode());
        product.setName(productDto.getName());
        product.setDescription(productDto.getDescription());
        product.setRibbon(productDto.getRibbon());

        productDao.update(product);
    }

    @Override
    public void deleteProduct(int id) {
        productDao.find(id).orElseThrow(() -> new NotFoundException("The product does not exist."));
        productDao.delete(id);
    }

}
