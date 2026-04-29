package com.Revature.Ecommerce.Platform.models;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "products")
public class Products {

    @Id
    private String id;
    private String name;
    private String description;
    @Indexed
    private String category;
    @Indexed
    private String brand;
    @Indexed
    private Double price;
    private Integer stock;
    private Long sellerId;
    private String city;
    private List<String> images;
    private List<String> tags;

    private Map<String, Object> attributes;
}