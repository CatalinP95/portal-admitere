package com.campus.admissions.dto.algorithm;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class FacultySpotsDto {
    private Integer facultyId;
    private Integer nrBuget;
    private Integer nrTaxa;
}