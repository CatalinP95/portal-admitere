package com.campus.admissions.dto.algorithm;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class BulkStatusRequest {
    private List<ApplicationStatusUpdate> updates;
}
