//package com.neighbourly.userservice.service;
//
//import io.github.bucket4j.Bandwidth;
//import io.github.bucket4j.Bucket;
//import io.github.bucket4j.Bucket4j;
//import io.github.bucket4j.Refill;
//import org.springframework.stereotype.Service;
//
//import java.time.Duration;
//import java.util.Map;
//import java.util.concurrent.ConcurrentHashMap;
//
//@Service
//public class RateLimitingService {
//
//    private final Map<String, Bucket> cache = new ConcurrentHashMap<>();
//
//    // 10 requests per minute per IP
//    private static final Bandwidth DEFAULT_LIMIT = Bandwidth.classic(10,
//            Refill.intervally(10, Duration.ofMinutes(1)));
//
//    public Bucket resolveBucket(String ip) {
//        return cache.computeIfAbsent(ip, k -> Bucket4j.builder()
//                .addLimit(DEFAULT_LIMIT)
//                .build());
//    }
//
//    public void resetBucket(String ip) {
//        cache.remove(ip);
//    }
//}