//package com.Revature.Ecommerce.Platform.service;
//
//import com.Revature.Ecommerce.Platform.models.*;
//import com.Revature.Ecommerce.Platform.models.Products;
//import com.Revature.Ecommerce.Platform.repository.ChatHistoryRepository;
//import com.Revature.Ecommerce.Platform.repository.ProductRepository;
//import com.Revature.Ecommerce.Platform.repository.ReviewRepository;
//import com.Revature.Ecommerce.Platform.util.VectorUtils;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import org.springframework.web.client.RestTemplate;
//
//import java.time.LocalDateTime;
//import java.util.*;
//
//@Service
//public class AIService {
//
//    @Autowired
//    private ProductRepository productRepository;
//
//    @Autowired
//    private ReviewRepository reviewRepository;
//
//    @Autowired
//    private ChatHistoryRepository chatRepository;
//
//    @Autowired
//    private EmbeddingService embeddingService;
//
//    @Autowired
//    private IntentService intentService;
//
//    // MAIN ENTRY (UNCHANGED)
//    public String handleQuery(Long userId, String query) {
//        Intent intent = intentService.classify(query);
//
//        String response;
//        switch (intent) {
//            case SEARCH -> response = handleSearch(query);
//            case COMPARE -> response = handleCompare(query);
//            case SUMMARY -> response = handleSummary(query);
//            default -> response = "Try asking something like 'compare iPhone and Samsung'";
//        }
//
//        saveChat(userId, query, response);
//        return response;
//    }
//
//    // SEARCH (NOW RAG)
//    private String handleSearch(String query) {
//
//        String lowerQuery = query.toLowerCase();
//
//        // Step 1: Keyword filtering
//        List<Products> filtered = productRepository.findAll().stream()
//                .filter(p ->
//                        p.getName().toLowerCase().contains(lowerQuery) ||
//                                p.getCategory().toLowerCase().contains(lowerQuery) ||
//                                p.getBrand().toLowerCase().contains(lowerQuery)
//                )
//                .toList();
//
//        if (filtered.isEmpty()) {
//            filtered = productRepository.findAll();
//        }
//
//        // Step 2: Vector ranking
//        List<Double> queryEmbedding = embeddingService.getEmbedding(query);
//
//        List<Products> results = filtered.stream()
//                .sorted((p1, p2) -> Double.compare(
//                        similarity(p2, queryEmbedding),
//                        similarity(p1, queryEmbedding)
//                ))
//                .limit(5)
//                .toList();
//
//        // Step 3: Build context
//        StringBuilder context = new StringBuilder("Products:\n");
//        for (Products p : results) {
//            context.append("- ")
//                    .append(p.getName())
//                    .append(" | Price: ₹").append(p.getPrice())
//                    .append(" | Brand: ").append(p.getBrand())
//                    .append(" | Category: ").append(p.getCategory())
//                    .append("\n");
//        }
//
//        // Step 4: Call LLM (RAG)
//        String prompt = """
//                You are an AI shopping assistant.
//
//                User Query:
//                %s
//
//                %s
//
//                Recommend the best products with reasons.
//                """.formatted(query, context.toString());
//
//        return callLLM(prompt);
//    }
//
//    // compare
//    private String handleCompare(String query) {
//
//        List<Double> queryEmbedding = embeddingService.getEmbedding(query);
//
//        List<Products> top = productRepository.findAll().stream()
//                .sorted((p1, p2) -> Double.compare(
//                        similarity(p2, queryEmbedding),
//                        similarity(p1, queryEmbedding)
//                ))
//                .limit(3) // give more context
//                .toList();
//
//        if (top.size() < 2) return "Not enough matching products";
//
//        // Build context
//        StringBuilder context = new StringBuilder("Products for comparison:\n");
//        for (Products p : top) {
//            context.append("- ")
//                    .append(p.getName())
//                    .append(" | Price: ₹").append(p.getPrice())
//                    .append(" | Brand: ").append(p.getBrand())
//                    .append("\n");
//        }
//
//        String prompt = """
//                You are an AI assistant.
//
//                User Query:
//                %s
//
//                %s
//
//                Compare the most relevant products clearly (price, features, recommendation).
//                """.formatted(query, context.toString());
//
//        return callLLM(prompt);
//    }
//
//    // 📝 SUMMARY (NOW RAG - IMPROVED)
//    private String handleSummary(String query) {
//
//        List<Double> queryEmbedding = embeddingService.getEmbedding(query);
//
//        Products product = productRepository.findAll().stream()
//                .max(Comparator.comparing(p -> similarity(p, queryEmbedding)))
//                .orElse(null);
//
//        if (product == null) return "No relevant product found";
//
//        List<Review> reviews = reviewRepository.findByProductId(product.getId());
//
//        if (reviews.isEmpty()) return "No reviews available";
//
//        // Build review context
//        StringBuilder reviewContext = new StringBuilder();
//        for (Review r : reviews) {
//            reviewContext.append("- ").append(r.getComment()).append("\n");
//        }
//
//        String prompt = """
//                You are an AI assistant.
//
//                User Query:
//                %s
//
//                Product:
//                %s
//
//                Reviews:
//                %s
//
//                Summarize the reviews. Mention pros and cons clearly.
//                """.formatted(query, product.getName(), reviewContext.toString());
//
//        return callLLM(prompt);
//    }
//
//    // SIMILARITY (UNCHANGED)
//    private double similarity(Products p, List<Double> queryEmbedding) {
//        if (p.getEmbedding() == null) return 0;
//        return VectorUtils.cosineSimilarity(p.getEmbedding(), queryEmbedding);
//    }
//
//    // LLM CALL (UNCHANGED)
//    private String callLLM(String prompt) {
//
//        RestTemplate restTemplate = new RestTemplate();
//
//        String url = "http://localhost:11434/api/generate";
//
//        Map<String, Object> body = new HashMap<>();
//        body.put("model", "tinyllama"); // lightweight model
//        body.put("prompt", prompt);
//        body.put("stream", false);
//
//        Map response = restTemplate.postForObject(url, body, Map.class);
//
//        return response.get("response").toString();
//    }
//
//    // SAVE CHAT (UNCHANGED)
//    private void saveChat(Long userId, String query, String response) {
//
//        ChatHistory chat = ChatHistory.builder()
//                .userId(userId)
//                .query(query)
//                .response(response)
//                .timestamp(LocalDateTime.now())
//                .build();
//
//        chatRepository.save(chat);
//    }
//}