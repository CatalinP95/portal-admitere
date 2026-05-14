package com.campus.dormitory.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "dormitory_audit_logs")
@Data
@NoArgsConstructor
public class AuditLog {

    @Id
    private String id;

    @Indexed
    private Long userId;

    @Indexed
    private String action;

    private String resource;
    private String resourceId;
    private String details;
    private LocalDateTime timestamp;

    public AuditLog(Long userId, String action, String resource, String resourceId, String details) {
        this.userId = userId;
        this.action = action;
        this.resource = resource;
        this.resourceId = resourceId;
        this.details = details;
        this.timestamp = LocalDateTime.now();
    }
}
