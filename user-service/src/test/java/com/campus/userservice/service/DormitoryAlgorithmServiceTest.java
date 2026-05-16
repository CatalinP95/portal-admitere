package com.campus.userservice.service;

import com.campus.userservice.client.AdmissionsClient;
import com.campus.userservice.client.DormitoryClient;
import com.campus.userservice.dto.dormitory.*;
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
class DormitoryAlgorithmServiceTest {

    @Mock private DormitoryClient dormitoryClient;
    @Mock private AdmissionsClient admissionsClient;
    @InjectMocks private DormitoryAlgorithmService service;

    private BlockRequestRankDto cerere(Integer id, Long userId, String sex, Double distKm) {
        BlockRequestRankDto dto = new BlockRequestRankDto();
        dto.setBlockRequestId(id);
        dto.setUserId(userId);
        dto.setSex(sex);
        dto.setDistanceKm(distKm);
        return dto;
    }

    private StudentAllocationDataDto dateStudent(Float bac, String conditie) {
        StudentAllocationDataDto dto = new StudentAllocationDataDto();
        dto.setAverageBac(bac);
        dto.setMedicalCondition(conditie);
        return dto;
    }

    // --- computeScore ---

    @Test
    void computeScore_formulaCorecta() {
        // 0.9 * (9.0/10) + 0.1 * min(1, 250/500) = 0.81 + 0.05 = 0.86
        double score = service.computeScore(9.0f, 250.0);
        assertEquals(0.86, score, 0.001);
    }

    @Test
    void computeScore_distantaMaximaNormalizataLa1() {
        // distanta 1000 km > 500 → min(1, 1000/500) = 1.0
        // 0.9 * (10/10) + 0.1 * 1.0 = 0.9 + 0.1 = 1.0
        double score = service.computeScore(10.0f, 1000.0);
        assertEquals(1.0, score, 0.001);
    }

    @Test
    void computeScore_valoriNull_tratateCaZero() {
        double score = service.computeScore(null, null);
        assertEquals(0.0, score, 0.001);
    }

    // --- getRoomType ---

    @Test
    void getRoomType_barbatSanatos_returneaza1() {
        assertEquals(1, service.getRoomType("M", "SANATOS"));
    }

    @Test
    void getRoomType_femeieSchiopata_returneaza4() {
        assertEquals(4, service.getRoomType("F", "Handicap fizic"));
    }

    @Test
    void getRoomType_barbatConditieMedicala_returneaza3() {
        assertEquals(3, service.getRoomType("M", "Handicap auditiv"));
    }

    @Test
    void getRoomType_femeieSchiopata_sanitosNuEsteConditieMedicala() {
        // "SANATOS" trebuie tratat ca fara conditie medicala
        assertEquals(2, service.getRoomType("F", "SANATOS"));
    }

    @Test
    void getRoomType_conditieBlank_tratataSanatos() {
        assertEquals(1, service.getRoomType("M", ""));
    }

    // --- runAllocation ---

    @Test
    void runAllocation_listaGoala_returneazaZero() {
        when(dormitoryClient.getPendingRequests(1)).thenReturn(List.of());

        DormitoryAllocationResultDto result = service.runAllocation(1);

        assertEquals(0, result.getTotalProcessed());
        assertEquals(0, result.getAllocated());
        verify(admissionsClient, never()).getStudentAllocationData(any());
        verify(dormitoryClient, never()).bulkAllocate(any());
    }

    @Test
    void runAllocation_sorteazaDescrescatorDupaScor() {
        when(dormitoryClient.getPendingRequests(1)).thenReturn(List.of(
                cerere(10, 100L, "M", 100.0),  // scor mic
                cerere(20, 200L, "F", 400.0),  // scor mare
                cerere(30, 300L, "M", 250.0)   // scor mediu
        ));
        // bac 6 → scor = 0.9*0.6 + 0.1*0.2 = 0.54 + 0.02 = 0.56
        when(admissionsClient.getStudentAllocationData(100L)).thenReturn(dateStudent(6.0f, "SANATOS"));
        // bac 9 → scor = 0.9*0.9 + 0.1*0.8 = 0.81 + 0.08 = 0.89
        when(admissionsClient.getStudentAllocationData(200L)).thenReturn(dateStudent(9.0f, "SANATOS"));
        // bac 8 → scor = 0.9*0.8 + 0.1*0.5 = 0.72 + 0.05 = 0.77
        when(admissionsClient.getStudentAllocationData(300L)).thenReturn(dateStudent(8.0f, "SANATOS"));
        doNothing().when(dormitoryClient).bulkAllocate(any());

        service.runAllocation(1);

        ArgumentCaptor<BulkBedAllocationRequest> captor =
                ArgumentCaptor.forClass(BulkBedAllocationRequest.class);
        verify(dormitoryClient).bulkAllocate(captor.capture());

        List<BedAllocationRequest> alocari = captor.getValue().getAllocations();
        assertEquals(3, alocari.size());
        // primul trebuie sa fie studentul cu scorul cel mai mare (userId 200, blockRequestId 20)
        assertEquals(20, alocari.get(0).getBlockRequestId());
        // al doilea: scor mediu (userId 300, blockRequestId 30)
        assertEquals(30, alocari.get(1).getBlockRequestId());
        // ultimul: scor mic (userId 100, blockRequestId 10)
        assertEquals(10, alocari.get(2).getBlockRequestId());
    }

