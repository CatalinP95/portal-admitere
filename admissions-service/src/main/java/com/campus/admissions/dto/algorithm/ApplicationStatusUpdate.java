package com.campus.admissions.dto.algorithm;

import lombok.Data;

@Data
public class ApplicationStatusUpdate {
    private Long applicationId;
    private String status;
}
