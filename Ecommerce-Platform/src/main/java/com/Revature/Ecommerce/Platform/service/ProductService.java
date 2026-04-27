package com.Revature.Ecommerce.Platform.service;

import com.Revature.Ecommerce.Platform.CustomExceptions.*;
import com.Revature.Ecommerce.Platform.models.Products;
import com.Revature.Ecommerce.Platform.repository.ProductRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

@Service
public class ProductService {

    @Autowired
    private ProductRepository repository;

    //seller/admin
    public Products createProduct(Products product) {
        return repository.save(product);
    }

    public Products getProductById(String id) {
        return repository.findById(id).orElseThrow(() -> new ProductNotFoundException("Product not found"));
    }

    public Products updateProduct(String id, Products updated, String sellerId) {
        Products product = getProductById(id);

        if(!product.getSellerId().equals(sellerId)){
            throw new UnauthorizedException("You cannot update this product");
        }
        product.setName(updated.getName());
        product.setPrice(updated.getPrice());
        product.setCategory(updated.getCategory());
        product.setBrand(updated.getBrand());
        product.setStock(updated.getStock());
        product.setAttributes(updated.getAttributes());

        return repository.save(product);
    }

    public void deleteProduct(String id, String sellerId){
        Products product=getProductById(id);

        if(!product.getSellerId().equals(sellerId)){
            throw new UnauthorizedException("You cannot delete this product");
        }

        repository.deleteById(id);
    }
    //customer
    public Page<Products> searchProducts(
            String keyword,
            String category,
            String brand,
            Double minPrice,
            Double maxPrice,
            String tag,
            Boolean inStock,
            String sellerId,
            int page,
            int size,
            String sortBy,
            String sortDir
    ) {
        Sort sort=Sort.by("desc".equalsIgnoreCase(sortDir) ? Sort.Direction.DESC : Sort.Direction.ASC,
                sortBy != null ? sortBy : "price"
        );
        Pageable pageable=PageRequest.of(page, size, sort);

        if(minPrice != null && maxPrice != null && minPrice > maxPrice){
            throw new InvalidFilterException("Invalid price range");
        }

        if (keyword != null) {
            return repository.findByNameContainingIgnoreCase(keyword, pageable);
        }

        if (category != null && brand != null && minPrice != null && maxPrice != null) {
            return repository.findByCategoryAndBrandAndPriceBetween(category, brand, minPrice, maxPrice, pageable);
        }

        if (category != null && minPrice != null && maxPrice != null) {
            return repository.findByCategoryAndPriceBetween(category, minPrice, maxPrice, pageable);
        }

        if(brand != null && minPrice != null && maxPrice != null){
            return repository.findByBrandAndPriceBetween(brand, minPrice, maxPrice, pageable);
        }

        if(category != null){
            return repository.findByCategory(category, pageable);
        }

        if(brand != null){
            return repository.findByBrand(brand, pageable);
        }

        if(minPrice!=null && maxPrice != null){
            return repository.findByPriceBetween(minPrice, maxPrice, pageable);
        }

        if(tag != null){
            return repository.findByTagsContaining(tag, pageable);
        }

        if(inStock != null && inStock){
            return repository.findByStockGreaterThan(0, pageable);
        }

        if(sellerId != null){
            return repository.findBySellerId(sellerId, pageable);
        }

        return repository.findAll(pageable);
    }
}