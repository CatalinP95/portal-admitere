package com.campus.userservice.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;

import java.time.LocalDateTime;

@Document(collection = "audit_logs")
public class AuditLog {

    @Id
    private String id;

    @Indexed
    private Long userId;

    private String action;
    private String resource;
    private String details;
    private LocalDateTime timestamp;

    public AuditLog() {}

    public AuditLog(Long userId, String action, String resource, String details) {
        this.userId = userId;
        this.action = action;
        this.resource = resource;
        this.details = details;
        this.timestamp = LocalDateTime.now();
    }

    public String getId() { return id; }
    public Long getUserId() { return userId; }
    public String getAction() { return action; }
    public String getResource() { return resource; }
    public String getDetails() { return details; }
    public LocalDateTime getTimestamp() { return timestamp; }
}
