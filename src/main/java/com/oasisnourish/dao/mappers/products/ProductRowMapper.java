package com.oasisnourish.dao.mappers.products;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.oasisnourish.dao.mappers.EntityRowMapper;
import com.oasisnourish.models.products.Product;

public class ProductRowMapper implements EntityRowMapper<Product> {

    @Override
    public Product mapToEntity(ResultSet resultSet) throws SQLException {
        Product product = new Product();
        product.setId(resultSet.getInt("id"));
        product.setCode(resultSet.getString("code"));
        product.setName(resultSet.getString("name"));
        product.setDescription(resultSet.getString("description"));
        product.setRibbon(resultSet.getString("ribbon"));
        return product;
    }

    @Override
    public void mapToRow(PreparedStatement statement, Product product, boolean includeId) throws SQLException {
        statement.setString(1, product.getCode());
        statement.setString(2, product.getName());
        statement.setString(3, product.getDescription());
        statement.setString(4, product.getRibbon());

        if (includeId) {
            statement.setInt(5, product.getId());
        }
    }

}
