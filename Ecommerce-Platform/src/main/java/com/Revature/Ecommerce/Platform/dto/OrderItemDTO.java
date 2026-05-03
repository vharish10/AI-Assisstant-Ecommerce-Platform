package com.Revature.Ecommerce.Platform.dto;

import lombok.*;

@Data
@Builder
public class OrderItemDTO {

    private String productId;
    private String productName;
    private Integer quantity;
    private Double price;

    private Double totalPrice;

    private boolean cancelled;
}