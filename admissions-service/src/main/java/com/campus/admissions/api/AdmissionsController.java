package com.campus.admissions.api;

import com.campus.admissions.model.*;
import com.campus.admissions.service.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admissions")
public class AdmissionsController {

    private final ApplicationService applicationService;
    private final SessionService sessionService;
    private final FacultyService facultyService;
    private final UniversityService universityService;
    private final ContractService contractService;

    public AdmissionsController(ApplicationService applicationService,
                                 SessionService sessionService,
                                 FacultyService facultyService,
                                 UniversityService universityService,
                                 ContractService contractService) {
        this.applicationService = applicationService;
        this.sessionService = sessionService;
        this.facultyService = facultyService;
        this.universityService = universityService;
        this.contractService = contractService;
    }

    // --- depunere cerere ---

    @PostMapping("/cerere")
    public ResponseEntity<Application> depuneCerere(@RequestBody Application application,
                                                     @RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.ok(applicationService.submit(application, userId));
    }

    @GetMapping("/cerere/ale-mele")
    public ResponseEntity<List<Application>> cereriMele(@RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.ok(applicationService.findByUserId(userId));
    }

    // --- confirmare spot ---

    // buget: studentul confirma ca a trimis diploma
    @PostMapping("/cerere/{id}/confirma-diploma")
    public ResponseEntity<Boolean> confirmaDiploma(@PathVariable Integer id,
                                                    @RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.ok(applicationService.confirmWithDiploma(userId, id));
    }

    // taxa: studentul plateste X procent din taxa
    @PostMapping("/cerere/{id}/confirma-plata")
    public ResponseEntity<Boolean> confirmaPlata(@PathVariable Integer id,
                                                  @RequestHeader("X-User-Id") Long userId,
                                                  @RequestParam Float paymentPercentage) {
        return ResponseEntity.ok(applicationService.confirmWithPayment(userId, id, paymentPercentage));
    }

    // --- rezultate proprii ---

    @GetMapping("/rezultate")
    public ResponseEntity<List<Application>> rezultateMele(@RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.ok(applicationService.findByUserId(userId));
    }

    // --- date pentru formular cerere ---

    @GetMapping("/universitati")
    public ResponseEntity<List<University>> getUniversitati() {
        return ResponseEntity.ok(universityService.findAll());
    }

    @GetMapping("/facultati/{universityId}")
    public ResponseEntity<List<Faculty>> getFacultatiByUniversitate(@PathVariable Integer universityId) {
        University univ = universityService.findById(universityId);
        return ResponseEntity.ok(facultyService.findByUniversity(univ));
    }

    @GetMapping("/sesiuni/active")
    public ResponseEntity<List<Session>> getActiveSesiuni() {
        return ResponseEntity.ok(sessionService.findActiveSessions());
    }

    // --- statistici publice ---

    @GetMapping("/statistica/perFacultate")
    public ResponseEntity<List<Object[]>> statisticiPerFacultate() {
        return ResponseEntity.ok(applicationService.countPerFaculty());
    }

    // --- contract propriu ---

    @GetMapping("/contract")
    public ResponseEntity<List<Contract>> contractulMeu(@RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.ok(contractService.findByUserId(userId));
    }
}
