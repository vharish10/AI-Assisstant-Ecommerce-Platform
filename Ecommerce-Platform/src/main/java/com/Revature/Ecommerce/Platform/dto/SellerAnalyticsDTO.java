package com.Revature.Ecommerce.Platform.dto;

import java.util.*;
import lombok.*;

@Data
@Builder
public class SellerAnalyticsDTO {

    private Long sellerId;

    private int totalProducts;
    private int totalOrders;
    private int totalUnitsSold;
    private double totalRevenue;

    private int lowStockProducts;

    private List<TopProductDTO> topSellingProducts;
}