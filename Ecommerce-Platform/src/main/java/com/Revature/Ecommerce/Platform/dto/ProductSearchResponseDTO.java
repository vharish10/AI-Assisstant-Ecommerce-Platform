package com.Revature.Ecommerce.Platform.dto;

import lombok.*;
import java.util.*;

@Data
@AllArgsConstructor
public class ProductSearchResponseDTO {
    private List<ProductResponseDTO> products;
    private int page;
    private int size;
    private long totalElements;
}
