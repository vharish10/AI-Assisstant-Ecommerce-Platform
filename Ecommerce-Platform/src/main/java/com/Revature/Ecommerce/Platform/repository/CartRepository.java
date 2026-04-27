package com.Revature.Ecommerce.Platform.repository;

import com.Revature.Ecommerce.Platform.models.Cart;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface CartRepository extends MongoRepository<Cart, String> {

    Optional<Cart> findByUserId(String userId);
}