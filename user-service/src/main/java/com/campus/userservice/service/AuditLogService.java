package com.campus.userservice.service;

import com.campus.userservice.model.AuditLog;
import com.campus.userservice.repository.AuditLogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuditLogService {

    private static final Logger log = LoggerFactory.getLogger(AuditLogService.class);

    private final AuditLogRepository auditLogRepository;

    public AuditLogService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    @Async
    public void log(Long userId, String action, String resource, String details) {
        try {
            auditLogRepository.save(new AuditLog(userId, action, resource, details));
        } catch (Exception e) {
            log.warn("Failed to save audit log for user {}: {}", userId, e.getMessage());
        }
    }

    public List<AuditLog> getLogsForUser(Long userId) {
        return auditLogRepository.findByUserIdOrderByTimestampDesc(userId);
    }
}
