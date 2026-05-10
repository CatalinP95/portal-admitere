package com.campus.userservice.api;

import com.campus.userservice.dto.AnnouncementDto;
import com.campus.userservice.dto.AnnouncementRequest;
import com.campus.userservice.service.AnnouncementService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/announcements")
public class AnnouncementController {

    private final AnnouncementService announcementService;

    public AnnouncementController(AnnouncementService announcementService) {
        this.announcementService = announcementService;
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page<AnnouncementDto>> getAll(
            @RequestParam(required = false) String tag,
            Pageable pageable) {
        if (tag != null && !tag.isBlank()) {
            return ResponseEntity.ok(announcementService.getByTag(tag.toUpperCase(), pageable));
        }
        return ResponseEntity.ok(announcementService.getAll(pageable));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AnnouncementDto> create(
            Authentication auth,
            @Valid @RequestBody AnnouncementRequest request) {
        Long userId = Long.parseLong(auth.getName());
        return ResponseEntity.ok(announcementService.create(userId, request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AnnouncementDto> update(
            @PathVariable Long id,
            @Valid @RequestBody AnnouncementRequest request) {
        return ResponseEntity.ok(announcementService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        announcementService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
