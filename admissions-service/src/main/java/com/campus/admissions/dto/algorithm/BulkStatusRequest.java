package com.campus.admissions.dto.algorithm;

import lombok.Data;

import java.util.List;

@Data
public class BulkStatusRequest {
    private List<ApplicationStatusUpdate> updates;
}
