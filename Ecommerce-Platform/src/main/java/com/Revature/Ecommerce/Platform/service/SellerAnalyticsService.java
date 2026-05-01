package com.Revature.Ecommerce.Platform.service;

import com.Revature.Ecommerce.Platform.dto.SellerAnalyticsDTO;
import com.Revature.Ecommerce.Platform.dto.TopProductDTO;
import com.Revature.Ecommerce.Platform.repository.OrderItemRepository;
import com.Revature.Ecommerce.Platform.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SellerAnalyticsService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    private static final int LOW_STOCK_THRESHOLD = 5;

    public SellerAnalyticsDTO getSellerAnalytics(Long sellerId) {

        // Total products
        int totalProducts = productRepository.countBySellerId(sellerId);

        //Low stock products
        int lowStockProducts = productRepository
                .countBySellerIdAndStockLessThan(sellerId, LOW_STOCK_THRESHOLD);

        //Orders data
        Integer totalOrders = Optional.ofNullable(
                orderItemRepository.getTotalOrders(sellerId)
        ).orElse(0);

        Integer totalUnitsSold = Optional.ofNullable(
                orderItemRepository.getTotalUnitsSold(sellerId)
        ).orElse(0);

        Double totalRevenue = Optional.ofNullable(
                orderItemRepository.getTotalRevenue(sellerId)
        ).orElse(0.0);

        // Top products
        List<Object[]> rawTopProducts = orderItemRepository.getTopSellingProducts(sellerId);

        List<TopProductDTO> topProducts = rawTopProducts.stream()
                .limit(5)
                .map(obj -> new TopProductDTO(
                        (String) obj[0],
                        (String) obj[1],
                        ((Number) obj[2]).intValue()
                ))
                .toList();

        return SellerAnalyticsDTO.builder()
                .sellerId(sellerId)
                .totalProducts(totalProducts)
                .totalOrders(totalOrders)
                .totalUnitsSold(totalUnitsSold)
                .totalRevenue(totalRevenue)
                .lowStockProducts(lowStockProducts)
                .topSellingProducts(topProducts)
                .build();
    }
}