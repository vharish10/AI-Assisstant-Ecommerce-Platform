package com.Revature.Ecommerce.Platform.models;

import com.Revature.Ecommerce.Platform.models.*;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "order_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderItemId;
    private String productId;
    private String productName;
    private Integer quantity;
    private Double price;
    private Long sellerId;
    private String city;
    private boolean isCancelled;

    @ManyToOne
    @JoinColumn(name = "order_id")
    @JsonBackReference
    private Order order;
}