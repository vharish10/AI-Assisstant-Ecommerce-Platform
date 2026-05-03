package com.Revature.Ecommerce.Platform.repository;

import com.Revature.Ecommerce.Platform.models.ChatHistory;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.*;

public interface ChatHistoryRepository extends MongoRepository<ChatHistory, String> {

    List<ChatHistory> findByUserId(Long userId);
}
