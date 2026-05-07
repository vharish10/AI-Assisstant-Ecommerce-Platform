package com.Revature.Ecommerce.Platform.controller;

import com.Revature.Ecommerce.Platform.dto.ProductRequestDTO;
import com.Revature.Ecommerce.Platform.dto.ProductResponseDTO;
import com.Revature.Ecommerce.Platform.dto.ProductSearchResponseDTO;
import com.Revature.Ecommerce.Platform.models.Products;
import com.Revature.Ecommerce.Platform.models.Products;
import com.Revature.Ecommerce.Platform.service.ImageService;
import com.Revature.Ecommerce.Platform.service.ProductService;

import com.Revature.Ecommerce.Platform.service.RecentlyViewedService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.swagger.v3.oas.annotations.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@RestController
@RequestMapping("/api/products")
@Tag(name = "Product Controller")
public class ProductController {

    private static final Logger log = LoggerFactory.getLogger(ProductController.class);

    @Autowired
    private RecentlyViewedService recentlyViewedService;

    @Autowired
    private ProductService service;

    @Autowired
    private ImageService imageService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProductResponseDTO> createProduct(

            @RequestPart("product") String productJson,
            @RequestPart(value = "images", required = false) List<MultipartFile> images,
            @RequestParam Long sellerId) throws Exception {

        ObjectMapper mapper = new ObjectMapper();
        ProductRequestDTO dto = mapper.readValue(productJson, ProductRequestDTO.class);

        List<String> imageUrls = new ArrayList<>();

        if (images != null) {
            for (MultipartFile file : images) {
                imageUrls.add(imageService.uploadImage(file));
            }
        }

        Products product = service.mapToEntity(dto, sellerId);
        product.setImages(imageUrls);

        Products saved = service.createProduct(product);

        return ResponseEntity.ok(service.mapToDTO(saved));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> getProductById(
            @PathVariable String id,
            @RequestParam Long userId) {

        Products product = service.getProductById(id);
        recentlyViewedService.addViewedProduct(userId, id);

        return ResponseEntity.ok(service.mapToDTO(product));
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Update Product")
    public ResponseEntity<ProductResponseDTO> updateProduct(
            @PathVariable String id,
            @RequestPart("product") ProductRequestDTO dto,
            @RequestPart(value = "images", required = false) List<MultipartFile> images,
            @RequestParam Long sellerId) {
        List<String> imageUrls = new ArrayList<>();
        if (images != null) {
            for (MultipartFile file : images) {
                imageUrls.add(imageService.uploadImage(file));
            }
        }
        Products updated = imageService.updateProductWithImages(id, dto, sellerId, imageUrls);
        return ResponseEntity.ok(service.mapToDTO(updated));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete Product")
    public ResponseEntity<String> deleteProduct(
            @PathVariable String id,
            @RequestParam Long sellerId) {

        log.info("API: Delete Product {}", id);

        service.deleteProduct(id, sellerId);

        return ResponseEntity.ok("Product deleted successfully");
    }

    @GetMapping("/search")
    @Operation(summary = "Search Products")
    public ResponseEntity<ProductSearchResponseDTO> searchProducts(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) String tag,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "price") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

        return ResponseEntity.ok(
                service.searchProducts(
                        keyword, category, brand,
                        minPrice, maxPrice, tag,
                        page, size, sortBy, sortDir
                )
        );
    }
}