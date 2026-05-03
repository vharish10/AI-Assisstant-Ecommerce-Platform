package com.Revature.Ecommerce.Platform.repository;

import com.Revature.Ecommerce.Platform.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
}