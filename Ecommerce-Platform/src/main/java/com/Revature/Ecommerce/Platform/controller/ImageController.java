package com.Revature.Ecommerce.Platform.controller;

import com.Revature.Ecommerce.Platform.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/images")
public class ImageController {

    @Autowired
    private ImageService imageService;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile file) {

        String imageUrl = imageService.uploadImage(file);

        return ResponseEntity.ok(imageUrl);
    }
}