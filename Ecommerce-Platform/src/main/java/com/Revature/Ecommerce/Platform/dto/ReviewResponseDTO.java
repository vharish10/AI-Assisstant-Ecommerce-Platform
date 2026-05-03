package com.Revature.Ecommerce.Platform.dto;

import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
public class ReviewResponseDTO {

    private String productId;
    private Long userId;
    private int rating;
    private String comment;
    private LocalDateTime createdAt;
}