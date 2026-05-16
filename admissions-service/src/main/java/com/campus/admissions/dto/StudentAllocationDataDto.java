package com.campus.admissions.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StudentAllocationDataDto {
    private Long userId;
    private Float averageBac;
    private String medicalCondition;
}
