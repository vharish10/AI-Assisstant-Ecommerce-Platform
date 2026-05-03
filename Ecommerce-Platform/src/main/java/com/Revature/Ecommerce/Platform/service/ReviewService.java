package com.Revature.Ecommerce.Platform.service;

import com.Revature.Ecommerce.Platform.CustomExceptions.*;
import com.Revature.Ecommerce.Platform.dto.*;
import com.Revature.Ecommerce.Platform.models.Review;
import com.Revature.Ecommerce.Platform.repository.OrderItemRepository;
import com.Revature.Ecommerce.Platform.repository.ProductRepository;
import com.Revature.Ecommerce.Platform.repository.ReviewRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    // Add or Update Review
    public ReviewResponseDTO addReview(ReviewRequestDTO dto) {

        if (!productRepository.existsById(dto.getProductId())) {
            throw new ProductNotFoundException("Product not found");
        }

        if (dto.getRating() < 1 || dto.getRating() > 5) {
            throw new InvalidRequestException("Rating must be between 1 and 5");
        }

        Review review = reviewRepository
                .findByProductIdAndUserId(dto.getProductId(), dto.getUserId())
                .orElse(Review.builder()
                        .productId(dto.getProductId())
                        .userId(dto.getUserId())
                        .createdAt(LocalDateTime.now())
                        .build());

        review.setRating(dto.getRating());
        review.setComment(dto.getComment());
        review.setUpdatedAt(LocalDateTime.now());

        Review saved = reviewRepository.save(review);

        return mapToDTO(saved);
    }

    // Get reviews for product
    public List<ReviewResponseDTO> getReviews(String productId) {
        return reviewRepository.findByProductId(productId)
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    // Delete review
    public void deleteReview(String productId, Long userId) {
        reviewRepository.deleteByProductIdAndUserId(productId, userId);
    }

    // Get rating summary
    public ProductRatingDTO getProductRating(String productId) {

        List<Review> reviews = reviewRepository.findByProductId(productId);

        if (reviews.isEmpty()) {
            return new ProductRatingDTO(0.0, 0);
        }

        double avg = reviews.stream()
                .mapToInt(Review::getRating)
                .average()
                .orElse(0.0);

        return new ProductRatingDTO(avg, reviews.size());
    }

    // Mapping
    private ReviewResponseDTO mapToDTO(Review review) {
        return ReviewResponseDTO.builder()
                .productId(review.getProductId())
                .userId(review.getUserId())
                .rating(review.getRating())
                .comment(review.getComment())
                .createdAt(review.getCreatedAt())
                .build();
    }
}