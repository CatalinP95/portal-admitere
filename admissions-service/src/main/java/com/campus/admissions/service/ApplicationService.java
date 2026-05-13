package com.campus.admissions.service;

import com.campus.admissions.dto.algorithm.ApplicationRankDto;
import com.campus.admissions.dto.algorithm.ApplicationStatusUpdate;
import com.campus.admissions.dto.algorithm.BulkStatusRequest;
import com.campus.admissions.model.*;
import com.campus.admissions.repository.ApplicationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.web.server.ResponseStatusException;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final AverageCompetitionService averageCompetitionService;

    @PersistenceContext
    private EntityManager entityManager;

    public ApplicationService(ApplicationRepository applicationRepository,
                              AverageCompetitionService averageCompetitionService) {
        this.applicationRepository = applicationRepository;
        this.averageCompetitionService = averageCompetitionService;
    }

    public Page<Application> findAll(Pageable pageable) {
        return applicationRepository.findAll(pageable);
    }

    public Application findById(Integer id) {
        return applicationRepository.findById(id).orElse(null);
    }

    public List<Application> findByUserId(Long userId) {
        return applicationRepository.findByUserId(userId);
    }

    public List<ApplicationRankDto> getApplicationsRank(List<Application> applicationList) {
        return applicationList.stream().map(app -> {
            ApplicationRankDto dto = ApplicationRankDto.builder()
                    .applicationId(app.getId().longValue())
                    .userId(app.getUserId())
                    .facultyId(app.getFaculty().getId())
                    .formFunding(app.getFormFunding())
                    .build();

            averageCompetitionService.getByUserId(app.getUserId())
                    .ifPresent(avg -> {
                        dto.setAverageBac(avg.getAverageBac());
                        dto.setMarkDif1(avg.getMarkDif1());
                        dto.setMarkDif2(avg.getMarkDif2());
                        dto.setMarkDif3(avg.getMarkDif3());
                    });
            return dto;
        }).toList();
    }

    public List<Application> findBySessionIdAndStatus(Integer sessionId, String status) {
        return applicationRepository.findBySessionIdAndStatus(sessionId, status);
    }

    public List<Application> findBySessionAndStatus(Session session, String status) {
        return applicationRepository.findBySessionAndStatus(session, status);
    }

    public List<Application> findBySessionAndFacultyAndStatus(Session session, Faculty faculty, String status) {
        return applicationRepository.findBySessionAndFacultyAndStatus(session, faculty, status);
    }

    public List<Application> findWaitingList(Session session, Faculty faculty) {
        return applicationRepository.findWaitingListBySessionAndFaculty(session, faculty);
    }

    // depunere cerere noua
    @Transactional
    public Application submit(Application application, Long userId) {

        boolean exists = applicationRepository.existsByUserIdAndSessionIdAndEnabled(
                userId,
                application.getSession().getId(),
                1);

        if (exists) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Utilizatorul are deja o aplicatie pentru sesiunea curenta");
        }

        Date now = new Date();

        application.setStatus(ApplicationStatus.PENDING.name());
        application.setDate(now);
        application.setEnabled(1);
        application.setUserId(userId);
        application.setCreatedBy(userId);
        application.setCreatedAt(now);

        return applicationRepository.save(application);
    }

    public Application save(Application application, Long userId) {
        Date date = new Date();
        if (application.getId() == null) {
            application.setCreatedBy(userId);
            application.setCreatedAt(date);
            application.setEnabled(1);
        } else {
            Application existing = applicationRepository.findById(application.getId()).orElse(null);
            if (existing != null) {
                application.setCreatedBy(existing.getCreatedBy());
                application.setCreatedAt(existing.getCreatedAt());
                application.setEnabled(existing.getEnabled());
            }
        }
        application.setModifiedBy(userId);
        application.setModifiedAt(date);
        return applicationRepository.save(application);
    }

    // confirmare cu diploma (buget)
    @Transactional
    public boolean confirmWithDiploma(Long userId, Integer applicationId) {
        Application app = applicationRepository.findById(applicationId).orElse(null);
        if (app == null || !app.getUserId().equals(userId)) return false;
        if (!ApplicationStatus.APPROVED.name().equals(app.getStatus())) return false;
        app.setStatus(ApplicationStatus.CONFIRMED.name());
        app.setConfirmationType("DIPLOMA");
        app.setConfirmationDate(new Date());
        applicationRepository.save(app);
        return true;
    }

    // confirmare cu plata (taxa) - student plateste X procent din taxa
    @Transactional
    public boolean confirmWithPayment(Long userId, Integer applicationId, Float paymentPercentage) {
        Application app = applicationRepository.findById(applicationId).orElse(null);
        if (app == null || !app.getUserId().equals(userId)) return false;
        if (!ApplicationStatus.APPROVED.name().equals(app.getStatus())) return false;
        app.setStatus(ApplicationStatus.CONFIRMED.name());
        app.setConfirmationType("PAYMENT");
        app.setPaymentPercentage(paymentPercentage);
        app.setConfirmationDate(new Date());
        applicationRepository.save(app);
        return true;
    }

    // job nocturn: expira confirmari depasite + promoveaza din lista de asteptare
    @Scheduled(cron = "0 0 2 * * *")
    @Transactional
    public void processExpiredConfirmations() {
        List<Application> expired = applicationRepository.findExpiredApplications(new Date());
        for (Application app : expired) {
            app.setStatus(ApplicationStatus.EXPIRED.name());
            applicationRepository.save(app);
            promoteFirstFromWaitingList(app.getSession(), app.getFaculty(), app.getFormFunding());
        }
    }

    @Transactional
    public void promoteFirstFromWaitingList(Session session, Faculty faculty, Integer formFunding) {
        applicationRepository.findWaitingListBySessionAndFaculty(session, faculty).stream()
                .filter(a -> a.getFormFunding().equals(formFunding))
                .findFirst()
                .ifPresent(next -> {
                    next.setStatus(ApplicationStatus.APPROVED.name());
                    next.setWaitingListPosition(null);
                    next.setConfirmationDeadline(calcDeadline(session));
                    applicationRepository.save(next);
                });
    }

    // expune cererile PENDING pentru algoritmul Person A (via Feign)
    public List<Application> findPendingBySession(Session session) {
        return applicationRepository.findBySessionAndStatus(session, ApplicationStatus.PENDING.name());
    }

    @Transactional
    public void bulkUpdateStatus(BulkStatusRequest bulkStatusRequest) {
        for (ApplicationStatusUpdate statusUpdate : bulkStatusRequest.getUpdates()) {
            Application application =
                    applicationRepository.getReferenceById(statusUpdate.getApplicationId().intValue());
            log.info("Updating status of applicationId={} with applicationStatus={}",
                    application.getId(), statusUpdate.getStatus().toString());
            application.setStatus(statusUpdate.getStatus().toString());
        }
    }

    public void delete(Application application) {
        applicationRepository.delete(application);
    }

    @SuppressWarnings("unchecked")
    public List<Object[]> countPerFaculty() {
        return entityManager.createNativeQuery(
                        "select count(a.id) as total, p.name as profile, u.name as university " +
                                "from profile p " +
                                "inner join university u on p.university_id = u.id " +
                                "left join addmissionapplic a on a.profile_id = p.id and a.status = 'CONFIRMED' " +
                                "GROUP BY p.name ORDER BY u.name, p.name, total")
                .getResultList();
    }

    private Date calcDeadline(Session session) {
        Calendar cal = Calendar.getInstance();
        int days = session.getConfirmationDeadlineDays() != null ? session.getConfirmationDeadlineDays() : 7;
        cal.add(Calendar.DAY_OF_MONTH, days);
        return cal.getTime();
    }
}
