package com.oasisnourish.dao.impl.products;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import com.oasisnourish.dao.impl.DaoTestHelper;
import com.oasisnourish.models.products.ProductImage;

@ExtendWith(MockitoExtension.class)
public class ProductImageDaoImplTest extends DaoTestHelper<ProductImage> {

    private static final String FIND_BY_ID = "SELECT * FROM product_images WHERE id = ?";
    private static final String FIND_BY_PRODUCT_ID = "SELECT * FROM product_images WHERE product_id = ?";
    private static final String FIND_BY_URL = "SELECT * FROM product_images WHERE url = ?";
    private static final String FIND_ALL = "SELECT * FROM product_images";
    private static final String INSERT = "INSERT INTO product_images (url, number, product_id) VALUES (?, ?, ?)";
    private static final String UPDATE = "UPDATE product_images SET number = ? WHERE id = ?";
    private static final String DELETE = "DELETE FROM product_images WHERE id = ?";

    @InjectMocks
    private ProductImageDaoImpl productImageDao;

    private final List<ProductImage> images = Arrays.asList(
            new ProductImage(1, "https://url1.example", 1, 123),
            new ProductImage(2, "https://url2.example", 2, 123),
            new ProductImage(3, "https://url3.example", 1, 124)
    );

    @BeforeEach
    @Override
    protected void setUp() throws SQLException {
        super.setUp();
    }

    @Test
    void testFindById_Exists() throws SQLException {
        ProductImage expected = images.get(0);
        mockEntityExists(FIND_BY_ID, expected);
        assertEntityExists(expected, productImageDao.find(expected.getId()));

    }

    @Test
    void testFindById_DoesNotExist() throws SQLException {
        mockEntityDoesNotExist(FIND_BY_ID);
        assertEntityDoesNotExist(productImageDao.find(1));
    }

    @Test
    void testFindByUrl_Exists() throws SQLException {
        ProductImage expected = images.get(0);
        mockEntityExists(FIND_BY_URL, expected);
        assertEntityExists(expected, productImageDao.findByUrl(expected.getUrl()));

    }

    @Test
    void testFindByUrl_DoesNotExist() throws SQLException {
        mockEntityDoesNotExist(FIND_BY_URL);
        assertEntityDoesNotExist(productImageDao.findByUrl("https://noimage.test"));
    }

    @Test
    void testFindAll_Exists() throws SQLException {
        mockEntityList(FIND_ALL, images);
        assertEntityListEquals(images, productImageDao.findAll());
    }

    @Test
    void testFindAll_DoesNotExist() throws SQLException {
        mockEntityEmpyList(FIND_ALL);
        assertEntityListEquals(Arrays.asList(), productImageDao.findAll());
    }

    @Test
    void testFindByProductId_Exists() throws SQLException {
        int productId = 123;
        var filteredImages = images.stream().filter(image -> image.getProductId() == productId).collect(Collectors.toList());
        mockEntityList(FIND_BY_PRODUCT_ID, filteredImages);
        assertEntityListEquals(filteredImages, productImageDao.findByProductId(productId));

    }

    @Test
    void testFindByProductId_DoesNotExist() throws SQLException {
        mockEntityEmpyList(FIND_BY_PRODUCT_ID);
        assertEntityListEquals(Arrays.asList(), productImageDao.findByProductId(123));
    }

    @Test
    void testSave() throws SQLException {
        int expectedId = 5;
        var image = new ProductImage(0, "https://url1.example", 1, 123);

        mockEntitySave(INSERT, expectedId);
        productImageDao.save(image);
        assertEntitySaved(expectedId, image.getId(), image);

    }

    @Test
    void testUpdate() throws SQLException {
        var image = images.get(0);
        mockEntityUpdate(UPDATE);
        productImageDao.update(image);
        assertEntityUpdated(image);
    }

    @Test
    void testDelete() throws SQLException {
        mockEntityDelete(DELETE);
        productImageDao.delete(1);
        assertEntityDeleted(1);
    }
}
