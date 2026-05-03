package com.Revature.Ecommerce.Platform.service;

import com.Revature.Ecommerce.Platform.models.*;
import com.Revature.Ecommerce.Platform.models.Products;
import com.Revature.Ecommerce.Platform.repository.ChatHistoryRepository;
import com.Revature.Ecommerce.Platform.repository.ProductRepository;
import com.Revature.Ecommerce.Platform.repository.ReviewRepository;
import com.Revature.Ecommerce.Platform.util.VectorUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class AIService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private ChatHistoryRepository chatRepository;

    @Autowired
    private EmbeddingService embeddingService;

    @Autowired
    private IntentService intentService;

    // handling queries
    public String handleQuery(Long userId, String query) {
        Intent intent = intentService.classify(query);
        String response;
        switch (intent) {
            case SEARCH -> response = handleSearch(query);
            case COMPARE -> response = handleCompare(query);
            case SUMMARY -> response = handleSummary(query);
            default -> response = "Try asking something like 'compare iPhone and Samsung'";
        }
        saveChat(userId, query, response);
        return response;
    }

    // smart searching
    private String handleSearch(String query) {

        String lowerQuery = query.toLowerCase();

        // 🔹 STEP 1: Keyword filtering (VERY IMPORTANT)
        List<Products> filtered = productRepository.findAll().stream()
                .filter(p ->
                        p.getName().toLowerCase().contains(lowerQuery) ||
                                p.getCategory().toLowerCase().contains(lowerQuery) ||
                                p.getBrand().toLowerCase().contains(lowerQuery)
                )
                .toList();

        // 🔹 If no keyword match, fallback to all products
        if (filtered.isEmpty()) {
            filtered = productRepository.findAll();
        }

        // 🔹 STEP 2: Vector ranking
        List<Double> queryEmbedding = embeddingService.getEmbedding(query);

        List<Products> results = filtered.stream()
                .sorted((p1, p2) -> Double.compare(
                        similarity(p2, queryEmbedding),
                        similarity(p1, queryEmbedding)
                ))
                .limit(5)
                .toList();

        return results.stream()
                .map(p -> p.getName() + " - ₹" + p.getPrice())
                .reduce("", (a, b) -> a + "\n" + b);
    }

    //coomparing multiple products
    private String handleCompare(String query) {

        List<Double> queryEmbedding = embeddingService.getEmbedding(query);

        List<Products> top = productRepository.findAll().stream()
                .map(p -> new AbstractMap.SimpleEntry<>(p, similarity(p, queryEmbedding)))
                .sorted((a, b) -> Double.compare(b.getValue(), a.getValue()))
                .limit(2)
                .map(Map.Entry::getKey)
                .toList();

        if (top.size() < 2) return "Not enough matching products";

        Products p1 = top.get(0);
        Products p2 = top.get(1);

        return p1.getName() + " vs " + p2.getName()
                + "\nPrice: " + p1.getPrice() + " vs " + p2.getPrice();
    }

    //Summarise the product
    private String handleSummary(String query) {

        List<Double> queryEmbedding = embeddingService.getEmbedding(query);

        Products product = productRepository.findAll().stream()
                .max(Comparator.comparing(p -> similarity(p, queryEmbedding)))
                .orElse(null);

        if (product == null) return "No relevant product found";

        List<Review> reviews = reviewRepository.findByProductId(product.getId());

        if (reviews.isEmpty()) return "No reviews available";

        String combined = reviews.stream()
                .map(Review::getComment)
                .reduce("", (a, b) -> a + " " + b);

        return callLLM("Summarize reviews for " + product.getName() + ": " + combined);
    }

    //SImilarity
    private double similarity(Products p, List<Double> queryEmbedding) {
        if (p.getEmbedding() == null){
            return 0;
        }
        return VectorUtils.cosineSimilarity(p.getEmbedding(), queryEmbedding);
    }

    //LLM(Ollama)
    private String callLLM(String prompt) {

        RestTemplate restTemplate = new RestTemplate();

        String url = "http://localhost:11434/api/generate";

        Map<String, Object> body = new HashMap<>();
        body.put("model", "tinyllama"); // lightweight model
        body.put("prompt", prompt);
        body.put("stream", false);

        Map response = restTemplate.postForObject(url, body, Map.class);

        return response.get("response").toString();
    }

    //saving chat history
    private void saveChat(Long userId, String query, String response) {

        ChatHistory chat = ChatHistory.builder()
                .userId(userId)
                .query(query)
                .response(response)
                .timestamp(LocalDateTime.now())
                .build();

        chatRepository.save(chat);
    }
}