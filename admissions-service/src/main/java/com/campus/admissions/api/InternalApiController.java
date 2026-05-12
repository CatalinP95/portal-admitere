package com.campus.admissions.api;

import com.campus.admissions.dto.algorithm.ApplicationRankDto;
import com.campus.admissions.model.Application;
import com.campus.admissions.model.Faculty;
import com.campus.admissions.model.Session;
import com.campus.admissions.service.ApplicationService;
import com.campus.admissions.service.FacultyService;
import com.campus.admissions.service.SessionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Endpoint-uri interne consumate de user-service via Feign
// pentru algoritmul automat de admitere
@RestController
@RequestMapping("/internal")
public class InternalApiController {

    private final ApplicationService applicationService;
    private final SessionService sessionService;
    private final FacultyService facultyService;

    public InternalApiController(ApplicationService applicationService,
                                  SessionService sessionService,
                                  FacultyService facultyService) {
        this.applicationService = applicationService;
        this.sessionService = sessionService;
        this.facultyService = facultyService;
    }


    // returneaza toate cererile PENDING dintr-o sesiune (pentru ranking)
    @GetMapping("/applications/pending/{sessionId}")
    public ResponseEntity<List<ApplicationRankDto>> getPendingApplications(@PathVariable Integer sessionId) {
        return ResponseEntity.ok(
                applicationService.getApplicationsRank(
                applicationService.findBySessionIdAndStatus(sessionId, "PENDING")
                ));
    }

    // returneaza locurile disponibile per facultate pentru o sesiune
    @GetMapping("/faculty/{facultyId}/spots")
    public ResponseEntity<Session> getSpotsForFaculty(@PathVariable Integer facultyId,
                                                        @RequestParam Integer sessionId) {
        Session session = sessionService.findById(sessionId);
        return ResponseEntity.ok(session);
    }

    // algoritmul Person A apeleaza acest endpoint cu rezultatele ranking-ului
    // pentru a actualiza bulk statusurile cererilor (APPROVED / WAITING_LIST / REJECTED)
    @PutMapping("/applications/bulk-status")
    public ResponseEntity<Void> bulkUpdateStatus(@RequestBody List<Application> applications) {
        applicationService.bulkUpdateStatus(applications);
        return ResponseEntity.ok().build();
    }

    // sesiuni active (pentru a sti pe ce sesiune ruleaza algoritmul)
    @GetMapping("/sessions/active")
    public ResponseEntity<List<Session>> getActiveSessions() {
        return ResponseEntity.ok(sessionService.findActiveSessions());
    }

    // lista de asteptare per facultate (pentru algoritmul de promovare)
    @GetMapping("/applications/waiting-list/{sessionId}/{facultyId}")
    public ResponseEntity<List<Application>> getWaitingList(@PathVariable Integer sessionId,
                                                              @PathVariable Integer facultyId) {
        Session session = sessionService.findById(sessionId);
        Faculty faculty = facultyService.findById(facultyId);
        return ResponseEntity.ok(applicationService.findWaitingList(session, faculty));
    }
}
