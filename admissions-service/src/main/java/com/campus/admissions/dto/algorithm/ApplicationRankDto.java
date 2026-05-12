package com.campus.admissions.dto.algorithm;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ApplicationRankDto {
    private Long applicationId;
    private Long userId;
    private Integer facultyId;
    private Float averageBac;
    private Integer formFunding;
    private Float markDif1;
    private Float markDif2;
    private Float markDif3;
}