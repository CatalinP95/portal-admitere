package com.campus.dormitory.api;

import com.campus.dormitory.model.AuditLog;
import com.campus.dormitory.service.AuditLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/dormitory/audit")
@Tag(name = "Audit Log", description = "View dormitory audit trail (MongoDB)")
public class AuditLogController {

    private final AuditLogService auditLogService;

    public AuditLogController(AuditLogService auditLogService) {
        this.auditLogService = auditLogService;
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get audit log entries for a user")
    public ResponseEntity<List<AuditLog>> getByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(auditLogService.getLogsForUser(userId));
    }

    @GetMapping("/resource")
    @Operation(summary = "Get audit log entries for a specific resource")
    public ResponseEntity<List<AuditLog>> getByResource(@RequestParam String resource,
                                                        @RequestParam String resourceId) {
        return ResponseEntity.ok(auditLogService.getLogsForResource(resource, resourceId));
    }
}
