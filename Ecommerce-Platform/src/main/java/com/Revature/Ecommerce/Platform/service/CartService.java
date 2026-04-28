package com.Revature.Ecommerce.Platform.service;

import com.Revature.Ecommerce.Platform.CustomExceptions.*;
import com.Revature.Ecommerce.Platform.models.*;
import com.Revature.Ecommerce.Platform.repository.CartRepository;
import com.Revature.Ecommerce.Platform.repository.ProductRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class CartService {

    private static final Logger log = LoggerFactory.getLogger(CartService.class);

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ProductRepository productRepository;

    public Cart getOrCreateCart(Long userId) {
        return cartRepository.findByUserId(userId).orElseGet(() -> {
                    log.info("Creating new cart for user {}", userId);
                    return Cart.builder()
                            .userId(userId)
                            .items(new ArrayList<>())
                            .totalPrice(0.0)
                            .build();
                });
    }

    public Cart addToCart(Long userId, String productId, int quantity) {
        log.info("Adding product {} to cart for user {}", productId, userId);
        if(quantity <= 0){
            throw new InvalidRequestException("Quantity must be greater than 0");
        }
        Cart cart = getOrCreateCart(userId);
        Products product = productRepository.findById(productId).orElseThrow(() -> new ProductNotFoundException("Product not found"));

        if(quantity > product.getStock()){
            log.warn("Not enough stock for product {}", productId);
            throw new InvalidRequestException("Not enough stock");
        }

        Optional<CartItem> existingItem = cart.getItems().stream()
                .filter(item -> item.getProductId().equals(productId))
                .findFirst();
        if(existingItem.isPresent()){
            CartItem item = existingItem.get();
            int newQuantity = item.getQuantity() + quantity;
            if (newQuantity > product.getStock()) {
                throw new InvalidRequestException("Exceeds available stock");
            }
            item.setQuantity(newQuantity);
        } else {
            CartItem newItem = CartItem.builder()
                    .productId(productId)
                    .name(product.getName())
                    .price(product.getPrice())
                    .quantity(quantity)
                    .build();
            cart.getItems().add(newItem);
        }
        recalculateTotal(cart);
        return cartRepository.save(cart);
    }

    public Cart viewCart(Long userId) {
        return getOrCreateCart(userId);
    }

    public void recalculateTotal(Cart cart){
        double total = 0.0;
        for(CartItem item:cart.getItems()){
            total+=item.getPrice()*item.getQuantity();
        }
        cart.setTotalPrice(total);
    }

    public Cart updateQuantity(Long userId, String productId, int quantity) {
        log.info("Updating quantity for product {} in user {} cart", productId, userId);
        if (quantity <= 0) {
            throw new InvalidRequestException("Quantity must be greater than 0");
        }
        Cart cart = cartRepository.findByUserId(userId).orElseThrow(() -> new CartNotFoundException("Cart not found"));
        CartItem foundItem = null;
        for(CartItem item:cart.getItems()){
            if(item.getProductId().equals(productId)){
                foundItem=item;
                break;
            }
        }

        Products product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product not found"));

        if (quantity > product.getStock()) {
            log.warn("Adjusting quantity due to stock limit for product {}", productId);
            quantity = product.getStock(); // auto-adjust
        }
        foundItem.setQuantity(quantity);
        recalculateTotal(cart);
        return cartRepository.save(cart);
    }

    public Cart removeItem(Long userId, String productId){
        log.info("Removing product {} from user {} cart", productId, userId);
        Cart cart = cartRepository.findByUserId(userId).orElseThrow(() -> new CartNotFoundException("Cart not found"));
        boolean removed = cart.getItems().
                removeIf(item -> item.getProductId().equals(productId));
        if(!removed){
            throw new CartItemNotFoundException("Item not found in cart");
        }
        recalculateTotal(cart);

        return cartRepository.save(cart);
    }

    public void clearCart(Long userId) {

        log.info("Clearing cart for user {}", userId);

        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new CartNotFoundException("Cart not found"));

        cart.getItems().clear();
        cart.setTotalPrice(0.0);

        cartRepository.save(cart);
    }

    public int getCartItemCount(Long userId) {
        Cart cart = getOrCreateCart(userId);
        return cart.getItems().stream()
                .mapToInt(CartItem::getQuantity)
                .sum();
    }
}