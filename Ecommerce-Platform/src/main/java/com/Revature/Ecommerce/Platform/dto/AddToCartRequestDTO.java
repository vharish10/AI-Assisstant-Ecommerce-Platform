package com.Revature.Ecommerce.Platform.dto;

import lombok.Data;

@Data
public class AddToCartRequestDTO {
    private String productId;
    private int quantity;
}