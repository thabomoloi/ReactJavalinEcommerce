package com.oasisnourish.dao.impl.products;

import java.util.List;
import java.util.Optional;

import com.oasisnourish.dao.impl.AbstractDao;
import com.oasisnourish.dao.mappers.EntityRowMapper;
import com.oasisnourish.dao.products.ProductImageDao;
import com.oasisnourish.db.JdbcConnection;
import com.oasisnourish.models.products.ProductImage;

public class ProductImageDaoImpl extends AbstractDao<ProductImage> implements ProductImageDao {

    private static final String FIND_BY_ID = "SELECT * FROM product_images WHERE id = ?";
    private static final String FIND_BY_PRODUCT_ID = "SELECT * FROM product_images WHERE product_id = ?";
    private static final String FIND_BY_URL = "SELECT * FROM product_images WHERE url = ?";
    private static final String FIND_ALL = "SELECT * FROM product_images";
    private static final String INSERT = "INSERT INTO product_images (url, number, product_id) VALUES (?, ?, ?)";
    private static final String UPDATE = "UPDATE product_images SET number = ? WHERE id = ?";
    private static final String DELETE = "DELETE FROM product_images WHERE id = ?";

    public ProductImageDaoImpl(JdbcConnection jdbcConnection, EntityRowMapper<ProductImage> entityRowMapper) {
        super(jdbcConnection, entityRowMapper);
    }

    @Override
    public List<ProductImage> findByProductId(int productId) {
        return queryList(FIND_BY_PRODUCT_ID, ps -> ps.setInt(1, productId));
    }

    @Override
    public Optional<ProductImage> findByUrl(String url) {
        return querySingle(FIND_BY_URL, ps -> ps.setString(1, url));
    }

    @Override
    public Optional<ProductImage> find(int id) {
        return querySingle(FIND_BY_ID, ps -> ps.setInt(1, id));
    }

    @Override
    public List<ProductImage> findAll() {
        return queryList(FIND_ALL, _ -> {
        });
    }

    @Override
    public void save(ProductImage image) {
        executeUpdate(INSERT, ps -> entityRowMapper.mapToRow(ps, image, false), rs -> image.setId(rs.getInt(1)));
    }

    @Override
    public void update(ProductImage image) {
        executeUpdate(UPDATE, ps -> entityRowMapper.mapToRow(ps, image, true), null);

    }

    @Override
    public void delete(int id) {
        executeUpdate(DELETE, ps -> ps.setInt(1, id), null);
    }

}
