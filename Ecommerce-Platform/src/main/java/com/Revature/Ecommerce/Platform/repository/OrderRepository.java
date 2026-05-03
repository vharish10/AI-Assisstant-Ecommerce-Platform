package com.Revature.Ecommerce.Platform.repository;

import com.Revature.Ecommerce.Platform.models.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    Order findByOrderId(Long orderId);
    List<Order> findByUserId(Long userId);
}
