package com.Revature.Ecommerce.Platform.models;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.*;

@Document(collection = "recently_viewed")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecentlyViewed {

    @Id
    private String id;
    private Long userId;
    private LinkedList<String> productIds;
}