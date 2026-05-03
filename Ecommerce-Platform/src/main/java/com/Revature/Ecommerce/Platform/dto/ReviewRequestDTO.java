package com.Revature.Ecommerce.Platform.dto;

import lombok.Data;

@Data
public class ReviewRequestDTO {
    private String productId;
    private Long userId;
    private int rating;
    private String comment;
}