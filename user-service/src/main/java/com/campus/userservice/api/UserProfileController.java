package com.campus.userservice.api;

import com.campus.userservice.dto.UserProfileDto;
import com.campus.userservice.dto.UserProfileRequest;
import com.campus.userservice.service.UserProfileService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profile")
public class UserProfileController {

    private final UserProfileService userProfileService;

    public UserProfileController(UserProfileService userProfileService) {
        this.userProfileService = userProfileService;
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserProfileDto> getOwnProfile(Authentication auth) {
        Long userId = Long.parseLong(auth.getName());
        return ResponseEntity.ok(userProfileService.getByUserId(userId));
    }

    @PutMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserProfileDto> updateOwnProfile(Authentication auth,
                                                            @Valid @RequestBody UserProfileRequest request) {
        Long userId = Long.parseLong(auth.getName());
        return ResponseEntity.ok(userProfileService.save(userId, request));
    }

    @GetMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN') or principal == #userId.toString()")
    public ResponseEntity<UserProfileDto> getProfileById(@PathVariable Long userId) {
        return ResponseEntity.ok(userProfileService.getByUserId(userId));
    }
}
