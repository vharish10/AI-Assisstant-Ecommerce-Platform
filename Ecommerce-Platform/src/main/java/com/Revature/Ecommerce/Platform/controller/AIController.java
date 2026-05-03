package com.Revature.Ecommerce.Platform.controller;

import com.Revature.Ecommerce.Platform.service.AIService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ai")
@Tag(name = "AI Controller")
public class AIController {

    @Autowired
    private AIService service;

    @PostMapping("/query")
    public ResponseEntity<String> query(
            @RequestParam Long userId,
            @RequestBody String query) {

        return ResponseEntity.ok(service.handleQuery(userId, query));
    }
}