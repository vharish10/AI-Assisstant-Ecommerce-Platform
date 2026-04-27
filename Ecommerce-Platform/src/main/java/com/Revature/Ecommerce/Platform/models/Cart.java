package com.Revature.Ecommerce.Platform.models;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "carts")
public class Cart {

    @Id
    private String id;
    private String userId;
    private List<CartItem> items=new ArrayList<>();
    private Double totalPrice=0.0;
}