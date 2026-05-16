package com.campus.userservice.api;

import com.campus.userservice.dto.ChangePasswordRequest;
import com.campus.userservice.dto.RegisterRequest;
import com.campus.userservice.dto.UserDto;
import com.campus.userservice.service.UserService;
import org.springframework.security.core.Authentication;
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
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@Tag(name = "Utilizatori", description = "Management utilizatori — doar ADMIN")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "Profilul meu", description = "Returneaza datele contului utilizatorului autentificat")
    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserDto> getMe(Authentication auth) {
        Long userId = Long.parseLong(auth.getName());
        return ResponseEntity.ok(userService.findById(userId));
    }

    @Operation(summary = "Schimbare parola proprie", description = "Schimba parola utilizatorului autentificat")
    @PutMapping("/me/password")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> changePassword(Authentication auth,
                                                @Valid @RequestBody ChangePasswordRequest request) {
        Long userId = Long.parseLong(auth.getName());
        userService.changePassword(userId, request.getCurrentPassword(), request.getNewPassword());
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Lista utilizatori", description = "Returneaza toti utilizatorii paginat")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista returnata cu succes"),
        @ApiResponse(responseCode = "403", description = "Acces interzis — necesita rol ADMIN")
    })
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<UserDto>> getAll(
            @Parameter(description = "Caută după username sau email")
            @RequestParam(required = false) String search,
            @Parameter(description = "Filtrează după rol: STUDENT, ADMIN, SECRETARIAT")
            @RequestParam(required = false) String role,
            Pageable pageable) {
        return ResponseEntity.ok(userService.findFiltered(search, role, pageable));
    }

    @Operation(summary = "Detalii utilizator", description = "Returneaza un utilizator dupa ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Utilizator gasit"),
        @ApiResponse(responseCode = "403", description = "Acces interzis"),
        @ApiResponse(responseCode = "404", description = "Utilizator negasit")
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or principal == #id.toString()")
    public ResponseEntity<UserDto> getById(
            @Parameter(description = "ID-ul utilizatorului") @PathVariable Long id) {
        return ResponseEntity.ok(userService.findById(id));
    }

    @Operation(summary = "Creare utilizator", description = "Creeaza un utilizator nou cu rol STUDENT")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Utilizator creat"),
        @ApiResponse(responseCode = "400", description = "Date invalide"),
        @ApiResponse(responseCode = "403", description = "Acces interzis — necesita rol ADMIN")
    })
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDto> create(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(userService.create(request));
    }

    @Operation(summary = "Actualizare utilizator")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Utilizator actualizat"),
        @ApiResponse(responseCode = "403", description = "Acces interzis — necesita rol ADMIN")
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDto> update(@PathVariable Long id,
                                           @Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(userService.update(id, request));
    }

    @Operation(summary = "Dezactivare utilizator", description = "Soft delete — seteaza enabled=false")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Utilizator dezactivat"),
        @ApiResponse(responseCode = "403", description = "Acces interzis — necesita rol ADMIN")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Schimbare rol", description = "Modifica rolul unui utilizator. Roluri disponibile: STUDENT, ADMIN, SECRETARIAT")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Rol modificat"),
        @ApiResponse(responseCode = "403", description = "Acces interzis — necesita rol ADMIN")
    })
    @PutMapping("/{id}/role")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDto> changeRole(@PathVariable Long id,
                                               @Parameter(description = "Noul rol: STUDENT, ADMIN, SECRETARIAT")
                                               @RequestParam String role) {
        return ResponseEntity.ok(userService.changeRole(id, role));
    }
}
