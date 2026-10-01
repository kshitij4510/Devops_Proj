package com.agrirent.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.management.ManagementFactory;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/health")
@CrossOrigin(origins = "*")
public class HealthController {

    private final long startTime = System.currentTimeMillis();

    @GetMapping
    public ResponseEntity<Map<String, Object>> checkHealth() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("service", "farm-equipment-rental");
        health.put("version", "1.0.0-mvp");
        health.put("timestamp", LocalDateTime.now().toString());
        health.put("uptimeMs", System.currentTimeMillis() - startTime);
        health.put("jvmUptimeMs", ManagementFactory.getRuntimeMXBean().getUptime());
        health.put("availableProcessors", Runtime.getRuntime().availableProcessors());
        health.put("freeMemoryBytes", Runtime.getRuntime().freeMemory());
        health.put("totalMemoryBytes", Runtime.getRuntime().totalMemory());
        return ResponseEntity.ok(health);
    }
}
