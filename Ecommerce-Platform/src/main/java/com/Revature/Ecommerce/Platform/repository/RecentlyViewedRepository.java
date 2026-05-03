package com.Revature.Ecommerce.Platform.repository;

import com.Revature.Ecommerce.Platform.models.RecentlyViewed;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RecentlyViewedRepository extends MongoRepository<RecentlyViewed, String> {

    Optional<RecentlyViewed> findByUserId(Long userId);
}
