package com.Revature.Ecommerce.Platform.dto;

import lombok.*;

@Data
@AllArgsConstructor
public class ProductRatingDTO {

    private double averageRating;
    private int totalReviews;
}