package com.Revature.Ecommerce.Platform.dto;

import lombok.*;

@Data
@AllArgsConstructor
public class TopProductDTO {

    private String productId;
    private String productName;
    private int unitsSold;
}