package com.Revature.Ecommerce.Platform.models;

import lombok.*;
import org.springframework.data.annotation.Id;
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

    private String category;
    private String brand;

    private Double price;
    private Integer stock;

    private String sellerId;

    private List<String> images;
    private List<String> tags;
    //features(like RAM,GPU,storage)
    private Map<String, Object> attributes;
}