package com.campus.userservice.api;

import com.campus.userservice.model.User;
import com.campus.userservice.repository.UserRepository;
import com.campus.userservice.service.MenuService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/menu")
@Tag(name = "Meniu navigare", description = "Returneaza elementele de meniu dinamice in functie de rolul utilizatorului autentificat")
public class MenuController {

    private final MenuService menuService;
    private final UserRepository userRepository;

    public MenuController(MenuService menuService, UserRepository userRepository) {
        this.menuService = menuService;
        this.userRepository = userRepository;
    }

    @Operation(summary = "Meniu pentru rolul curent", description = "Returneaza lista de elemente de meniu (label + url) corespunzatoare rolului utilizatorului autentificat")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Meniu returnat cu succes"),
        @ApiResponse(responseCode = "403", description = "Neautentificat")
    })
    @GetMapping
    public ResponseEntity<List<Map<String, String>>> getMenu(Authentication authentication) {
        User user = userRepository.findById(Long.parseLong(authentication.getName()))
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return ResponseEntity.ok(menuService.getMenuForRole(user.getRole()));
    }

    @Operation(summary = "Rolul utilizatorului curent", description = "Returneaza rolul utilizatorului autentificat: STUDENT, ADMIN sau SECRETARIAT")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Rol returnat: { \"role\": \"STUDENT\" }"),
        @ApiResponse(responseCode = "403", description = "Neautentificat")
    })
    @GetMapping("/role")
    public ResponseEntity<Map<String, String>> getRole(Authentication authentication) {
        User user = userRepository.findById(Long.parseLong(authentication.getName()))
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return ResponseEntity.ok(Map.of("role", user.getRole().name()));
    }
}
