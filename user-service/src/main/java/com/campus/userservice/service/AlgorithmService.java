package com.campus.userservice.service;

import com.campus.userservice.client.AdmissionsClient;
import com.campus.userservice.dto.algorithm.*;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class AlgorithmService {

    private static final Logger log = LoggerFactory.getLogger(AlgorithmService.class);

    private final AdmissionsClient admissionsClient;

    public AlgorithmService(AdmissionsClient admissionsClient) {
        this.admissionsClient = admissionsClient;
    }

    @CircuitBreaker(name = "admissions-service")
    @Retry(name = "admissions-service")
    public AlgorithmResultDto runRanking(Integer sessionId) {
        log.info("Pornire algoritm ranking pentru sesiunea {}", sessionId);

        List<ApplicationRankDto> candidates = admissionsClient.getPendingApplications(sessionId);
        if (candidates.isEmpty()) {
            log.warn("Niciun candidat PENDING pentru sesiunea {}", sessionId);
            return new AlgorithmResultDto(sessionId.longValue(), 0, 0, 0, 0);
        }

        Map<Integer, List<ApplicationRankDto>> byFaculty = candidates.stream()
                .collect(Collectors.groupingBy(ApplicationRankDto::getFacultyId));

        List<ApplicationStatusUpdate> updates = new ArrayList<>();
        int totalBuget = 0, totalTaxa = 0, totalWaiting = 0;

        for (Map.Entry<Integer, List<ApplicationRankDto>> entry : byFaculty.entrySet()) {
            Integer facultyId = entry.getKey();
            List<ApplicationRankDto> sorted = entry.getValue().stream()
                    .sorted(Comparator.comparing(ApplicationRankDto::getAverageBac).reversed())
                    .collect(Collectors.toList());

            FacultySpotsDto spots = admissionsClient.getFacultySpots(facultyId);
            int bugetLeft = spots.getNrBuget() != null ? spots.getNrBuget() : 0;
            int taxaLeft = spots.getNrTaxa() != null ? spots.getNrTaxa() : 0;

            for (ApplicationRankDto app : sorted) {
                if (bugetLeft > 0) {
                    updates.add(new ApplicationStatusUpdate(app.getApplicationId(), "APPROVED"));
                    bugetLeft--;
                    totalBuget++;
                } else if (taxaLeft > 0) {
                    updates.add(new ApplicationStatusUpdate(app.getApplicationId(), "APPROVED"));
                    taxaLeft--;
                    totalTaxa++;
                } else {
                    updates.add(new ApplicationStatusUpdate(app.getApplicationId(), "WAITING_LIST"));
                    totalWaiting++;
                }
            }

            log.info("Facultate {}: {} buget, {} taxa, {} waiting",
                    facultyId, spots.getNrBuget() - bugetLeft,
                    spots.getNrTaxa() - taxaLeft, totalWaiting);
        }

        admissionsClient.updateStatuses(new BulkStatusRequest(updates));

        int total = candidates.size();
        log.info("Algoritm finalizat: {} total, {} buget, {} taxa, {} waiting",
                total, totalBuget, totalTaxa, totalWaiting);

        return new AlgorithmResultDto(sessionId.longValue(), total, totalBuget, totalTaxa, totalWaiting);
    }
}
