package com.campus.userservice.dto;

public class AuthResponse {

    private String accessToken;
    private String refreshToken;
    private String tokenType = "Bearer";
    private String role;
    private Long userId;

    public AuthResponse(String accessToken, String refreshToken, String role, Long userId) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.role = role;
        this.userId = userId;
    }

    public String getAccessToken() { return accessToken; }
    public String getRefreshToken() { return refreshToken; }
    public String getTokenType() { return tokenType; }
    public String getRole() { return role; }
    public Long getUserId() { return userId; }
}
