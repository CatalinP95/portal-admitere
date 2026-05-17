package com.campus.userservice.api;

import com.campus.userservice.dto.chat.ChatRequest;
import com.campus.userservice.dto.chat.ChatResponse;
import com.campus.userservice.service.ClaudeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chat")
@Tag(name = "AI Asistent", description = "Chatbot de admitere bazat pe Claude AI")
public class ChatController {

    private final ClaudeService claudeService;

    public ChatController(ClaudeService claudeService) {
        this.claudeService = claudeService;
    }

    @Operation(
        summary = "Trimite o intrebare asistentului AI",
        description = "Candidatul trimite o intrebare despre admitere si primeste un raspuns generat de Claude AI. " +
                      "Raspunsurile acopera: documente necesare, medii, facultati, termene si proceduri."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Raspuns generat cu succes"),
        @ApiResponse(responseCode = "400", description = "Mesaj invalid (gol sau prea lung)"),
        @ApiResponse(responseCode = "401", description = "Neautentificat")
    })
    @PostMapping("/ask")
    public ResponseEntity<ChatResponse> ask(@Valid @RequestBody ChatRequest request) {
        String reply = claudeService.ask(request.getMessage());
        return ResponseEntity.ok(new ChatResponse(reply));
    }
}
