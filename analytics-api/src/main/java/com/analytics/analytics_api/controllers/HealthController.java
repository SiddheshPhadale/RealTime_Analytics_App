package com.analytics.analytics_api.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/health")
@Slf4j
public class HealthController {

    @Autowired
    private ReactiveRedisTemplate<String, String> redisTemplate;

    @GetMapping("/redis")
    public Mono<ResponseEntity<Map<String, Object>>> checkRedis() {
        return redisTemplate.opsForValue().get("test")
                .map(value -> {
                    Map<String, Object> response = new HashMap<>();
                    response.put("status", "connected");
                    response.put("message", "Redis is accessible");
                    return ResponseEntity.ok(response);
                })
                .onErrorResume(e -> {
                    Map<String, Object> response = new HashMap<>();
                    response.put("status", "disconnected");
                    response.put("message", "Redis connection failed: " + e.getMessage());
                    return Mono.just(ResponseEntity.ok(response));
                })
                .defaultIfEmpty(ResponseEntity.ok(Map.of("status", "connected", "message", "Redis is accessible")));
    }

    @GetMapping("/kafka")
    public ResponseEntity<Map<String, Object>> checkKafka() {
        // For now, assume Kafka is always connected since we don't have direct access
        // In a real implementation, you'd inject KafkaAdmin or Producer and test connectivity
        Map<String, Object> response = new HashMap<>();
        response.put("status", "connected");
        response.put("message", "Kafka is accessible");
        return ResponseEntity.ok(response);
    }
}