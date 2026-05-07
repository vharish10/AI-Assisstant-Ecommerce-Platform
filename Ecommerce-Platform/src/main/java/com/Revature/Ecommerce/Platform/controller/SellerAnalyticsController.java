package com.Revature.Ecommerce.Platform.controller;

import com.Revature.Ecommerce.Platform.dto.SellerAnalyticsDTO;
import com.Revature.Ecommerce.Platform.service.SellerAnalyticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/seller/analytics")
@Tag(name = "Seller Analytics Controller")
public class SellerAnalyticsController {

    @Autowired
    private SellerAnalyticsService service;

    @GetMapping
    @Operation(
            summary = "Get Seller Analytics",
            description = "Fetch dashboard analytics including revenue, orders, stock status, and top products for a seller"
    )
    public ResponseEntity<SellerAnalyticsDTO> getAnalytics(

            @Parameter(
                    description = "Unique ID of the seller",
                    example = "101",
                    required = true
            )
            @RequestParam Long sellerId) {

        return ResponseEntity.ok(service.getSellerAnalytics(sellerId));
    }
}