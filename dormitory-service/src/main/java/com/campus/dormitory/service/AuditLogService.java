package com.campus.dormitory.service;

import com.campus.dormitory.model.AuditLog;
import com.campus.dormitory.repository.mongo.AuditLogRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Slf4j
@Service
public class AuditLogService {

    private final ObjectProvider<AuditLogRepository> auditLogRepository;

    public AuditLogService(ObjectProvider<AuditLogRepository> auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    @Async
    public void log(Long userId, String action, String resource, String resourceId, String details) {
        AuditLogRepository repo = auditLogRepository.getIfAvailable();
        if (repo == null) {
            log.debug("AuditLogRepository unavailable (test profile?), skipping audit for {}/{}", action, resource);
            return;
        }
        try {
            repo.save(new AuditLog(userId, action, resource, resourceId, details));
        } catch (Exception e) {
            log.warn("Failed to save audit log for user {}: {}", userId, e.getMessage());
        }
    }

    public List<AuditLog> getLogsForUser(Long userId) {
        AuditLogRepository repo = auditLogRepository.getIfAvailable();
        return repo == null ? Collections.emptyList() : repo.findByUserIdOrderByTimestampDesc(userId);
    }

    public List<AuditLog> getLogsForResource(String resource, String resourceId) {
        AuditLogRepository repo = auditLogRepository.getIfAvailable();
        return repo == null ? Collections.emptyList()
                : repo.findByResourceAndResourceIdOrderByTimestampDesc(resource, resourceId);
    }
}
