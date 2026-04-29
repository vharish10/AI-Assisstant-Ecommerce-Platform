package com.Revature.Ecommerce.Platform.controller;

import com.Revature.Ecommerce.Platform.models.Products;
import com.Revature.Ecommerce.Platform.models.Wishlist;
import com.Revature.Ecommerce.Platform.service.WishListService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/wishlist")
@Tag(name = "Wishlist Controller", description = "APIs for managing wishlist")
public class WishListController {

    @Autowired
    private WishListService service;

    @PostMapping("/add")
    @Operation(summary = "Add product to wishlist")
    public ResponseEntity<Wishlist> add(@Parameter(description = "User ID") @RequestParam Long userId,
            @Parameter(description = "Product ID") @RequestParam String productId) {
        return ResponseEntity.ok(service.addToWishlist(userId, productId));
    }

    @DeleteMapping("/remove")
    @Operation(summary = "Remove product from wishlist")
    public ResponseEntity<Wishlist> remove(@Parameter(description = "User ID") @RequestParam Long userId,
            @Parameter(description = "Product ID") @RequestParam String productId) {
        return ResponseEntity.ok(service.removeFromWishlist(userId, productId));
    }

    @GetMapping
    @Operation(summary = "Get all wishlist products")
    public ResponseEntity<List<Products>> getWishlist(@Parameter(description = "User ID") @RequestParam Long userId) {
        return ResponseEntity.ok(service.getWishlist(userId));
    }

    @PostMapping("/move-to-cart")
    @Operation(summary = "Move product from wishlist to cart")
    public ResponseEntity<String> moveToCart(@Parameter(description = "User ID") @RequestParam Long userId,
            @Parameter(description = "Product ID") @RequestParam String productId) {
        service.moveToCart(userId, productId);
        return ResponseEntity.ok("Product moved to cart successfully");
    }
}