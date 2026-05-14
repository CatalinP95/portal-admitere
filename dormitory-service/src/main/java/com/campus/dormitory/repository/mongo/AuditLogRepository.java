package com.campus.dormitory.repository.mongo;

import com.campus.dormitory.model.AuditLog;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface AuditLogRepository extends MongoRepository<AuditLog, String> {
    List<AuditLog> findByUserIdOrderByTimestampDesc(Long userId);
    List<AuditLog> findByActionOrderByTimestampDesc(String action);
    List<AuditLog> findByResourceAndResourceIdOrderByTimestampDesc(String resource, String resourceId);
}
