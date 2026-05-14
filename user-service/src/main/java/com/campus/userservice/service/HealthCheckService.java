package com.campus.userservice.service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class HealthCheckService {

    private static final Logger log = LoggerFactory.getLogger(HealthCheckService.class);

    private final RestTemplate restTemplate = new RestTemplate();

    @CircuitBreaker(name = "discovery-server", fallbackMethod = "discoveryFallback")
    public String checkDiscovery() {
        log.info("Checking discovery server health...");
        return restTemplate.getForObject("http://localhost:8761/actuator/health", String.class);
    }

    public String discoveryFallback(Exception e) {
        log.warn("Discovery server unavailable: {}", e.getMessage());
        return "{\"status\": \"Discovery unavailable\"}";
    }
}
