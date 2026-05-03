package com.Revature.Ecommerce.Platform.dto;

import lombok.Data;

@Data
public class PlaceOrderRequestDTO {
    private Long userId;
    private Long addressId;
}