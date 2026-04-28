package com.Revature.Ecommerce.Platform.controller;

import com.Revature.Ecommerce.Platform.models.Cart;
import com.Revature.Ecommerce.Platform.service.CartService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@Tag(name = "Cart Controller", description = "APIs for managing user cart")
public class CartController {

    private static final Logger log = LoggerFactory.getLogger(CartController.class);

    @Autowired
    private CartService service;

    @PostMapping("/add")
    @Operation(summary = "Add product to cart")
    public ResponseEntity<Cart> addToCart(@Parameter(description = "User ID") @RequestParam Long userId,
            @Parameter(description = "Product ID") @RequestParam String productId,
            @Parameter(description = "Quantity") @RequestParam int quantity) {

        log.info("API: Add to cart | userId={} productId={} quantity={}", userId, productId, quantity);

        return ResponseEntity.ok(service.addToCart(userId, productId, quantity));
    }

    @GetMapping
    @Operation(summary = "View user's cart")
    public ResponseEntity<Cart> viewCart(@Parameter(description = "User ID") @RequestParam Long userId) {
        log.info("API: View cart | userId={}", userId);
        return ResponseEntity.ok(service.viewCart(userId));
    }

    @PutMapping("/update")
    @Operation(summary = "Update quantity of a cart item")
    public ResponseEntity<Cart> updateQuantity(
            @Parameter(description = "User ID") @RequestParam Long userId,
            @Parameter(description = "Product ID") @RequestParam String productId,
            @Parameter(description = "New quantity") @RequestParam int quantity) {

        log.info("API: Update cart item | userId={} productId={} quantity={}", userId, productId, quantity);

        return ResponseEntity.ok(service.updateQuantity(userId, productId, quantity));
    }

    @DeleteMapping("/remove")
    @Operation(summary = "Remove item from cart")
    public ResponseEntity<Cart> removeItem(@Parameter(description = "User ID") @RequestParam Long userId,
            @Parameter(description = "Product ID") @RequestParam String productId) {

        log.info("API: Remove item | userId={} productId={}", userId, productId);
        return ResponseEntity.ok(service.removeItem(userId, productId));
    }

    @DeleteMapping("/clear")
    @Operation(summary = "Clear entire cart")
    public ResponseEntity<String> clearCart(@Parameter(description = "User ID") @RequestParam Long userId) {

        log.info("API: Clear cart | userId={}", userId);
        service.clearCart(userId);
        return ResponseEntity.ok("Cart cleared successfully");
    }
}