package com.campus.admissions.service;

import com.campus.admissions.dto.algorithm.ApplicationRankDto;
import com.campus.admissions.dto.algorithm.ApplicationStatusUpdate;
import com.campus.admissions.dto.algorithm.BulkStatusRequest;
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

    @Mock
    private AverageCompetitionService averageCompetitionService;

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

    @Test
    void getApplicationRank_shouldReturnDtosWithAverageCompetitionData() {
        Faculty faculty = Faculty.builder()
                .id(10)
                .build();

        Application application = Application.builder()
                .id(1)
                .userId(100L)
                .faculty(faculty)
                .formFunding(1)
                .build();

        AverageCompetition averageCompetition = AverageCompetition.builder()
                .averageBac(9.75f)
                .markDif1(9.50f)
                .markDif2(9.80f)
                .markDif3(10.0f)
                .build();

        when(averageCompetitionService.getByUserId(100L))
                .thenReturn(Optional.of(averageCompetition));

        List<ApplicationRankDto> result =
                applicationService.getApplicationsRank(List.of(application));

        assertEquals(1, result.size());

        ApplicationRankDto dto = result.getFirst();

        assertEquals(1L, dto.getApplicationId());
        assertEquals(100L, dto.getUserId());
        assertEquals(10, dto.getFacultyId());
        assertEquals(1, dto.getFormFunding());

        assertEquals(9.75f, dto.getAverageBac());
        assertEquals(9.50f, dto.getMarkDif1());
        assertEquals(9.80f, dto.getMarkDif2());
        assertEquals(10.0f, dto.getMarkDif3());

        verify(averageCompetitionService).getByUserId(100L);
    }

    @Test
    void getApplicationRank_shouldReturnDtosWithoutAverageCompetitionData() {
        Faculty faculty = Faculty.builder()
                .id(20)
                .build();

        Application application = Application.builder()
                .id(2)
                .userId(200L)
                .faculty(faculty)
                .formFunding(2)
                .build();

        when(averageCompetitionService.getByUserId(200L))
                .thenReturn(Optional.empty());

        List<ApplicationRankDto> result =
                applicationService.getApplicationsRank(List.of(application));

        assertEquals(1, result.size());

        ApplicationRankDto dto = result.getFirst();

        assertEquals(2L, dto.getApplicationId());
        assertEquals(200L, dto.getUserId());
        assertEquals(20, dto.getFacultyId());
        assertEquals(2, dto.getFormFunding());

        assertNull(dto.getAverageBac());
        assertNull(dto.getMarkDif1());
        assertNull(dto.getMarkDif2());
        assertNull(dto.getMarkDif3());

        verify(averageCompetitionService).getByUserId(200L);
    }

    @Test
    void findBySessionIdAndStatus_shouldReturnApplications() {
        Integer sessionId = 1;
        String status = "APPROVED";

        List<Application> expectedApplications = List.of(
                Application.builder().id(1).build(),
                Application.builder().id(2).build()
        );

        when(applicationRepository.findBySessionIdAndStatus(sessionId, status))
                .thenReturn(expectedApplications);

        List<Application> result =
                applicationService.findBySessionIdAndStatus(sessionId, status);

        assertEquals(2, result.size());
        assertEquals(expectedApplications, result);

        verify(applicationRepository)
                .findBySessionIdAndStatus(sessionId, status);
    }

    @Test
    void findBySessionIdAndStatus_shouldReturnEmptyList() {
        Integer sessionId = 99;
        String status = "INVALID";

        when(applicationRepository.findBySessionIdAndStatus(sessionId, status))
                .thenReturn(Collections.emptyList());

        List<Application> result =
                applicationService.findBySessionIdAndStatus(sessionId, status);

        assertTrue(result.isEmpty());

        verify(applicationRepository)
                .findBySessionIdAndStatus(sessionId, status);
    }

    @Test
    void bulkUpdateStatus_shouldUpdateApplicationStatuses() {

        // Arrange
        Application application1 = new Application();
        application1.setId(1);
        application1.setStatus("PENDING");

        Application application2 = new Application();
        application2.setId(2);
        application2.setStatus("PENDING");

        ApplicationStatusUpdate update1 = new ApplicationStatusUpdate();
        update1.setApplicationId(1L);
        update1.setStatus(ApplicationStatus.APPROVED);

        ApplicationStatusUpdate update2 = new ApplicationStatusUpdate();
        update2.setApplicationId(2L);
        update2.setStatus(ApplicationStatus.REJECTED);

        BulkStatusRequest request = BulkStatusRequest.builder()
                .updates(List.of(update1, update2)).build();

        when(applicationRepository.getReferenceById(1))
                .thenReturn(application1);

        when(applicationRepository.getReferenceById(2))
                .thenReturn(application2);

        // Act
        applicationService.bulkUpdateStatus(request);

        // Assert
        assertEquals(ApplicationStatus.APPROVED.toString(), application1.getStatus());
        assertEquals(ApplicationStatus.REJECTED.toString(), application2.getStatus());

        verify(applicationRepository).getReferenceById(1);
        verify(applicationRepository).getReferenceById(2);
    }
}