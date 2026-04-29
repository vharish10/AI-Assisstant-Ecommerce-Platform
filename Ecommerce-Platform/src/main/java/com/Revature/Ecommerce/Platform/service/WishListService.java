package com.Revature.Ecommerce.Platform.service;

import com.Revature.Ecommerce.Platform.CustomExceptions.WishListNotFound;
import com.Revature.Ecommerce.Platform.models.Products;
import com.Revature.Ecommerce.Platform.models.Wishlist;
import com.Revature.Ecommerce.Platform.repository.ProductRepository;
import com.Revature.Ecommerce.Platform.repository.WishlistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class WishListService {
    @Autowired
    WishlistRepository wishlistRepository;

    @Autowired
    CartService cartService;

    @Autowired
    ProductRepository productRepository;

    //adding product to wishlist
    public Wishlist addToWishlist(Long userId, String productId){
        //finds wishlist of the user, if not exist then creates one
        Wishlist wishlist=wishlistRepository.findByUserId(userId)
                .orElse(Wishlist.builder()
                        .userId(userId)
                        .productIds(new ArrayList<>())
                        .build());
        //if the wishlist doesn't contain the product then add the product
        if(!wishlist.getProductIds().contains(productId)){
            wishlist.getProductIds().add(productId);
        }
        //save the product to wishlist
        return wishlistRepository.save(wishlist);
    }

    //Remove product from WishList
    public Wishlist removeFromWishlist(Long userId, String productId) {
        //finding the users wishlist
        Wishlist wishlist=wishlistRepository.findByUserId(userId).orElseThrow(() -> new WishListNotFound("Wishlist not found"));
        wishlist.getProductIds().remove(productId);     //removing the product from wishlist
        return wishlistRepository.save(wishlist);
    }

    //viewing all the products in wishlist
    public List<Products> getWishlist(Long userId) {
        Wishlist wishlist = wishlistRepository.findByUserId(userId).orElseThrow(() -> new WishListNotFound("Wishlist not found"));
        return productRepository.findAllById(wishlist.getProductIds());
    }

    //if we want to move the product from wishlist to cart
    public void moveToCart(Long userId, String productId) {

        // Add to cart (default quantity = 1)
        cartService.addToCart(userId, productId, 1);

        // Remove from wishlist
        Wishlist wishlist = wishlistRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Wishlist not found"));

        wishlist.getProductIds().remove(productId);

        wishlistRepository.save(wishlist);
    }
}
