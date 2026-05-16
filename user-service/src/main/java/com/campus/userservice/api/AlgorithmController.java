package com.campus.userservice.api;

import com.campus.userservice.dto.algorithm.AlgorithmResultDto;
import com.campus.userservice.dto.dormitory.DormitoryAllocationResultDto;
import com.campus.userservice.service.AlgorithmService;
import com.campus.userservice.service.DormitoryAlgorithmService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/algorithm")
@Tag(name = "Algoritm ranking", description = "Rulare algoritm de departajare admitere cu scor compus si alocare buget/taxa")
public class AlgorithmController {

    private final AlgorithmService algorithmService;
    private final DormitoryAlgorithmService dormitoryAlgorithmService;

    public AlgorithmController(AlgorithmService algorithmService,
                               DormitoryAlgorithmService dormitoryAlgorithmService) {
        this.algorithmService = algorithmService;
        this.dormitoryAlgorithmService = dormitoryAlgorithmService;
    }

    @Operation(
        summary = "Ruleaza algoritmul de ranking admitere",
        description = "Calculeaza scorul compus (0.6×medBac + 0.2×dif1 + 0.1×dif2 + 0.1×dif3) pentru toti candidatii " +
            "dintr-o sesiune, ii sorteaza descrescator si aloca locuri buget/taxa per facultate. " +
            "Candidatii nepromovati primesc status WAITING_LIST. Returneaza statistici per facultate."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Ranking rulat cu succes — returneaza statistici globale si per facultate"),
        @ApiResponse(responseCode = "403", description = "Acces interzis — necesita rol ADMIN"),
        @ApiResponse(responseCode = "503", description = "admissions-service indisponibil — circuit breaker activ")
    })
    @PostMapping("/run/{sessionId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AlgorithmResultDto> runRanking(
            @Parameter(description = "ID-ul sesiunii de admitere pentru care se ruleaza algoritmul")
            @PathVariable Integer sessionId) {
        return ResponseEntity.ok(algorithmService.runRanking(sessionId));
    }

    @Operation(
        summary = "Ruleaza algoritmul de alocare camin",
        description = "Preia cererile PENDING dintr-o sesiune de camin, calculeaza scorul " +
            "(0.9×medieBac/10 + 0.1×distanta/500), sorteaza descrescator si trimite alocarea " +
            "catre dormitory-service care atribuie paturile."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Alocare rulata cu succes"),
        @ApiResponse(responseCode = "403", description = "Acces interzis — necesita rol ADMIN"),
        @ApiResponse(responseCode = "503", description = "dormitory-service sau admissions-service indisponibil")
    })
    @PostMapping("/dormitory/run/{sessionId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DormitoryAllocationResultDto> runDormitoryAllocation(
            @Parameter(description = "ID-ul sesiunii de alocare camin")
            @PathVariable Integer sessionId) {
        return ResponseEntity.ok(dormitoryAlgorithmService.runAllocation(sessionId));
    }
}
