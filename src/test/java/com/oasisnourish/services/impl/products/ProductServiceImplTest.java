package com.oasisnourish.services.impl.products;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.oasisnourish.dao.products.ProductDao;
import com.oasisnourish.dto.products.ProductInputDto;
import com.oasisnourish.exceptions.NotFoundException;
import com.oasisnourish.exceptions.ProductCodeExistsException;
import com.oasisnourish.models.products.Product;

@ExtendWith(MockitoExtension.class)
public class ProductServiceImplTest {

    @Mock
    private ProductDao productDao;

    @InjectMocks
    private ProductServiceImpl productService;

    private final List<Product> products = Arrays.asList(
            new Product(1, "CODE01", "Product 1", "Description 1", "ribbon1"),
            new Product(2, "CODE02", "Product 2", "Description 2", "ribbon2"),
            new Product(3, "CODE03", "Product 3", "Description 3", "ribbon3")
    );

    @Test
    void testFindProductById() {
        var expected = products.get(0);
        when(productDao.find(expected.getId())).thenReturn(Optional.of(expected));
        var actual = productService.findProductById(expected.getId()).get();
        assertEquals(expected, actual);
    }

    @Test
    void testFindProducByCode() {
        var expected = products.get(0);
        when(productDao.findByCode(expected.getCode())).thenReturn(Optional.of(expected));
        var actual = productService.findProductByCode(expected.getCode()).get();
        assertEquals(expected, actual);
    }

    @Test
    void testFindAllProducts() {
        when(productDao.findAll()).thenReturn(products);
        var allProducts = productService.findAllProducts();
        assertEquals(products, allProducts);
    }

    @Test
    void testCreateProduct_Success() {
        var productDto = new ProductInputDto(0, "CODE01", "Product 1", "Description 1", "ribbon1");
        var expectedProduct = products.get(0);

        when(productDao.findByCode(productDto.getCode())).thenReturn(Optional.empty());
        // Simulate setting id after insert
        doAnswer((invocation) -> {
            Product product = invocation.getArgument(0);
            product.setId(expectedProduct.getId());
            return null;
        }).when(productDao).save(any(Product.class));

        productService.createProduct(productDto);

        ArgumentCaptor<Product> captor = ArgumentCaptor.forClass(Product.class);
        verify(productDao, times(1)).save(captor.capture());

        Product actualProduct = captor.getValue();
        assertEquals(expectedProduct, actualProduct);
    }

    @Test
    void testCreateProduct_ProductCodeExisits() {
        var productDto = new ProductInputDto(0, "CODE01", "Product 1", "Description 1", "ribbon1");
        when(productDao.findByCode(productDto.getCode())).thenReturn(Optional.of(new Product(1, "CODE01", "Product 1", "Description 1", "ribbon1")));

        var exception = assertThrows(ProductCodeExistsException.class, () -> productService.createProduct(productDto));
        assertEquals("The product with code CODE01 already exists.", exception.getMessage());
        verify(productDao, never()).save(any(Product.class));
    }

    @Test
    void testUpdateProduct_Success() {
        var expectedProduct = products.get(2);
        var productDto = new ProductInputDto(expectedProduct.getId(), expectedProduct.getCode(), expectedProduct.getName(), expectedProduct.getDescription(), expectedProduct.getRibbon());

        var product = new Product();
        product.setId(expectedProduct.getId());

        when(productDao.find(productDto.getId())).thenReturn(Optional.of(product));

        productService.updateProduct(productDto);

        ArgumentCaptor<Product> captor = ArgumentCaptor.forClass(Product.class);
        verify(productDao, times(1)).update(captor.capture());

        Product actualProduct = captor.getValue();
        assertEquals(expectedProduct, actualProduct);
    }

    @Test
    void testUpdateProduct_DoesNotExist() {
        var expectedProduct = products.get(2);
        var productDto = new ProductInputDto(expectedProduct.getId(), expectedProduct.getCode(), expectedProduct.getName(), expectedProduct.getDescription(), expectedProduct.getRibbon());

        when(productDao.find(productDto.getId())).thenReturn(Optional.empty());

        var exception = assertThrows(NotFoundException.class, () -> productService.updateProduct(productDto));
        assertEquals("The product does not exist.", exception.getMessage());
        verify(productDao, never()).update(expectedProduct);
    }

    @Test
    void testDeleteProduct_Success() {
        var product = products.get(1);
        when(productDao.find(product.getId())).thenReturn(Optional.of(product));

        productService.deleteProduct(product.getId());
        verify(productDao).delete(product.getId());
    }

    @Test
    void testDeleteProduct_DoesNotExist() {
        var product = products.get(1);
        when(productDao.find(product.getId())).thenReturn(Optional.empty());

        var exception = assertThrows(NotFoundException.class, () -> productService.deleteProduct(product.getId()));
        assertEquals("The product does not exist.", exception.getMessage());
        verify(productDao, never()).delete(product.getId());
    }

}
