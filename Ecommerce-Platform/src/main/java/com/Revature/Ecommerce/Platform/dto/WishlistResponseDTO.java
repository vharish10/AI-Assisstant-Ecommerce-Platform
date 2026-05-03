package com.Revature.Ecommerce.Platform.dto;

import lombok.*;
import java.util.*;

@Data
@Builder
public class WishlistResponseDTO {

    private Long userId;
    private List<ProductResponseDTO> products;
    private int totalItems;
}