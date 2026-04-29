package com.Revature.Ecommerce.Platform.controller;

import com.Revature.Ecommerce.Platform.models.Order;
import com.Revature.Ecommerce.Platform.service.OrderService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/orders")
@Tag(name = "Order Controller")
public class OrderController {

    @Autowired
    private OrderService service;

    @PostMapping("/checkout")
    public ResponseEntity<Order> placeOrder(
            @RequestParam Long userId,
            @RequestParam Long addressId) {

        return ResponseEntity.ok(service.placeOrder(userId, addressId));
    }

    @PostMapping("/buy-now")
    public ResponseEntity<Order> buyNow(
            @RequestParam Long userId,
            @RequestParam String productId,
            @RequestParam int quantity,
            @RequestParam Long addressId) {

        return ResponseEntity.ok(service.buyNow(userId, productId, quantity, addressId));
    }

    @GetMapping("/user")
    @Operation(summary = "Get user order history")
    public ResponseEntity<List<Order>> getOrders(@RequestParam Long userId) {
        return ResponseEntity.ok(service.getOrdersByUser(userId));
    }

    @GetMapping("/{orderId}")
    @Operation(summary = "Get order details")
    public ResponseEntity<Map<String, Object>> getOrderDetails(@PathVariable Long orderId) {
        return ResponseEntity.ok(service.getOrderDetails(orderId));
    }

    @PutMapping("/{orderId}/cancel")
    @Operation(summary = "Cancel order")
    public ResponseEntity<Order> cancelOrder(@PathVariable Long orderId) {
        return ResponseEntity.ok(service.cancelOrder(orderId));
    }

    @PutMapping("/{orderId}/cancel-item")
    @Operation(summary = "Cancel specific item in order")
    public ResponseEntity<Order> cancelOrderItem(
            @PathVariable Long orderId,
            @RequestParam String productId) {
        return ResponseEntity.ok(service.cancelOrderItem(orderId, productId));
    }
}
