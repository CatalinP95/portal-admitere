package com.campus.admissions.api;

import com.campus.admissions.model.*;
import com.campus.admissions.service.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/secretariat")
public class SecretariatController {

    private final SessionService sessionService;
    private final ApplicationService applicationService;
    private final ContractService contractService;
    private final FacultyService facultyService;
    private final UniversityService universityService;

    public SecretariatController(SessionService sessionService,
                                  ApplicationService applicationService,
                                  ContractService contractService,
                                  FacultyService facultyService,
                                  UniversityService universityService) {
        this.sessionService = sessionService;
        this.applicationService = applicationService;
        this.contractService = contractService;
        this.facultyService = facultyService;
        this.universityService = universityService;
    }

    @PostMapping("/sesiune")
    public ResponseEntity<Session> createSession(@RequestBody Session session) {
        return ResponseEntity.ok(sessionService.save(session));
    }

    @GetMapping("/sesiuni")
    public ResponseEntity<List<Session>> listSessions() {
        return ResponseEntity.ok(sessionService.findAll());
    }

    @GetMapping("/sesiuni/active")
    public ResponseEntity<List<Session>> activeSessions() {
        return ResponseEntity.ok(sessionService.findActiveSessions());
    }

    @DeleteMapping("/sesiune/{id}")
    public ResponseEntity<Void> deleteSession(@PathVariable Integer id) {
        Session session = sessionService.findById(id);
        if (session != null) sessionService.delete(session);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/cereriPrimite/get/view")
    public ResponseEntity<List<Application>> getCereriPrimite(@RequestBody Session session) {
        return ResponseEntity.ok(applicationService.findBySessionAndStatus(session, ApplicationStatus.PENDING.name()));
    }

    @GetMapping("/cereriPrimite/toate/{sessionId}")
    public ResponseEntity<List<Application>> getCereriBySession(@PathVariable Integer sessionId) {
        Session session = sessionService.findById(sessionId);
        return ResponseEntity.ok(applicationService.findBySessionAndStatus(session, ApplicationStatus.PENDING.name()));
    }

    @PostMapping("/rezultate/get/view")
    public ResponseEntity<List<Application>> getRezultate(@RequestBody Session session) {
        return ResponseEntity.ok(applicationService.findBySessionAndStatus(session, ApplicationStatus.CONFIRMED.name()));
    }

    @GetMapping("/listaAsteptare/{sessionId}/{facultyId}")
    public ResponseEntity<List<Application>> getWaitingList(@PathVariable Integer sessionId,
                                                             @PathVariable Integer facultyId) {
        Session session = sessionService.findById(sessionId);
        Faculty faculty = facultyService.findById(facultyId);
        return ResponseEntity.ok(applicationService.findWaitingList(session, faculty));
    }


    @GetMapping("/statistici/perFacultate")
    public ResponseEntity<List<Object[]>> statisticiPerFacultate() {
        return ResponseEntity.ok(applicationService.countPerFaculty());
    }

    @GetMapping("/universitati")
    public ResponseEntity<List<University>> getUniversitati() {
        return ResponseEntity.ok(universityService.findAll());
    }

    @PostMapping("/universitate")
    public ResponseEntity<Boolean> saveUniversitate(@RequestBody University university) {
        return ResponseEntity.ok(universityService.save(university));
    }

    @GetMapping("/facultati")
    public ResponseEntity<List<Faculty>> getFacultati() {
        return ResponseEntity.ok(facultyService.findAll());
    }

    @PostMapping("/facultate")
    public ResponseEntity<Boolean> saveFacultate(@RequestBody Faculty faculty) {
        return ResponseEntity.ok(facultyService.save(faculty));
    }

    @GetMapping("/contracte/{userId}")
    public ResponseEntity<List<Contract>> getContracteByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(contractService.findByUserId(userId));
    }
}
