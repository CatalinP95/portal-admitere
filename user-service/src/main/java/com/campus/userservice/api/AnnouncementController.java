package com.campus.userservice.api;

import com.campus.userservice.dto.AnnouncementDto;
import com.campus.userservice.dto.AnnouncementRequest;
import com.campus.userservice.service.AnnouncementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/announcements")
@Tag(name = "Anunturi", description = "Anunturi cu taguri — citire publica, scriere doar ADMIN")
public class AnnouncementController {

    private final AnnouncementService announcementService;

    public AnnouncementController(AnnouncementService announcementService) {
        this.announcementService = announcementService;
    }

    @Operation(summary = "Lista anunturi", description = "Returneaza anunturile active paginat. Filtreaza dupa tag cu ?tag=IMPORTANT")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista returnata"),
        @ApiResponse(responseCode = "403", description = "Neautentificat")
    })
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page<AnnouncementDto>> getAll(
            @Parameter(description = "Filtru tag: IMPORTANT, ACADEMIC, FINANCIAR, TERMEN_LIMITA")
            @RequestParam(required = false) String tag,
            @Parameter(description = "Caută în titlu")
            @RequestParam(required = false) String search,
            Pageable pageable) {
        return ResponseEntity.ok(announcementService.getFiltered(tag, search, pageable));
    }

    @Operation(summary = "Creare anunt", description = "Creeaza un anunt nou cu tag-uri. Tag-urile noi sunt create automat.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Anunt creat"),
        @ApiResponse(responseCode = "400", description = "Date invalide"),
        @ApiResponse(responseCode = "403", description = "Necesita rol ADMIN")
    })
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AnnouncementDto> create(
            Authentication auth,
            @Valid @RequestBody AnnouncementRequest request) {
        Long userId = Long.parseLong(auth.getName());
        return ResponseEntity.ok(announcementService.create(userId, request));
    }

    @Operation(summary = "Actualizare anunt")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Anunt actualizat"),
        @ApiResponse(responseCode = "403", description = "Necesita rol ADMIN"),
        @ApiResponse(responseCode = "404", description = "Anunt negasit")
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AnnouncementDto> update(
            @PathVariable Long id,
            @Valid @RequestBody AnnouncementRequest request) {
        return ResponseEntity.ok(announcementService.update(id, request));
    }

    @Operation(summary = "Stergere anunt", description = "Soft delete — anuntul nu mai apare in lista dar ramane in DB")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Anunt sters"),
        @ApiResponse(responseCode = "403", description = "Necesita rol ADMIN"),
        @ApiResponse(responseCode = "404", description = "Anunt negasit")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        announcementService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
