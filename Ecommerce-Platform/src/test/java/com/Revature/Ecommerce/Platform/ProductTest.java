package com.Revature.Ecommerce.Platform;

import com.Revature.Ecommerce.Platform.CustomExceptions.*;
import com.Revature.Ecommerce.Platform.dto.ProductSearchResponseDTO;
import com.Revature.Ecommerce.Platform.models.Products;
import com.Revature.Ecommerce.Platform.repository.ProductRepository;
import com.Revature.Ecommerce.Platform.service.ProductService;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.MongoOperations;

import java.util.*;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ProductTest {

    @Mock
    private ProductRepository repository;

    @Mock
    private MongoOperations mongoTemplate;

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
                .sellerId(2L)
                .build();
    }

    @Test           //Checking if product can be created
    void testCreateProduct() {
        when(repository.save(product)).thenReturn(product);
        Products saved = service.createProduct(product);
        assertNotNull(saved);
        assertEquals("iPhone 15", saved.getName());
        verify(repository, times(1)).save(product);
    }

    @Test           //Testing the product search by Id
    void testGetProductByIdSuccess() {
        when(repository.findById("1")).thenReturn(Optional.of(product));
        Products result = service.getProductById("1");
        assertEquals("iPhone 15", result.getName());
    }

    @Test
    void testGetProductByIdNotFound(){
        when(repository.findById("1")).thenReturn(Optional.empty());
        assertThrows(ProductNotFoundException.class, () -> service.getProductById("1"));
    }

    @Test
    void testDeleteProductSuccess() {
        when(repository.findById("1")).thenReturn(Optional.of(product));
        service.deleteProduct("1", 2L);
        verify(repository, times(1)).deleteById("1");
    }

    @Test
    void testDeleteProductUnauthorized() {
        when(repository.findById("1")).thenReturn(Optional.of(product));
        assertThrows(UnauthorizedException.class, () -> service.deleteProduct("1", 999L));
    }

    @Test
    void testSearchProducts() {

        List<Products> productList = List.of(product);

        when(mongoTemplate.count(any(), eq(Products.class))).thenReturn(1L);
        when(mongoTemplate.find(any(), eq(Products.class))).thenReturn(productList);

        ProductSearchResponseDTO result = service.searchProducts(
                null, "Mobile", null,
                null, null,
                null,
                0, 10,
                "price", "asc"
        );

        assertEquals(1, result.getTotalElements());
        assertEquals("iPhone 15", result.getProducts().get(0).getName());
    }

    @Test
    void testSearchInvalidPriceRange() {

        assertThrows(InvalidFilterException.class, () ->
                service.searchProducts(
                        null, null, null,
                        5000.0, 1000.0,   // invalid
                        null,
                        0, 10,
                        "price", "asc"
                )
        );
    }

    @Test
    void testSearchNoResults() {

        when(mongoTemplate.count(any(), eq(Products.class))).thenReturn(0L);
        when(mongoTemplate.find(any(), eq(Products.class))).thenReturn(Collections.emptyList());

        ProductSearchResponseDTO result = service.searchProducts(
                null, null, null,
                null, null,
                null,
                0, 10,
                "price", "asc"
        );

        assertEquals(0, result.getTotalElements());
        assertTrue(result.getProducts().isEmpty());
    }
}