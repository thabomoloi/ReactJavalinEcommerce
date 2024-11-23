package com.oasisnourish.dao.products;

import java.util.Optional;

import com.oasisnourish.dao.Dao;
import com.oasisnourish.models.products.Product;

public interface ProductDao extends Dao<Product> {

    Optional<Product> findByCode(String code);
}
