package com.campus.userservice.service;

import com.campus.userservice.client.AdmissionsClient;
import com.campus.userservice.client.DormitoryClient;
import com.campus.userservice.dto.dormitory.*;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class DormitoryAlgorithmService {

    private static final Logger log = LoggerFactory.getLogger(DormitoryAlgorithmService.class);

    // formula: 90% medie bac (0-10) + 10% distanta normalizata la 500 km
    private static final double W_BAC  = 0.9;
    private static final double W_DIST = 0.1;
    private static final double MAX_DISTANCE_KM = 500.0;

    private final DormitoryClient dormitoryClient;
    private final AdmissionsClient admissionsClient;

    public DormitoryAlgorithmService(DormitoryClient dormitoryClient,
                                     AdmissionsClient admissionsClient) {
        this.dormitoryClient = dormitoryClient;
        this.admissionsClient = admissionsClient;
    }

    @CircuitBreaker(name = "dormitory-service")
    @Retry(name = "dormitory-service")
    public DormitoryAllocationResultDto runAllocation(Integer sessionId) {
        log.info("Pornire algoritm alocare camin pentru sesiunea {}", sessionId);

        List<BlockRequestRankDto> pending = dormitoryClient.getPendingRequests(sessionId);
        if (pending.isEmpty()) {
            log.warn("Nicio cerere PENDING pentru sesiunea de camin {}", sessionId);
            return new DormitoryAllocationResultDto(sessionId, 0, 0, 0);
        }

        // pentru fiecare cerere: preia datele de la admissions-service si calculeaza scorul
        List<ScoredRequest> scored = new ArrayList<>();
        for (BlockRequestRankDto req : pending) {
            try {
                StudentAllocationDataDto studentData =
                        admissionsClient.getStudentAllocationData(req.getUserId());
                double score = computeScore(studentData.getAverageBac(), studentData.getDistanceKm());
                int roomType = getRoomType(studentData.getSex(), studentData.getMedicalCondition());
                scored.add(new ScoredRequest(req.getBlockRequestId(), roomType, score));
            } catch (Exception e) {
                log.warn("Nu s-au putut prelua datele pentru userId={}, cererea este ignorata: {}",
                        req.getUserId(), e.getMessage());
            }
        }

        // sorteaza descrescator dupa scor — studentul cu scorul cel mai mare primeste loc primul
        scored.sort(Comparator.comparingDouble(ScoredRequest::score).reversed());

        List<BedAllocationRequest> allocations = scored.stream()
                .map(s -> new BedAllocationRequest(s.blockRequestId(), s.roomType(), s.score()))
                .toList();

        dormitoryClient.bulkAllocate(new BulkBedAllocationRequest(allocations));

        log.info("Algoritm camin finalizat: {} cereri trimise catre dormitory-service", allocations.size());
        return new DormitoryAllocationResultDto(sessionId, pending.size(), allocations.size(),
                pending.size() - allocations.size());
    }

    // scor = 90% * (medieBac / 10) + 10% * min(1, distanta / 500)
    double computeScore(Float averageBac, Double distanceKm) {
        double bac  = averageBac  != null ? averageBac  : 0.0;
        double dist = distanceKm  != null ? distanceKm  : 0.0;
        return W_BAC * (bac / 10.0) + W_DIST * Math.min(1.0, dist / MAX_DISTANCE_KM);
    }

    // Room type: 1=M sanatos, 2=F sanatoasa, 3=M conditie medicala, 4=F conditie medicala
    int getRoomType(String sex, String medicalCondition) {
        boolean hasMedical = medicalCondition != null && !medicalCondition.isBlank()
                && !"SANATOS".equalsIgnoreCase(medicalCondition);
        if ("M".equalsIgnoreCase(sex)) return hasMedical ? 3 : 1;
        if ("F".equalsIgnoreCase(sex)) return hasMedical ? 4 : 2;
        return 1;
    }

    private record ScoredRequest(Integer blockRequestId, Integer roomType, double score) {}
}
