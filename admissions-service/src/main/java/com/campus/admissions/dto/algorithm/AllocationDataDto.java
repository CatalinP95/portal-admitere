package com.campus.admissions.dto.algorithm;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class AllocationDataDto {
    private String sex;
    private Double distanceKm;
    private String medicalCondition;
    private Float averageBac;
}
