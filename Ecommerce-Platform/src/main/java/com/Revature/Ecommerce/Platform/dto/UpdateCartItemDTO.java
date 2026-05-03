package com.Revature.Ecommerce.Platform.dto;

import lombok.Data;

@Data
public class UpdateCartItemDTO {
    private String productId;
    private int quantity;
}