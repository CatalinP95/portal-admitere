package com.campus.userservice.api;

import com.campus.userservice.dto.AuthResponse;
import com.campus.userservice.dto.LoginRequest;
import com.campus.userservice.dto.RegisterRequest;
import com.campus.userservice.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@Tag(name = "Autentificare", description = "Inregistrare, login, refresh token si logout")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @Operation(summary = "Inregistrare utilizator nou", description = "Creaza un cont nou cu rol STUDENT implicit")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Cont creat cu succes, returneaza access + refresh token"),
        @ApiResponse(responseCode = "400", description = "Date invalide sau username/email deja existent")
    })
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @Operation(summary = "Autentificare", description = "Login cu username si parola, returneaza JWT")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Autentificare reusita"),
        @ApiResponse(responseCode = "401", description = "Credentiale incorecte")
    })
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @Operation(summary = "Refresh token", description = "Obtine un nou access token folosind refresh token-ul din header")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Token reinnoit cu succes"),
        @ApiResponse(responseCode = "400", description = "Refresh token invalid sau expirat")
    })
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@RequestHeader("Refresh-Token") String refreshToken) {
        return ResponseEntity.ok(authService.refresh(refreshToken));
    }

    @Operation(summary = "Logout", description = "Revoaca refresh token-ul curent")
    @ApiResponse(responseCode = "204", description = "Logout efectuat cu succes")
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestHeader("Refresh-Token") String refreshToken) {
        authService.logout(refreshToken);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/csrf")
    public ResponseEntity<Void> csrf(HttpServletRequest request, HttpServletResponse response) {
        CsrfToken token = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
        if (token != null) {
            token.getToken();
        }
        return ResponseEntity.ok().build();
    }
}
