package com.campus.userservice.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;

@Service
public class ClaudeService {

    private static final Logger log = LoggerFactory.getLogger(ClaudeService.class);

    private static final String GROQ_API_URL = "https://api.groq.com";
    private static final String MODEL = "llama-3.1-8b-instant";
    private static final int MAX_TOKENS = 1024;

    private static final String SYSTEM_PROMPT = """
            Ești un asistent virtual al portalului de admitere universitară.
            Ajuți candidații cu întrebări despre procesul de admitere: documente necesare,
            medii de admitere, facultăți disponibile, termene limită și proceduri generale.
            Răspunzi concis, clar și în limba română.
            Dacă nu știi răspunsul exact, îndrumă candidatul să contacteze secretariatul universității.
            """;

    @Value("${groq.api-key:}")
    private String apiKey;

    private final RestClient restClient;

    public ClaudeService() {
        this.restClient = RestClient.builder()
                .baseUrl(GROQ_API_URL)
                .build();
    }

    public String ask(String userMessage) {
        if (apiKey == null || apiKey.isBlank()) {
            log.warn("Groq API key neconfigurat — raspuns implicit returnat");
            return "Serviciul de asistenta AI nu este configurat momentan. Contactati secretariatul pentru informatii.";
        }

        try {
            GroqRequest request = new GroqRequest(
                    MODEL,
                    MAX_TOKENS,
                    List.of(
                            new GroqMessage("system", SYSTEM_PROMPT),
                            new GroqMessage("user", userMessage)
                    )
            );

            GroqResponse response = restClient.post()
                    .uri("/openai/v1/chat/completions")
                    .header("Authorization", "Bearer " + apiKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(request)
                    .retrieve()
                    .body(GroqResponse.class);

            if (response != null && response.choices() != null && !response.choices().isEmpty()) {
                return response.choices().get(0).message().content();
            }
            return "Nu am putut genera un raspuns. Incearca din nou.";

        } catch (Exception e) {
            log.error("Eroare la apelul Groq API: {}", e.getMessage());
            return "Serviciul de asistenta AI este momentan indisponibil. Incearca din nou mai tarziu.";
        }
    }

    // --- DTOs interne pentru Groq API (OpenAI-compatible) ---

    record GroqRequest(
            String model,
            @JsonProperty("max_tokens") int maxTokens,
            List<GroqMessage> messages
    ) {}

    record GroqMessage(String role, String content) {}

    record GroqResponse(List<GroqChoice> choices) {}

    record GroqChoice(GroqMessage message) {}
}
