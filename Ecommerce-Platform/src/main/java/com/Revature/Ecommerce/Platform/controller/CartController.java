package com.Revature.Ecommerce.Platform.controller;

import com.Revature.Ecommerce.Platform.models.Cart;
import com.Revature.Ecommerce.Platform.service.CartService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    @Autowired
    private CartService service;

    @PostMapping("/add")
    public ResponseEntity<Cart> addToCart(@RequestParam String userId,
            @RequestParam String productId,
            @RequestParam int quantity) {
        return ResponseEntity.ok(service.addToCart(userId, productId, quantity));
    }

    @GetMapping
    public ResponseEntity<Cart> viewCart(@RequestParam String userId) {
        return ResponseEntity.ok(service.viewCart(userId));
    }
}