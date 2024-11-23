package com.oasisnourish.dao.impl.products;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.oasisnourish.dao.mappers.EntityRowMapper;
import com.oasisnourish.db.JdbcConnection;
import com.oasisnourish.models.products.ProductImage;

@ExtendWith(MockitoExtension.class)
public class ProductImageDaoImplTest {

    private static final String FIND_BY_ID = "SELECT * FROM product_images WHERE id = ?";
    private static final String FIND_BY_PRODUCT_ID = "SELECT * FROM product_images WHERE product_id = ?";
    private static final String FIND_BY_URL = "SELECT * FROM product_images WHERE url = ?";
    private static final String FIND_ALL = "SELECT * FROM product_images";
    private static final String INSERT = "INSERT INTO product_images (url, number, product_id) VALUES (?, ?, ?)";
    private static final String UPDATE = "UPDATE product_images SET number = ? WHERE id = ?";
    private static final String DELETE = "DELETE FROM product_images WHERE id = ?";

    @Mock
    private JdbcConnection jdbcConnection;

    @Mock
    private Connection connection;

    @Mock
    private PreparedStatement ps;

    @Mock
    private ResultSet rs;

    @Mock
    private EntityRowMapper<ProductImage> entityRowMapper;

    @InjectMocks
    private ProductImageDaoImpl productImageDao;

    private final ProductImage image = new ProductImage(1, "https://url.example", 2, 123);

    @BeforeEach
    public void setUp() throws SQLException {
        when(jdbcConnection.getConnection()).thenReturn(connection);
    }

    void testFindById() {

    }

}
