package com.campus.userservice.client;

import com.campus.userservice.dto.algorithm.ApplicationRankDto;
import com.campus.userservice.dto.algorithm.BulkStatusRequest;
import com.campus.userservice.dto.algorithm.FacultySpotsDto;
import com.campus.userservice.dto.dormitory.StudentAllocationDataDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "admissions-service", fallbackFactory = AdmissionsClientFallback.class)
public interface AdmissionsClient {

    @GetMapping("/internal/applications/pending/{sessionId}")
    List<ApplicationRankDto> getPendingApplications(@PathVariable("sessionId") Integer sessionId);

    @GetMapping("/internal/faculty/{facultyId}/spots")
    FacultySpotsDto getFacultySpots(@PathVariable("facultyId") Integer facultyId);

    @PutMapping("/internal/applications/bulk-status")
    void updateStatuses(@RequestBody BulkStatusRequest request);

    @GetMapping("/internal/students/{userId}/allocation-data")
    StudentAllocationDataDto getStudentAllocationData(@PathVariable("userId") Long userId);
}
