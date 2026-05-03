package com.Revature.Ecommerce.Platform.repository;

import com.Revature.Ecommerce.Platform.models.Cart;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface CartRepository extends MongoRepository<Cart, String> {

    Optional<Cart> findByUserId(Long userId);
}