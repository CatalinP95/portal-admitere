package com.campus.userservice.api;

import com.campus.userservice.dto.UserProfileDto;
import com.campus.userservice.dto.UserProfileRequest;
import com.campus.userservice.service.UserProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profile")
@Tag(name = "Profil utilizator", description = "Gestionare date personale — firstName, lastName, CNP, telefon")
public class UserProfileController {

    private final UserProfileService userProfileService;

    public UserProfileController(UserProfileService userProfileService) {
        this.userProfileService = userProfileService;
    }

    @Operation(summary = "Profil propriu", description = "Returneaza profilul utilizatorului autentificat")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Profil returnat"),
        @ApiResponse(responseCode = "403", description = "Neautentificat"),
        @ApiResponse(responseCode = "404", description = "Profilul nu a fost completat inca")
    })
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserProfileDto> getOwnProfile(Authentication auth) {
        Long userId = Long.parseLong(auth.getName());
        return ResponseEntity.ok(userProfileService.getByUserId(userId));
    }

    @Operation(summary = "Salveaza profil", description = "Creeaza sau actualizeaza profilul utilizatorului autentificat")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Profil salvat"),
        @ApiResponse(responseCode = "400", description = "Date invalide — CNP format gresit, telefon invalid"),
        @ApiResponse(responseCode = "403", description = "Neautentificat")
    })
    @PutMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserProfileDto> updateOwnProfile(Authentication auth,
                                                            @Valid @RequestBody UserProfileRequest request) {
        Long userId = Long.parseLong(auth.getName());
        return ResponseEntity.ok(userProfileService.save(userId, request));
    }

    @Operation(summary = "Toate profilurile (paginat)", description = "Returneaza toate profilurile — doar ADMIN")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista profiluri"),
        @ApiResponse(responseCode = "403", description = "Acces interzis")
    })
    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<UserProfileDto>> getAllProfiles(
            @PageableDefault(size = 10, sort = "lastName") Pageable pageable) {
        return ResponseEntity.ok(userProfileService.getAll(pageable));
    }

    @Operation(summary = "Profil dupa ID", description = "Returneaza profilul unui utilizator — ADMIN sau proprietar")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Profil returnat"),
        @ApiResponse(responseCode = "403", description = "Acces interzis"),
        @ApiResponse(responseCode = "404", description = "Profil negasit")
    })
    @GetMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN') or principal == #userId.toString()")
    public ResponseEntity<UserProfileDto> getProfileById(
            @Parameter(description = "ID-ul utilizatorului") @PathVariable Long userId) {
        return ResponseEntity.ok(userProfileService.getByUserId(userId));
    }
}
