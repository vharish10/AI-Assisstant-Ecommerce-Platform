package com.Revature.Ecommerce.Platform.service;

import com.Revature.Ecommerce.Platform.models.Products;
import com.Revature.Ecommerce.Platform.models.RecentlyViewed;
import com.Revature.Ecommerce.Platform.repository.ProductRepository;
import com.Revature.Ecommerce.Platform.repository.RecentlyViewedRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class RecentlyViewedService {

    private static final int MAX_SIZE=10;

    @Autowired
    private RecentlyViewedRepository repository;

    @Autowired
    private ProductRepository productRepository;

    //adding product to recently viewed products
    public void addViewedProduct(Long userId, String productId) {
        RecentlyViewed rv = repository.findByUserId(userId)
                .orElse(RecentlyViewed.builder()
                        .userId(userId)
                        .productIds(new LinkedList<>())
                        .build());

        LinkedList<String> list = rv.getProductIds();
        list.remove(productId);

        list.addFirst(productId);

        if(list.size()>MAX_SIZE){
            list.removeLast();
        }
        repository.save(rv);
    }

    //viewing recently viewed products
    public List<Products> getRecentlyViewed(Long userId) {
        RecentlyViewed rv = repository.findByUserId(userId).orElseThrow(() -> new RuntimeException("No recently viewed products"));
        return productRepository.findAllById(rv.getProductIds());
    }
}