package com.oasisnourish.dao.impl.products;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import com.oasisnourish.dao.impl.DaoTestHelper;
import com.oasisnourish.models.products.Product;

@ExtendWith(MockitoExtension.class)
public class ProductDaoImplTest extends DaoTestHelper<Product> {

    private static final String FIND_BY_ID = "SELECT * FROM products WHERE id = ?";
    private static final String FIND_BY_CODE = "SELECT * FROM products WHERE code = ?";
    private static final String FIND_ALL = "SELECT * FROM products";
    private static final String INSERT = "INSERT INTO products (code, name, description, ribbon) VALUES (?, ?, ?, ?)";
    private static final String UPDATE = "UPDATE products SET code = ?, name = ?, description = ?, ribbon = ? WHERE id = ?";
    private static final String DELETE = "DELETE FROM products WHERE id = ?";

    @InjectMocks
    private ProductDaoImpl productDao;

    private final List<Product> products = Arrays.asList(
            new Product(1, "CODE01", "Product 1", "Description 1", "ribbon1"),
            new Product(2, "CODE02", "Product 2", "Description 2", "ribbon2"),
            new Product(3, "CODE03", "Product 3", "Description 3", "ribbon3")
    );

    @BeforeEach
    @Override
    protected void setUp() throws SQLException {
        super.setUp();
    }

    @Test
    void testFindById_Exists() throws SQLException {
        Product expected = products.get(0);
        mockEntityExists(FIND_BY_ID, expected);
        assertEntityExists(expected, productDao.find(expected.getId()));

    }

    @Test
    void testFindById_DoesNotExist() throws SQLException {
        mockEntityDoesNotExist(FIND_BY_ID);
        assertEntityDoesNotExist(productDao.find(1));
    }

    @Test
    void testFindByCode_Exists() throws SQLException {
        Product expected = products.get(0);
        mockEntityExists(FIND_BY_CODE, expected);
        assertEntityExists(expected, productDao.findByCode(expected.getCode()));

    }

    @Test
    void testFindByCode_DoesNotExist() throws SQLException {
        mockEntityDoesNotExist(FIND_BY_CODE);
        assertEntityDoesNotExist(productDao.findByCode("CODE01"));
    }

    @Test
    void testFindAll_Exists() throws SQLException {
        mockEntityList(FIND_ALL, products);
        assertEntityListEquals(products, productDao.findAll());
    }

    @Test
    void testFindAll_DoesNotExist() throws SQLException {
        mockEntityEmpyList(FIND_ALL);
        assertEntityListEquals(Arrays.asList(), productDao.findAll());
    }

    @Test
    void testSave() throws SQLException {
        int expectedId = 5;
        var product = new Product(0, "CODE01", "Product 1", "Description 1", "ribbon1");

        mockEntitySave(INSERT, expectedId);
        productDao.save(product);
        assertEntitySaved(expectedId, product.getId(), product);

    }

    @Test
    void testUpdate() throws SQLException {
        var product = products.get(0);
        mockEntityUpdate(UPDATE);
        productDao.update(product);
        assertEntityUpdated(product);
    }

    @Test
    void testDelete() throws SQLException {
        mockEntityDelete(DELETE);
        productDao.delete(1);
        assertEntityDeleted(1);
    }
}
