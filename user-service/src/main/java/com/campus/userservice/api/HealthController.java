package com.campus.userservice.api;

import com.campus.userservice.service.HealthCheckService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/health")
@Tag(name = "Health", description = "Health check endpoints cu Circuit Breaker")
public class HealthController {

    private final HealthCheckService healthCheckService;

    public HealthController(HealthCheckService healthCheckService) {
        this.healthCheckService = healthCheckService;
    }

    @GetMapping("/discovery")
    @Operation(summary = "Verifică starea Discovery Server (Eureka) cu Circuit Breaker")
    public ResponseEntity<String> discoveryHealth() {
        return ResponseEntity.ok(healthCheckService.checkDiscovery());
    }
}
