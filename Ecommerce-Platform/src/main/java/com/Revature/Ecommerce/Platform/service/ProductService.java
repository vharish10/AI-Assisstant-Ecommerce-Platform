package com.Revature.Ecommerce.Platform.service;

import com.Revature.Ecommerce.Platform.CustomExceptions.*;
import com.Revature.Ecommerce.Platform.models.Products;
import com.Revature.Ecommerce.Platform.repository.ProductRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class ProductService {

    private static final Logger log = LoggerFactory.getLogger(ProductService.class);

    @Autowired
    private ProductRepository repository;

    @Autowired
    private MongoOperations mongoTemplate;

    public Products createProduct(Products product) {
        log.info("Creating product: {}", product.getName());
        return repository.save(product);
    }

    public Products getProductById(String id) {
        log.info("Fetching product with ID: {}", id);
        return repository.findById(id).orElseThrow(() -> {
                    log.error("Product not found with ID: {}", id);
                    return new ProductNotFoundException("Product not found");
        });
    }

    public Products updateProduct(String id, Products updated, Long sellerId) {
        log.info("Updating product ID: {} by seller: {}", id, sellerId);
        Products product=getProductById(id);
        if(!product.getSellerId().equals(sellerId)){
            log.warn("Unauthorized update attempt by seller: {}", sellerId);
            throw new UnauthorizedException("You cannot update this product");
        }
        product.setName(updated.getName());
        product.setPrice(updated.getPrice());
        product.setCategory(updated.getCategory());
        product.setBrand(updated.getBrand());
        product.setStock(updated.getStock());
        product.setAttributes(updated.getAttributes());
        product.setImages(updated.getImages());
        product.setTags(updated.getTags());
        log.info("Product updated successfully: {}", id);
        return repository.save(product);
    }

    public void deleteProduct(String id, Long sellerId){
        log.info("Deleting product ID: {} by seller: {}", id, sellerId);
        Products product=getProductById(id);
        if(!product.getSellerId().equals(sellerId)){
            log.warn("Unauthorized delete attempt by seller: {}", sellerId);
            throw new UnauthorizedException("You cannot delete this product");
        }
        repository.deleteById(id);
        log.info("Product deleted successfully: {}", id);
    }
    //multiple search conditions
    public Page<Products> searchProducts(
            String keyword,
            String category,
            String brand,
            Double minPrice,
            Double maxPrice,
            String tag,
            int page,
            int size,
            String sortBy,
            String sortDir) {

        log.info("Searching products with filters");

        Sort sort = Sort.by(
                "desc".equalsIgnoreCase(sortDir) ? Sort.Direction.DESC : Sort.Direction.ASC,
                sortBy != null ? sortBy : "price"
        );

        Pageable pageable = PageRequest.of(page, size, sort);

        if(minPrice!=null && maxPrice!=null && minPrice>maxPrice){
            log.error("Invalid price range: {} - {}", minPrice, maxPrice);
            throw new InvalidFilterException("Invalid price range");
        }

        Query query = new Query();
        List<Criteria> criteriaList = new ArrayList<>();

        if(keyword!=null && !keyword.isEmpty()){
            criteriaList.add(new Criteria().orOperator(
                    Criteria.where("name").regex(keyword, "i"),
                    Criteria.where("description").regex(keyword, "i"),
                    Criteria.where("brand").regex(keyword, "i")
            ));
        }

        if(category != null && !category.isEmpty()){
            criteriaList.add(Criteria.where("category").is(category));
        }

        if(brand != null && !brand.isEmpty()){
            criteriaList.add(Criteria.where("brand").is(brand));
        }

        if(minPrice != null && maxPrice != null){
            criteriaList.add(Criteria.where("price").gte(minPrice).lte(maxPrice));
        }

        if(tag != null && !tag.isEmpty()){
            criteriaList.add(Criteria.where("tags").in(tag));
        }
        criteriaList.add(Criteria.where("stock").gt(0));
        if(!criteriaList.isEmpty()){
            query.addCriteria(new Criteria().andOperator(criteriaList.toArray(new Criteria[0])));
        }

        long total = mongoTemplate.count(query, Products.class);
        query.with(pageable);

        List<Products> products = mongoTemplate.find(query, Products.class);

        if(products.isEmpty()){
            log.warn("No products found for given filters");
        } else {
            log.info("Found {} products", total);
        }
        return new PageImpl<>(products, pageable, total);
    }
}