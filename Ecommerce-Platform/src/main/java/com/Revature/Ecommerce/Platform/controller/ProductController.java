package com.Revature.Ecommerce.Platform.controller;

import com.Revature.Ecommerce.Platform.models.Products;
import com.Revature.Ecommerce.Platform.service.ProductService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
@Tag(name = "Product Controller", description = "CRUD + Search APIs for Product Catalog")
public class ProductController {

    @Autowired
    private ProductService service;

    @PostMapping
    @Operation(summary = "Create a new product (Seller/Admin)")
    public ResponseEntity<Products> createProduct(@RequestBody Products product) {
        return ResponseEntity.ok(service.createProduct(product));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get product by ID")
    public ResponseEntity<Products> getProductById(@Parameter(description = "Product ID")
            @PathVariable String id) {
        return ResponseEntity.ok(service.getProductById(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update product (Only Seller Owner)")
    public ResponseEntity<Products> updateProduct(@PathVariable String id,
            @RequestBody Products product,
            @Parameter(description = "Seller ID for authorization")
            @RequestParam String sellerId) {
        return ResponseEntity.ok(service.updateProduct(id, product, sellerId));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete product (Only Seller Owner)")
    public ResponseEntity<String> deleteProduct(@PathVariable String id,
            @Parameter(description = "Seller ID for authorization")
            @RequestParam String sellerId) {
        service.deleteProduct(id, sellerId);
        return ResponseEntity.ok("Product deleted successfully");
    }

    @GetMapping("/search")
    @Operation(summary = "Search products with filters, sorting & pagination")
    public ResponseEntity<Page<Products>> searchProducts(@Parameter(description = "Search keyword")
            @RequestParam(required = false) String keyword, @Parameter(description = "Category filter")
            @RequestParam(required = false) String category,
            @Parameter(description = "Brand filter")
            @RequestParam(required = false) String brand,

            @Parameter(description = "Minimum price")
            @RequestParam(required = false) Double minPrice,

            @Parameter(description = "Maximum price")
            @RequestParam(required = false) Double maxPrice,

            @Parameter(description = "Tag filter")
            @RequestParam(required = false) String tag,

            @Parameter(description = "Only in-stock products")
            @RequestParam(required = false) Boolean inStock,

            @Parameter(description = "Filter by seller")
            @RequestParam(required = false) String sellerId,

            @Parameter(description = "Page number (default 0)")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "Page size (default 10)")
            @RequestParam(defaultValue = "10") int size,

            @Parameter(description = "Sort field (price, name, etc.)")
            @RequestParam(defaultValue = "price") String sortBy,

            @Parameter(description = "Sort direction (asc/desc)")
            @RequestParam(defaultValue = "asc") String sortDir
    ) {

        return ResponseEntity.ok(
                service.searchProducts(
                        keyword,
                        category,
                        brand,
                        minPrice,
                        maxPrice,
                        tag,
                        inStock,
                        sellerId,
                        page,
                        size,
                        sortBy,
                        sortDir
                )
        );
    }
}