package com.campus.admissions.service;

import com.campus.admissions.model.*;
import com.campus.admissions.repository.ApplicationRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.data.domain.*;
import jakarta.persistence.Query;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class ApplicationServiceTest {

    @Mock
    private ApplicationRepository applicationRepository;

    @Mock
    private EntityManager entityManager;

    @InjectMocks
    private ApplicationService applicationService;

    private Application application;
    private Session session;
    private Faculty faculty;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(
                applicationService,
                "entityManager",
                entityManager
        );

        session = new Session();
        session.setId(1);
        session.setConfirmationDeadlineDays(5);

        faculty = new Faculty();
        faculty.setId(1);

        application = new Application();
        application.setId(1);
        application.setSession(session);
        application.setFaculty(faculty);
        application.setFormFunding(1);
        application.setUserId(100L);
    }

    @Test
    void findAll_shouldReturnPage() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Application> page = new PageImpl<>(List.of(application));

        when(applicationRepository.findAll(pageable)).thenReturn(page);

        assertEquals(1, applicationService.findAll(pageable).getTotalElements());
    }

    @Test
    void findById_shouldReturnEntity() {
        when(applicationRepository.findById(1)).thenReturn(Optional.of(application));

        assertNotNull(applicationService.findById(1));
    }

    @Test
    void findByUserId_shouldReturnList() {
        when(applicationRepository.findByUserId(100L)).thenReturn(List.of(application));

        assertEquals(1, applicationService.findByUserId(100L).size());
    }

    @Test
    void findBySessionAndStatus_shouldReturnList() {
        when(applicationRepository.findBySessionAndStatus(session, "PENDING"))
                .thenReturn(List.of(application));

        assertEquals(1, applicationService.findBySessionAndStatus(session, "PENDING").size());
    }

    @Test
    void findBySessionFacultyStatus_shouldReturnList() {
        when(applicationRepository.findBySessionAndFacultyAndStatus(session, faculty, "PENDING"))
                .thenReturn(List.of(application));

        assertEquals(1,
                applicationService.findBySessionAndFacultyAndStatus(session, faculty, "PENDING").size());
    }

    @Test
    void findWaitingList_shouldReturnList() {
        when(applicationRepository.findWaitingListBySessionAndFaculty(session, faculty))
                .thenReturn(List.of(application));

        assertEquals(1, applicationService.findWaitingList(session, faculty).size());
    }

    @Test
    void submit_shouldThrowConflict_whenDuplicateExists() {
        when(applicationRepository.existsByUserIdAndSessionIdAndEnabled(100L, 1, 1))
                .thenReturn(true);

        assertThrows(ResponseStatusException.class,
                () -> applicationService.submit(application, 100L));
    }

    @Test
    void submit_shouldSave_whenNoDuplicate() {
        when(applicationRepository.existsByUserIdAndSessionIdAndEnabled(100L, 1, 1))
                .thenReturn(false);
        when(applicationRepository.save(any())).thenReturn(application);

        Application result = applicationService.submit(application, 100L);

        assertNotNull(result);
        verify(applicationRepository).save(any(Application.class));
    }

    @Test
    void save_newEntity_shouldSetCreatedFields() {
        Application newApp = new Application();

        when(applicationRepository.save(any())).thenReturn(newApp);

        Application result = applicationService.save(newApp, 1L);

        assertEquals(1L, result.getCreatedBy());
    }

    @Test
    void save_existingEntity_shouldPreserveFields() {
        Application existing = new Application();
        existing.setId(1);
        existing.setCreatedBy(2L);
        existing.setCreatedAt(new Date());
        existing.setEnabled(1);

        when(applicationRepository.findById(1)).thenReturn(Optional.of(existing));
        when(applicationRepository.save(any())).thenReturn(existing);

        Application result = applicationService.save(existing, 1L);

        assertEquals(2L, result.getCreatedBy());
    }

    @Test
    void confirmWithDiploma_shouldReturnFalse_whenNotFound() {
        when(applicationRepository.findById(1)).thenReturn(Optional.empty());

        assertFalse(applicationService.confirmWithDiploma(100L, 1));
    }

    @Test
    void confirmWithDiploma_shouldReturnFalse_whenWrongUser() {
        application.setUserId(999L);
        when(applicationRepository.findById(1)).thenReturn(Optional.of(application));

        assertFalse(applicationService.confirmWithDiploma(100L, 1));
    }

    @Test
    void confirmWithDiploma_shouldConfirm() {
        application.setStatus(ApplicationStatus.APPROVED.name());

        when(applicationRepository.findById(1)).thenReturn(Optional.of(application));
        when(applicationRepository.save(any())).thenReturn(application);

        assertTrue(applicationService.confirmWithDiploma(100L, 1));
    }

    @Test
    void confirmWithPayment_shouldConfirm() {
        application.setStatus(ApplicationStatus.APPROVED.name());

        when(applicationRepository.findById(1)).thenReturn(Optional.of(application));
        when(applicationRepository.save(any())).thenReturn(application);

        assertTrue(applicationService.confirmWithPayment(100L, 1, 50f));
    }

    @Test
    void processExpiredConfirmations_shouldExpireAndPromote() {
        Application expired = new Application();
        expired.setSession(session);
        expired.setFaculty(faculty);
        expired.setFormFunding(1);

        when(applicationRepository.findExpiredApplications(any()))
                .thenReturn(List.of(expired));

        when(applicationRepository.findWaitingListBySessionAndFaculty(session, faculty))
                .thenReturn(List.of(expired));

        when(applicationRepository.save(any())).thenReturn(expired);

        applicationService.processExpiredConfirmations();

        verify(applicationRepository, atLeastOnce()).save(any());
    }
    @Test
    void bulkUpdate_shouldSaveAll() {
        Application a1 = new Application();
        Application a2 = new Application();

        applicationService.bulkUpdateStatus(List.of(a1, a2));

        verify(applicationRepository, times(2)).save(any());
    }

    @Test
    void delete_shouldCallRepository() {
        applicationService.delete(application);

        verify(applicationRepository).delete(application);
    }

    @Test
    void countPerFaculty_shouldReturnResult() {
        when(entityManager.createNativeQuery(anyString()))
                .thenReturn(mock(Query.class));

        Query query = mock(Query.class);
        when(entityManager.createNativeQuery(anyString())).thenReturn(query);
        when(query.getResultList())
                .thenReturn(List.<Object[]>of(
                        new Object[]{1, "CS", "UB"}
                ));
        List<Object[]> result = applicationService.countPerFaculty();

        assertEquals(1, result.size());
    }

    @Test
    void findPending_shouldReturnList() {
        when(applicationRepository.findBySessionAndStatus(session, "PENDING"))
                .thenReturn(List.of(application));

        assertEquals(1, applicationService.findPendingBySession(session).size());
    }
}