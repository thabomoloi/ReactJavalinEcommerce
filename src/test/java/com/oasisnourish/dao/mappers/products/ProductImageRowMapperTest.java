package com.oasisnourish.dao.mappers.products;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.oasisnourish.models.products.ProductImage;

public class ProductImageRowMapperTest {

    private PreparedStatement ps;

    private ResultSet rs;

    private ProductImageRowMapper entityRowMapper;

    private final ProductImage image = new ProductImage(1, "https://url.example", 1, 1);

    @BeforeEach
    public void setUp() {
        ps = mock(PreparedStatement.class);
        rs = mock(ResultSet.class);
        entityRowMapper = new ProductImageRowMapper();
    }

    @Test
    void testMapToEntity() throws SQLException {
        when(rs.getInt("id")).thenReturn(image.getId());
        when(rs.getString("url")).thenReturn(image.getUrl());
        when(rs.getInt("number")).thenReturn(image.getNumber());
        when(rs.getInt("product_id")).thenReturn(image.getProductId());

        ProductImage result = entityRowMapper.mapToEntity(rs);
        assertEquals(image, result);
    }

    @Test
    void testMapToRow_Insert() throws SQLException {
        entityRowMapper.mapToRow(ps, image, false);
        verify(ps).setString(1, image.getUrl());
        verify(ps).setInt(2, image.getNumber());
        verify(ps).setInt(3, image.getProductId());
    }

    @Test
    void testMapToRow_Update() throws SQLException {
        entityRowMapper.mapToRow(ps, image, true);
        verify(ps).setInt(1, image.getNumber());
        verify(ps).setInt(2, image.getProductId());
    }

}
