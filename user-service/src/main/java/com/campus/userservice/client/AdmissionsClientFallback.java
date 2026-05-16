package com.campus.userservice.client;

import com.campus.userservice.dto.algorithm.ApplicationRankDto;
import com.campus.userservice.dto.algorithm.BulkStatusRequest;
import com.campus.userservice.dto.algorithm.FacultySpotsDto;
import com.campus.userservice.dto.dormitory.StudentAllocationDataDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class AdmissionsClientFallback implements FallbackFactory<AdmissionsClient> {

    private static final Logger log = LoggerFactory.getLogger(AdmissionsClientFallback.class);

    @Override
    public AdmissionsClient create(Throwable cause) {
        log.error("admissions-service unavailable: {}", cause.getMessage());
        return new AdmissionsClient() {
            @Override
            public List<ApplicationRankDto> getPendingApplications(Integer sessionId) {
                throw new RuntimeException("admissions-service indisponibil — nu se pot prelua candidatii");
            }

            @Override
            public FacultySpotsDto getFacultySpots(Integer facultyId) {
                throw new RuntimeException("admissions-service indisponibil — nu se pot prelua locurile");
            }

            @Override
            public void updateStatuses(BulkStatusRequest request) {
                throw new RuntimeException("admissions-service indisponibil — statusurile nu au fost actualizate");
            }

            @Override
            public StudentAllocationDataDto getStudentAllocationData(Long userId) {
                throw new RuntimeException("admissions-service indisponibil — datele studentului nu au putut fi preluate");
            }
        };
    }
}
