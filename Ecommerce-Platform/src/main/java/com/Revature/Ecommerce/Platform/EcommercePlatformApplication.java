package com.Revature.Ecommerce.Platform;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.core.MongoTemplate;

@SpringBootApplication
public class EcommercePlatformApplication {

    @Autowired
    private MongoTemplate mongoTemplate;

    @PostConstruct
    public void testMongoConnection() {
        try {
            String dbName = mongoTemplate.getDb().getName();
            System.out.println("Connected to MongoDB Database: " + dbName);
        } catch (Exception e) {
            System.out.println("MongoDB Connection Failed");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SpringApplication.run(EcommercePlatformApplication.class, args);
    }
}