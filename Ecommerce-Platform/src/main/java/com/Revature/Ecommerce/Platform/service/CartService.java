package com.Revature.Ecommerce.Platform.service;

import com.Revature.Ecommerce.Platform.CustomExceptions.*;
import com.Revature.Ecommerce.Platform.models.*;
import com.Revature.Ecommerce.Platform.repository.CartRepository;
import com.Revature.Ecommerce.Platform.repository.ProductRepository;
import com.Revature.Ecommerce.Platform.models.Products;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ProductRepository productRepository;

    public Cart getOrCreateCart(String userId) {
        return cartRepository.findByUserId(userId).orElseGet(()->Cart.builder()
                        .userId(userId)
                        .items(new ArrayList<>())
                        .totalPrice(0.0)
                        .build());
    }

    public Cart addToCart(String userId, String productId, int quantity) {
        Cart cart = getOrCreateCart(userId);
        Products product = productRepository.findById(productId).orElseThrow(() -> new RuntimeException("Product not found"));

        if(quantity>product.getStock()){
            throw new RuntimeException("Not enough stock");
        }
        Optional<CartItem> existingItem = cart.getItems().stream()
                .filter(item -> item.getProductId().equals(productId))
                .findFirst();

        if(existingItem.isPresent()){
            CartItem item=existingItem.get();
            item.setQuantity(item.getQuantity()+quantity);
        }else{
            CartItem newItem=CartItem.builder()
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

    public Cart viewCart(String userId) {
        return cartRepository.findByUserId(userId).orElseThrow(() -> new RuntimeException("Cart not found"));
    }

    public void recalculateTotal(Cart cart) {
        double total = 0.0;
        for(CartItem item:cart.getItems()){
            total+=item.getPrice()*item.getQuantity();
        }
        cart.setTotalPrice(total);
    }

    public Cart updateQuantity(String userId, String productId, int quantity) {
        Cart cart=cartRepository.findByUserId(userId).orElseThrow(() -> new CartNotFoundException("Cart not found"));
        CartItem foundItem = null;
        for(CartItem item:cart.getItems()){
            if(item.getProductId().equals(productId)){
                foundItem=item;
                break;
            }
        }
        if(foundItem==null){
            throw new CartItemNotFoundException("Item not found in cart");
        }
        Products product=productRepository.findById(productId).orElseThrow(() -> new RuntimeException("Product not found"));

        if(quantity>product.getStock()){
            throw new RuntimeException("Not enough stock");
        }
        foundItem.setQuantity(quantity);
        recalculateTotal(cart);
        return cartRepository.save(cart);
    }

    public void clearCart(String userId){
        Cart cart = cartRepository.findByUserId(userId).orElseThrow(() -> new CartNotFoundException("Cart not found"));
        cart.getItems().clear();
        cart.setTotalPrice(0.0);
        cartRepository.save(cart);
    }

    public Cart removeItem(String userId, String productId){
        Cart cart=cartRepository.findByUserId(userId).orElseThrow(() -> new CartNotFoundException("Cart not found"));

        boolean removed=false;
        for(int i=0;i<cart.getItems().size();i++){
            CartItem item=cart.getItems().get(i);
            if(item.getProductId().equals(productId)){
                cart.getItems().remove(i);
                removed=true;
                break;
            }
        }
        if(!removed){
            throw new CartItemNotFoundException("Item not found in cart");
        }
        recalculateTotal(cart);
        return cartRepository.save(cart);
    }
}