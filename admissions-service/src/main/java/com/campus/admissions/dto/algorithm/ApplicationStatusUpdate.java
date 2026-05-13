package com.campus.admissions.dto.algorithm;

import com.campus.admissions.model.ApplicationStatus;
import lombok.Data;

@Data
public class ApplicationStatusUpdate {
    private Long applicationId;
    private ApplicationStatus status;
}
