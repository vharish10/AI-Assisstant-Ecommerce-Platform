//package com.Revature.Ecommerce.Platform.service;
//
//import com.Revature.Ecommerce.Platform.models.Intent;
//import org.springframework.stereotype.Service;
//
//@Service
//public class IntentService {
//
//    public Intent classify(String query) {
//
//        query = query.toLowerCase();
//
//        if (query.contains("compare") || query.contains("difference"))
//            return Intent.COMPARE;
//
//        if (query.contains("review") || query.contains("summary"))
//            return Intent.SUMMARY;
//
//        if (query.contains("find") || query.contains("search") || query.contains("show"))
//            return Intent.SEARCH;
//
//        return Intent.UNKNOWN;
//    }
//}