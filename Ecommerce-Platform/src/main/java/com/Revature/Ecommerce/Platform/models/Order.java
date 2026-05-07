package com.Revature.Ecommerce.Platform.models;

import com.Revature.Ecommerce.Platform.models.*;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.*;
import java.util.*;

@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId;
    private Long userId;
    private Double subtotal;
    private Double discount;
    private Double totalAmount;
//    @Enumerated(EnumType.STRING)
//    private OrderStatus status;
    private LocalDateTime orderDate;
    private Long addressId;
    @OneToMany(mappedBy = "order",fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<OrderItem> items;
}