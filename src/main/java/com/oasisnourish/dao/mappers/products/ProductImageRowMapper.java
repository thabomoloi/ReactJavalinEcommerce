package com.oasisnourish.dao.mappers.products;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.oasisnourish.dao.mappers.EntityRowMapper;
import com.oasisnourish.models.products.ProductImage;

public class ProductImageRowMapper implements EntityRowMapper<ProductImage> {

    @Override
    public ProductImage mapToEntity(ResultSet resultSet) throws SQLException {
        ProductImage productImage = new ProductImage();
        productImage.setId(resultSet.getInt("id"));
        productImage.setUrl(resultSet.getString("url"));
        productImage.setOrder(resultSet.getInt("order"));
        productImage.setProductId(resultSet.getInt("product_id"));
        return productImage;
    }

    @Override
    public void mapToRow(PreparedStatement statement, ProductImage image, boolean includeId) throws SQLException {
        if (includeId) { // Update
            statement.setInt(1, image.getOrder());
            statement.setInt(2, image.getId());
        } else { // Save
            statement.setInt(1, image.getId());
            statement.setString(2, image.getUrl());
            statement.setInt(3, image.getOrder());
            statement.setInt(4, image.getProductId());
        }
    }

}