    @Test
    void runAllocation_tipCameraCorectInListaTrimiса() {
        when(dormitoryClient.getPendingRequests(1)).thenReturn(List.of(
                cerere(1, 1L, "M", 100.0),
                cerere(2, 2L, "F", 100.0)
        ));
        when(admissionsClient.getStudentAllocationData(1L)).thenReturn(dateStudent(8.0f, "SANATOS"));
        when(admissionsClient.getStudentAllocationData(2L)).thenReturn(dateStudent(9.0f, "Handicap fizic"));
        doNothing().when(dormitoryClient).bulkAllocate(any());

        service.runAllocation(1);

        ArgumentCaptor<BulkBedAllocationRequest> captor =
                ArgumentCaptor.forClass(BulkBedAllocationRequest.class);
        verify(dormitoryClient).bulkAllocate(captor.capture());

        List<BedAllocationRequest> alocari = captor.getValue().getAllocations();
        // cererea 2 (F + Handicap fizic → tip 4) are scor mai mare, e prima
        BedAllocationRequest prima = alocari.get(0);
        assertEquals(2, prima.getBlockRequestId());
        assertEquals(4, prima.getRoomType()); // F + medical = tip 4

        BedAllocationRequest doua = alocari.get(1);
        assertEquals(1, doua.getBlockRequestId());
        assertEquals(1, doua.getRoomType()); // M + sanatos = tip 1
    }

    @Test
    void runAllocation_eroareLaAdmissions_studentIgnorat() {
        when(dormitoryClient.getPendingRequests(1)).thenReturn(List.of(
                cerere(1, 1L, "M", 200.0),
                cerere(2, 2L, "F", 300.0)
        ));
        when(admissionsClient.getStudentAllocationData(1L)).thenReturn(dateStudent(9.0f, "SANATOS"));
        // studentul 2 arunca exceptie (admissions-service down)
        when(admissionsClient.getStudentAllocationData(2L))
                .thenThrow(new RuntimeException("admissions-service indisponibil"));
        doNothing().when(dormitoryClient).bulkAllocate(any());

        DormitoryAllocationResultDto result = service.runAllocation(1);

        // 2 cereri preluate, dar doar 1 a ajuns in lista de alocare
        assertEquals(2, result.getTotalProcessed());
        assertEquals(1, result.getAllocated());

        ArgumentCaptor<BulkBedAllocationRequest> captor =
                ArgumentCaptor.forClass(BulkBedAllocationRequest.class);
        verify(dormitoryClient).bulkAllocate(captor.capture());
        assertEquals(1, captor.getValue().getAllocations().size());
        assertEquals(1, captor.getValue().getAllocations().get(0).getBlockRequestId());
    }

    @Test
    void runAllocation_returneazaStatisticiCorecte() {
        when(dormitoryClient.getPendingRequests(5)).thenReturn(List.of(
                cerere(1, 1L, "M", 100.0),
                cerere(2, 2L, "F", 200.0),
                cerere(3, 3L, "M", 300.0)
        ));
        when(admissionsClient.getStudentAllocationData(1L)).thenReturn(dateStudent(7.0f, "SANATOS"));
        when(admissionsClient.getStudentAllocationData(2L)).thenReturn(dateStudent(8.0f, "SANATOS"));
        when(admissionsClient.getStudentAllocationData(3L)).thenReturn(dateStudent(9.0f, "SANATOS"));
        doNothing().when(dormitoryClient).bulkAllocate(any());

        DormitoryAllocationResultDto result = service.runAllocation(5);

        assertEquals(5, result.getSessionId());
        assertEquals(3, result.getTotalProcessed());
        assertEquals(3, result.getAllocated());
        assertEquals(0, result.getRejected());
    }
}
