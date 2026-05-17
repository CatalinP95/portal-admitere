package com.campus.userservice.dto.chat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ChatRequest {

    @NotBlank(message = "Mesajul nu poate fi gol")
    @Size(max = 2000, message = "Mesajul nu poate depasi 2000 de caractere")
    private String message;

    public ChatRequest() {}

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
