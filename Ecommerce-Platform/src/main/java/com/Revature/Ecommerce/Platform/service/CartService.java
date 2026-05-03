package com.Revature.Ecommerce.Platform.service;

import com.Revature.Ecommerce.Platform.CustomExceptions.*;
import com.Revature.Ecommerce.Platform.dto.CartItemDTO;
import com.Revature.Ecommerce.Platform.dto.CartResponseDTO;
import com.Revature.Ecommerce.Platform.models.*;
import com.Revature.Ecommerce.Platform.repository.CartRepository;
import com.Revature.Ecommerce.Platform.repository.ProductRepository;

import jakarta.transaction.Transactional;
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

    public CartResponseDTO mapToDTO(Cart cart) {

        List<CartItemDTO> itemDTOs = cart.getItems().stream()
                .map(item -> CartItemDTO.builder()
                        .productId(item.getProductId())
                        .name(item.getName())
                        .price(item.getPrice())
                        .quantity(item.getQuantity())
                        .totalPrice(item.getPrice() * item.getQuantity())
                        .build())
                .toList();

        int totalItems = itemDTOs.stream()
                .mapToInt(CartItemDTO::getQuantity)
                .sum();

        return CartResponseDTO.builder()
                .userId(cart.getUserId())
                .items(itemDTOs)
                .totalItems(totalItems)
                .totalPrice(cart.getTotalPrice())
                .build();
    }

    public Cart getOrCreateCart(Long userId) {
        return cartRepository.findByUserId(userId).orElseGet(() -> {
                    log.info("Creating new cart for user {}", userId);
                    return cartRepository.save(
                            Cart.builder()
                                    .userId(userId)
                                    .items(new ArrayList<>())
                                    .totalPrice(0.0)
                                    .build()
                    );
                });
    }

    @Transactional
    public CartResponseDTO addToCart(Long userId, String productId, int quantity) {

        log.info("Adding product {} to cart for user {}", productId, userId);

        if (quantity <= 0) {
            throw new InvalidRequestException("Quantity must be greater than 0");
        }

        //Fetch product FIRST
        Products product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product not found"));

        //Out of stock check
        if(product.getStock() <= 0){
            throw new InvalidRequestException("Product is out of stock");
        }

        //Stock validation
        if(quantity > product.getStock()){
            throw new InvalidRequestException("Not enough stock");
        }
        Cart cart = getOrCreateCart(userId);
        Optional<CartItem> existingItem = cart.getItems().stream()
                .filter(item -> item.getProductId().equals(productId))
                .findFirst();

        if(existingItem.isPresent()){
            CartItem item = existingItem.get();
            int newQuantity = item.getQuantity() + quantity;
            if(newQuantity > product.getStock()){
                throw new InvalidRequestException("Exceeds available stock");
            }
            item.setQuantity(newQuantity);
        }else{
            CartItem newItem = CartItem.builder()
                    .productId(productId)
                    .name(product.getName())
                    .price(product.getPrice())
                    .quantity(quantity)
                    .build();
            cart.getItems().add(newItem);
        }

        recalculateTotal(cart);
        Cart savedCart = cartRepository.save(cart);
        return mapToDTO(savedCart);
    }

    public CartResponseDTO viewCart(Long userId) {
        return mapToDTO(getOrCreateCart(userId));
    }

    public void recalculateTotal(Cart cart){
        double total = 0.0;
        for(CartItem item:cart.getItems()){
            total+=item.getPrice()*item.getQuantity();
        }
        cart.setTotalPrice(total);
    }

    public CartResponseDTO updateQuantity(Long userId, String productId, int quantity) {

        log.info("Updating quantity for product {} in user {} cart", productId, userId);

        if (quantity <= 0) {
            throw new InvalidRequestException("Quantity must be greater than 0");
        }

        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new CartNotFoundException("Cart not found"));

        //use stream instead of loop
        CartItem foundItem = cart.getItems().stream()
                .filter(item -> item.getProductId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new CartItemNotFoundException("Item not found in cart"));

        Products product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product not found"));


        if (product.getStock() <= 0) {
            throw new InvalidRequestException("Product is out of stock");
        }

        //Auto-adjust quantity
        if (quantity > product.getStock()) {
            log.warn("Adjusting quantity due to stock limit for product {}", productId);
            quantity = product.getStock();
        }
        foundItem.setQuantity(quantity);
        recalculateTotal(cart);
        Cart savedCart = cartRepository.save(cart);

        return mapToDTO(savedCart);
    }

    private Cart removeItemInternal(Long userId, String productId){
        log.info("Removing product {} from user {} cart", productId, userId);

        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new CartNotFoundException("Cart not found"));

        boolean removed = cart.getItems()
                .removeIf(item -> item.getProductId().equals(productId));

        if(!removed){
            throw new CartItemNotFoundException("Item not found in cart");
        }

        recalculateTotal(cart);

        return cartRepository.save(cart);
    }

    public CartResponseDTO removeItem(Long userId, String productId){
        return mapToDTO(removeItemInternal(userId, productId));
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