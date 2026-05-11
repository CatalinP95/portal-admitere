package com.campus.userservice.service;

import com.campus.userservice.client.AdmissionsClient;
import com.campus.userservice.dto.algorithm.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AlgorithmServiceTest {

    @Mock private AdmissionsClient admissionsClient;
    @InjectMocks private AlgorithmService algorithmService;

    private ApplicationRankDto candidat(Long appId, Long userId, Integer facultyId,
                                        float bac, float d1, float d2, float d3,
                                        int formFunding) {
        ApplicationRankDto dto = new ApplicationRankDto();
        dto.setApplicationId(appId);
        dto.setUserId(userId);
        dto.setFacultyId(facultyId);
        dto.setAverageBac(bac);
        dto.setMarkDif1(d1);
        dto.setMarkDif2(d2);
        dto.setMarkDif3(d3);
        dto.setFormFunding(formFunding);
        return dto;
    }

    private FacultySpotsDto spots(Integer facultyId, int buget, int taxa) {
        FacultySpotsDto dto = new FacultySpotsDto();
        dto.setFacultyId(facultyId);
        dto.setNrBuget(buget);
        dto.setNrTaxa(taxa);
        return dto;
    }

    @Test
    void scorCompus_calculatCorect() {
        // score = 0.6*9.0 + 0.2*8.0 + 0.1*7.0 + 0.1*6.0 = 5.4 + 1.6 + 0.7 + 0.6 = 8.3
        ApplicationRankDto app = candidat(1L, 1L, 1, 9.0f, 8.0f, 7.0f, 6.0f, 1);
        double score = algorithmService.computeScore(app);
        assertEquals(8.3, score, 0.001);
    }

    @Test
    void ranking_sortezaDupaScorCompus_nuDoarBac() {
        // candidatul 1: bac=9.0, d1=5.0 → score=0.6*9+0.2*5+0.1*0+0.1*0 = 5.4+1.0 = 6.4
        // candidatul 2: bac=7.0, d1=9.0 → score=0.6*7+0.2*9+0.1*0+0.1*0 = 4.2+1.8 = 6.0
        // candidatul 3: bac=8.0, d1=8.0 → score=0.6*8+0.2*8+0.1*0+0.1*0 = 4.8+1.6 = 6.4 (egal cu 1)
        when(admissionsClient.getPendingApplications(1)).thenReturn(List.of(
                candidat(1L, 10L, 1, 9.0f, 5.0f, 0f, 0f, 1),
                candidat(2L, 20L, 1, 7.0f, 9.0f, 0f, 0f, 1),
                candidat(3L, 30L, 1, 8.0f, 8.0f, 0f, 0f, 1)
        ));
        when(admissionsClient.getFacultySpots(1)).thenReturn(spots(1, 1, 0));
        doNothing().when(admissionsClient).updateStatuses(any());

        AlgorithmResultDto result = algorithmService.runRanking(1);

        assertEquals(3, result.getTotalProcessed());
        assertEquals(1, result.getApprovedBuget());
        assertEquals(2, result.getWaitingList());

        ArgumentCaptor<BulkStatusRequest> captor = ArgumentCaptor.forClass(BulkStatusRequest.class);
        verify(admissionsClient).updateStatuses(captor.capture());
        List<ApplicationStatusUpdate> updates = captor.getValue().getUpdates();

        // candidatul 2 (score 6.0) trebuie sa fie WAITING_LIST
        ApplicationStatusUpdate updateCand2 = updates.stream()
                .filter(u -> u.getApplicationId().equals(2L)).findFirst().orElseThrow();
        assertEquals("WAITING_LIST", updateCand2.getStatus());
    }

    @Test
    void ranking_bugetisteOcupaLocuriBuget_taxistiiPeLocuriTaxa() {
        when(admissionsClient.getPendingApplications(1)).thenReturn(List.of(
                candidat(1L, 10L, 1, 9.5f, 9.0f, 8.0f, 8.0f, 1), // bugetist
                candidat(2L, 20L, 1, 8.0f, 7.0f, 7.0f, 7.0f, 1), // bugetist
                candidat(3L, 30L, 1, 9.0f, 8.5f, 8.0f, 8.0f, 2), // taxist
                candidat(4L, 40L, 1, 7.0f, 6.0f, 6.0f, 6.0f, 2)  // taxist
        ));
        when(admissionsClient.getFacultySpots(1)).thenReturn(spots(1, 2, 1));
        doNothing().when(admissionsClient).updateStatuses(any());

        AlgorithmResultDto result = algorithmService.runRanking(1);

        assertEquals(4, result.getTotalProcessed());
        assertEquals(2, result.getApprovedBuget()); // cei 2 bugetisti
        assertEquals(1, result.getApprovedTaxa());  // primul taxist
        assertEquals(1, result.getWaitingList());   // al doilea taxist
    }

    @Test
    void ranking_locuriRamaseBuget_completateDeTaxisti() {
        // 1 loc buget, 0 bugetisti → taxistul cu scorul cel mai mare ia locul buget
        when(admissionsClient.getPendingApplications(1)).thenReturn(List.of(
                candidat(1L, 10L, 1, 9.0f, 9.0f, 9.0f, 9.0f, 2), // taxist
                candidat(2L, 20L, 1, 7.0f, 7.0f, 7.0f, 7.0f, 2)  // taxist
        ));
        when(admissionsClient.getFacultySpots(1)).thenReturn(spots(1, 1, 1));
        doNothing().when(admissionsClient).updateStatuses(any());

        AlgorithmResultDto result = algorithmService.runRanking(1);

        assertEquals(2, result.getTotalProcessed());
        assertEquals(1, result.getApprovedBuget()); // taxistul a luat locul buget ramas
        assertEquals(1, result.getApprovedTaxa());
        assertEquals(0, result.getWaitingList());
    }

    @Test
    void ranking_raportPerFacultateCorect() {
        when(admissionsClient.getPendingApplications(1)).thenReturn(List.of(
                candidat(1L, 10L, 1, 9.0f, 9.0f, 9.0f, 9.0f, 1),
                candidat(2L, 20L, 1, 6.0f, 6.0f, 6.0f, 6.0f, 1),
                candidat(3L, 30L, 2, 8.0f, 8.0f, 8.0f, 8.0f, 2)
        ));
        when(admissionsClient.getFacultySpots(1)).thenReturn(spots(1, 1, 1));
        when(admissionsClient.getFacultySpots(2)).thenReturn(spots(2, 1, 0));
        doNothing().when(admissionsClient).updateStatuses(any());

        AlgorithmResultDto result = algorithmService.runRanking(1);

        assertFalse(result.getFaculties().isEmpty());
        FacultyRankingResult fac1 = result.getFaculties().stream()
                .filter(f -> f.getFacultyId().equals(1)).findFirst().orElseThrow();
        assertEquals(2, fac1.getTotalCandidates());
        assertEquals(1, fac1.getApprovedBuget());
        assertEquals(1, fac1.getWaitingList());
        assertTrue(fac1.getMinimumScore() > 0);
    }

    @Test
    void ranking_listGoala_returneazaZero() {
        when(admissionsClient.getPendingApplications(1)).thenReturn(List.of());

        AlgorithmResultDto result = algorithmService.runRanking(1);

        assertEquals(0, result.getTotalProcessed());
        assertEquals(0, result.getApprovedBuget());
        assertEquals(0, result.getWaitingList());
        assertTrue(result.getFaculties().isEmpty());
        verify(admissionsClient, never()).getFacultySpots(anyInt());
        verify(admissionsClient, never()).updateStatuses(any());
    }

    @Test
    void ranking_toiiInWaitingListCandLocuriZero() {
        when(admissionsClient.getPendingApplications(1)).thenReturn(List.of(
                candidat(1L, 10L, 1, 8.0f, 8.0f, 8.0f, 8.0f, 1),
                candidat(2L, 20L, 1, 9.0f, 9.0f, 9.0f, 9.0f, 2)
        ));
        when(admissionsClient.getFacultySpots(1)).thenReturn(spots(1, 0, 0));
        doNothing().when(admissionsClient).updateStatuses(any());

        AlgorithmResultDto result = algorithmService.runRanking(1);

        assertEquals(2, result.getTotalProcessed());
        assertEquals(0, result.getApprovedBuget());
        assertEquals(0, result.getApprovedTaxa());
        assertEquals(2, result.getWaitingList());
    }
}
