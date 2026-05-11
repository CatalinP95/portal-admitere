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

    private static final double W_BAC  = 0.6;
    private static final double W_DIF1 = 0.2;
    private static final double W_DIF2 = 0.1;
    private static final double W_DIF3 = 0.1;

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
            return new AlgorithmResultDto(sessionId.longValue(), 0, 0, 0, 0, List.of());
        }

        Map<Integer, List<ApplicationRankDto>> byFaculty = candidates.stream()
                .collect(Collectors.groupingBy(ApplicationRankDto::getFacultyId));

        List<ApplicationStatusUpdate> updates = new ArrayList<>();
        int totalBuget = 0, totalTaxa = 0, totalWaiting = 0;
        List<FacultyRankingResult> facultyResults = new ArrayList<>();

        for (Map.Entry<Integer, List<ApplicationRankDto>> entry : byFaculty.entrySet()) {
            Integer facultyId = entry.getKey();
            List<ApplicationRankDto> facultyCandidates = entry.getValue();

            FacultySpotsDto spots = admissionsClient.getFacultySpots(facultyId);
            int bugetLeft = spots.getNrBuget() != null ? spots.getNrBuget() : 0;
            int taxaLeft  = spots.getNrTaxa()  != null ? spots.getNrTaxa()  : 0;

            List<ApplicationRankDto> bugetCandidates = facultyCandidates.stream()
                    .filter(a -> Integer.valueOf(1).equals(a.getFormFunding()))
                    .sorted(Comparator.comparingDouble(this::computeScore).reversed())
                    .collect(Collectors.toList());

            List<ApplicationRankDto> taxaCandidates = facultyCandidates.stream()
                    .filter(a -> !Integer.valueOf(1).equals(a.getFormFunding()))
                    .sorted(Comparator.comparingDouble(this::computeScore).reversed())
                    .collect(Collectors.toList());

            int facBuget = 0, facTaxa = 0, facWaiting = 0;
            double minScore = Double.MAX_VALUE;

            // candidatii buget concureaza pe locuri buget
            for (ApplicationRankDto app : bugetCandidates) {
                if (bugetLeft > 0) {
                    updates.add(new ApplicationStatusUpdate(app.getApplicationId(), "APPROVED"));
                    minScore = Math.min(minScore, computeScore(app));
                    bugetLeft--;
                    facBuget++;
                    totalBuget++;
                } else {
                    updates.add(new ApplicationStatusUpdate(app.getApplicationId(), "WAITING_LIST"));
                    facWaiting++;
                    totalWaiting++;
                }
            }

            // candidatii taxa: daca raman locuri buget le ocupa, apoi pe locuri taxa
            for (ApplicationRankDto app : taxaCandidates) {
                if (bugetLeft > 0) {
                    updates.add(new ApplicationStatusUpdate(app.getApplicationId(), "APPROVED"));
                    minScore = Math.min(minScore, computeScore(app));
                    bugetLeft--;
                    facBuget++;
                    totalBuget++;
                } else if (taxaLeft > 0) {
                    updates.add(new ApplicationStatusUpdate(app.getApplicationId(), "APPROVED"));
                    minScore = Math.min(minScore, computeScore(app));
                    taxaLeft--;
                    facTaxa++;
                    totalTaxa++;
                } else {
                    updates.add(new ApplicationStatusUpdate(app.getApplicationId(), "WAITING_LIST"));
                    facWaiting++;
                    totalWaiting++;
                }
            }

            double finalMinScore = minScore == Double.MAX_VALUE ? 0.0
                    : Math.round(minScore * 100.0) / 100.0;

            facultyResults.add(new FacultyRankingResult(
                    facultyId, facultyCandidates.size(),
                    facBuget, facTaxa, facWaiting, finalMinScore));

            log.info("Facultate {}: {} buget, {} taxa, {} waiting, scor minim {}",
                    facultyId, facBuget, facTaxa, facWaiting, finalMinScore);
        }

        admissionsClient.updateStatuses(new BulkStatusRequest(updates));

        int total = candidates.size();
        log.info("Algoritm finalizat: {} total, {} buget, {} taxa, {} waiting",
                total, totalBuget, totalTaxa, totalWaiting);

        return new AlgorithmResultDto(sessionId.longValue(), total,
                totalBuget, totalTaxa, totalWaiting, facultyResults);
    }

    double computeScore(ApplicationRankDto app) {
        double bac  = app.getAverageBac() != null ? app.getAverageBac() : 0.0;
        double dif1 = app.getMarkDif1()   != null ? app.getMarkDif1()   : 0.0;
        double dif2 = app.getMarkDif2()   != null ? app.getMarkDif2()   : 0.0;
        double dif3 = app.getMarkDif3()   != null ? app.getMarkDif3()   : 0.0;
        return W_BAC * bac + W_DIF1 * dif1 + W_DIF2 * dif2 + W_DIF3 * dif3;
    }
}
