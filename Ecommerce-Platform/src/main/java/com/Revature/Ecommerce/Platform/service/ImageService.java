package com.Revature.Ecommerce.Platform.service;

import com.Revature.Ecommerce.Platform.CustomExceptions.ProductNotFoundException;
import com.Revature.Ecommerce.Platform.CustomExceptions.UnauthorizedException;
import com.Revature.Ecommerce.Platform.dto.ProductRequestDTO;
import com.Revature.Ecommerce.Platform.models.Products;
import com.Revature.Ecommerce.Platform.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.UUID;

@Service
public class ImageService {

    @Autowired
    private ProductRepository productRepository;

    private final String uploadDir = "src/main/resources/static/images/";

    public String uploadImage(MultipartFile file) {

        try {
            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();

            Path path = Paths.get(uploadDir + fileName);

            Files.createDirectories(path.getParent());

            Files.write(path, file.getBytes());

            return "/images/" + fileName; // URL path

        } catch (IOException e) {
            throw new RuntimeException("Failed to upload image");
        }
    }

    public Products updateProductWithImages(String id,
                                            ProductRequestDTO dto,
                                            Long sellerId,
                                            List<String> images) {

        Products product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found"));

        if (!product.getSellerId().equals(sellerId)) {
            throw new UnauthorizedException("Not your product");
        }

        // update fields
        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setStock(dto.getStock());

        if (images != null && !images.isEmpty()) {
            product.setImages(images);   // replace images
        }

        return productRepository.save(product);
    }
}