package com.campus.dormitory.messaging;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class DormitoryEvent {

    private String type;
    private Long userId;
    private Integer resourceId;
    private String details;
    private LocalDateTime timestamp;

    public DormitoryEvent(String type, Long userId, Integer resourceId, String details) {
        this.type = type;
        this.userId = userId;
        this.resourceId = resourceId;
        this.details = details;
        this.timestamp = LocalDateTime.now();
    }
}
