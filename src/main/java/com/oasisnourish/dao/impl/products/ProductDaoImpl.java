package com.oasisnourish.dao.impl.products;

import java.util.List;
import java.util.Optional;

import com.oasisnourish.dao.impl.AbstractDao;
import com.oasisnourish.dao.mappers.EntityRowMapper;
import com.oasisnourish.dao.products.ProductDao;
import com.oasisnourish.db.JdbcConnection;
import com.oasisnourish.models.products.Product;

public class ProductDaoImpl extends AbstractDao<Product> implements ProductDao {

    private static final String FIND_BY_ID = "SELECT * FROM products WHERE id = ?";
    private static final String FIND_BY_CODE = "SELECT * FROM products WHERE code = ?";
    private static final String FIND_ALL = "SELECT * FROM products";
    private static final String INSERT = "INSERT INTO products (code, name, description, ribbon) VALUES (?, ?, ?, ?)";
    private static final String UPDATE = "UPDATE products SET code = ?, name = ?, description = ?, ribbon = ? WHERE id = ?";
    private static final String DELETE = "DELETE FROM products WHERE id = ?";

    public ProductDaoImpl(JdbcConnection jdbcConnection, EntityRowMapper<Product> entityRowMapper) {
        super(jdbcConnection, entityRowMapper);
    }

    @Override
    public Optional<Product> find(int id) {
        return querySingle(FIND_BY_ID, ps -> ps.setInt(1, id));
    }

    @Override
    public List<Product> findAll() {
        return queryList(FIND_ALL, _ -> {
        });
    }

    @Override
    public void save(Product product) {
        executeUpdate(INSERT, ps -> entityRowMapper.mapToRow(ps, product, false), rs -> product.setId(rs.getInt(1)));
    }

    @Override
    public void update(Product product) {
        executeUpdate(UPDATE, ps -> entityRowMapper.mapToRow(ps, product, true), null);
    }

    @Override
    public void delete(int id) {
        executeUpdate(DELETE, ps -> ps.setInt(1, id), null);
    }

    @Override
    public Optional<Product> findByCode(String code) {
        return querySingle(FIND_BY_CODE, ps -> ps.setString(1, code));
    }

}
