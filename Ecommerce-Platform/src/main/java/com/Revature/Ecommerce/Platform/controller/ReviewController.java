package com.Revature.Ecommerce.Platform.controller;

import com.Revature.Ecommerce.Platform.dto.*;
import com.Revature.Ecommerce.Platform.service.ReviewService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@Tag(name = "Review Controller", description = "APIs for product reviews and ratings")
public class ReviewController {

    @Autowired
    private ReviewService service;

    @PostMapping
    @Operation(summary = "Add or update review")
    public ResponseEntity<ReviewResponseDTO> addReview(
            @RequestBody ReviewRequestDTO dto) {

        return ResponseEntity.ok(service.addReview(dto));
    }

    @GetMapping("/{productId}")
    @Operation(summary = "Get reviews for product")
    public ResponseEntity<List<ReviewResponseDTO>> getReviews(
            @PathVariable String productId) {

        return ResponseEntity.ok(service.getReviews(productId));
    }

    @DeleteMapping
    @Operation(summary = "Delete review")
    public ResponseEntity<String> deleteReview(
            @RequestParam String productId,
            @RequestParam Long userId) {

        service.deleteReview(productId, userId);
        return ResponseEntity.ok("Review deleted");
    }

    @GetMapping("/{productId}/rating")
    @Operation(summary = "Get product rating summary")
    public ResponseEntity<ProductRatingDTO> getRating(
            @PathVariable String productId) {

        return ResponseEntity.ok(service.getProductRating(productId));
    }
}