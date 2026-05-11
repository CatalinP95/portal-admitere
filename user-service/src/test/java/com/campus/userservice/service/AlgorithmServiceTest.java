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

    private ApplicationRankDto candidat(Long appId, Long userId, Integer facultyId, float medie) {
        ApplicationRankDto dto = new ApplicationRankDto();
        dto.setApplicationId(appId);
        dto.setUserId(userId);
        dto.setFacultyId(facultyId);
        dto.setAverageBac(medie);
        dto.setFormFunding(1);
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
    void ranking_sortezaDescrescatorDupaMedie() {
        // 3 candidați, 1 loc buget, 1 loc taxă
        when(admissionsClient.getPendingApplications(1))
                .thenReturn(List.of(
                        candidat(1L, 10L, 1, 7.20f),
                        candidat(2L, 20L, 1, 9.80f),
                        candidat(3L, 30L, 1, 8.50f)
                ));
        when(admissionsClient.getFacultySpots(1)).thenReturn(spots(1, 1, 1));
        doNothing().when(admissionsClient).updateStatuses(any());

        AlgorithmResultDto result = algorithmService.runRanking(1);

        assertEquals(3, result.getTotalProcessed());
        assertEquals(1, result.getApprovedBuget());
        assertEquals(1, result.getApprovedTaxa());
        assertEquals(1, result.getWaitingList());

        // verificam ca statusurile trimise sunt corecte
        ArgumentCaptor<BulkStatusRequest> captor = ArgumentCaptor.forClass(BulkStatusRequest.class);
        verify(admissionsClient).updateStatuses(captor.capture());

        List<ApplicationStatusUpdate> updates = captor.getValue().getUpdates();
        // candidatul cu 9.80 (id=2) → APPROVED primul
        assertEquals(2L, updates.get(0).getApplicationId());
        assertEquals("APPROVED", updates.get(0).getStatus());
        // candidatul cu 8.50 (id=3) → APPROVED al doilea
        assertEquals(3L, updates.get(1).getApplicationId());
        assertEquals("APPROVED", updates.get(1).getStatus());
        // candidatul cu 7.20 (id=1) → WAITING_LIST
        assertEquals(1L, updates.get(2).getApplicationId());
        assertEquals("WAITING_LIST", updates.get(2).getStatus());
    }

    @Test
    void ranking_toiiInWaitingListCandLocuriZero() {
        when(admissionsClient.getPendingApplications(1))
                .thenReturn(List.of(
                        candidat(1L, 10L, 1, 8.0f),
                        candidat(2L, 20L, 1, 9.0f)
                ));
        when(admissionsClient.getFacultySpots(1)).thenReturn(spots(1, 0, 0));
        doNothing().when(admissionsClient).updateStatuses(any());

        AlgorithmResultDto result = algorithmService.runRanking(1);

        assertEquals(2, result.getTotalProcessed());
        assertEquals(0, result.getApprovedBuget());
        assertEquals(0, result.getApprovedTaxa());
        assertEquals(2, result.getWaitingList());
    }

    @Test
    void ranking_douaFacultatiIndependent() {
        when(admissionsClient.getPendingApplications(1))
                .thenReturn(List.of(
                        candidat(1L, 10L, 1, 9.0f),
                        candidat(2L, 20L, 1, 7.0f),
                        candidat(3L, 30L, 2, 8.5f),
                        candidat(4L, 40L, 2, 6.0f)
                ));
        when(admissionsClient.getFacultySpots(1)).thenReturn(spots(1, 1, 0));
        when(admissionsClient.getFacultySpots(2)).thenReturn(spots(2, 1, 0));
        doNothing().when(admissionsClient).updateStatuses(any());

        AlgorithmResultDto result = algorithmService.runRanking(1);

        assertEquals(4, result.getTotalProcessed());
        assertEquals(2, result.getApprovedBuget()); // cate 1 de la fiecare facultate
        assertEquals(0, result.getApprovedTaxa());
        assertEquals(2, result.getWaitingList());
    }

    @Test
    void ranking_listGoala_returneazaZero() {
        when(admissionsClient.getPendingApplications(1)).thenReturn(List.of());

        AlgorithmResultDto result = algorithmService.runRanking(1);

        assertEquals(0, result.getTotalProcessed());
        assertEquals(0, result.getApprovedBuget());
        assertEquals(0, result.getWaitingList());
        verify(admissionsClient, never()).getFacultySpots(anyInt());
        verify(admissionsClient, never()).updateStatuses(any());
    }

    @Test
    void ranking_totiIncapInLocuri_nimeniWaiting() {
        when(admissionsClient.getPendingApplications(1))
                .thenReturn(List.of(
                        candidat(1L, 10L, 1, 9.5f),
                        candidat(2L, 20L, 1, 8.0f)
                ));
        when(admissionsClient.getFacultySpots(1)).thenReturn(spots(1, 2, 2));
        doNothing().when(admissionsClient).updateStatuses(any());

        AlgorithmResultDto result = algorithmService.runRanking(1);

        assertEquals(2, result.getTotalProcessed());
        assertEquals(2, result.getApprovedBuget());
        assertEquals(0, result.getApprovedTaxa());
        assertEquals(0, result.getWaitingList());
    }
}
