package com.campus.userservice.api;

import com.campus.userservice.dto.algorithm.AlgorithmResultDto;
import com.campus.userservice.service.AlgorithmService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/algorithm")
public class AlgorithmController {

    private final AlgorithmService algorithmService;

    public AlgorithmController(AlgorithmService algorithmService) {
        this.algorithmService = algorithmService;
    }

    @PostMapping("/run/{sessionId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AlgorithmResultDto> runRanking(@PathVariable Integer sessionId) {
        return ResponseEntity.ok(algorithmService.runRanking(sessionId));
    }
}
