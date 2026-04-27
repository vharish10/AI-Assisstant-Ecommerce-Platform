package com.Revature.Ecommerce.Platform;

import com.Revature.Ecommerce.Platform.CustomExceptions.*;
import com.Revature.Ecommerce.Platform.models.Products;
import com.Revature.Ecommerce.Platform.repository.ProductRepository;
import com.Revature.Ecommerce.Platform.service.ProductService;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.data.domain.*;

import java.util.*;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository repository;

    @InjectMocks
    private ProductService service;

    private Products product;

    @BeforeEach
    void setUp() {
        product = Products.builder()
                .id("1")
                .name("iPhone 15")
                .category("Mobile")
                .brand("Apple")
                .price(80000.0)
                .stock(10)
                .sellerId("seller123")   // ✅ added (required in service)
                .build();
    }

    // 🟢 CREATE
    @Test
    void testCreateProduct() {
        when(repository.save(product)).thenReturn(product);

        Products saved = service.createProduct(product);

        Assertions.assertNotNull(saved);
        Assertions.assertEquals("iPhone 15", saved.getName());
        verify(repository, times(1)).save(product);
    }

    // 🟢 GET BY ID SUCCESS
    @Test
    void testGetProductById_Success() {
        when(repository.findById("1")).thenReturn(Optional.of(product));

        Products result = service.getProductById("1");

        Assertions.assertEquals("iPhone 15", result.getName());
    }

    // 🔴 GET BY ID NOT FOUND
    @Test
    void testGetProductById_NotFound() {
        when(repository.findById("1")).thenReturn(Optional.empty());

        Assertions.assertThrows(ProductNotFoundException.class,
                () -> service.getProductById("1"));
    }

    // 🟢 DELETE SUCCESS
    @Test
    void testDeleteProduct() {
        when(repository.findById("1")).thenReturn(Optional.of(product));

        service.deleteProduct("1", "seller123");

        verify(repository, times(1)).deleteById("1");
    }

    // 🔴 DELETE NOT AUTHORIZED
    @Test
    void testDeleteProduct_Unauthorized() {
        when(repository.findById("1")).thenReturn(Optional.of(product));

        Assertions.assertThrows(UnauthorizedException.class,
                () -> service.deleteProduct("1", "wrongSeller"));
    }

    // 🟢 SEARCH BY CATEGORY
    @Test
    void testSearchByCategory() {

        Page<Products> page = new PageImpl<>(List.of(product));

        when(repository.findByCategory(eq("Mobile"), any(Pageable.class)))
                .thenReturn(page);

        Page<Products> result = service.searchProducts(
                null, "Mobile", null,
                null, null,
                null, null, null,   // sellerId added
                0, 10,
                "price", "asc"
        );

        Assertions.assertEquals(1, result.getTotalElements());
    }

    // 🟢 SEARCH BY KEYWORD
    @Test
    void testSearchByKeyword() {

        Page<Products> page = new PageImpl<>(List.of(product));

        when(repository.findByNameContainingIgnoreCase(eq("iphone"), any(Pageable.class)))
                .thenReturn(page);

        Page<Products> result = service.searchProducts(
                "iphone", null, null,
                null, null,
                null, null, null,   // sellerId added
                0, 10,
                "price", "asc"
        );

        Assertions.assertEquals(1, result.getContent().size());
    }
}