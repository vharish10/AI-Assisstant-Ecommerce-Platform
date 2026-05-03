package com.Revature.Ecommerce.Platform.dto;

import lombok.*;

@Data
public class WishlistRequestDTO {
    private Long userId;
    private String productId;
}
