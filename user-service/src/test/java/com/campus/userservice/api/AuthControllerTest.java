package com.campus.userservice.api;

import com.campus.userservice.dto.LoginRequest;
import com.campus.userservice.dto.RegisterRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import com.campus.userservice.service.AuditLogService;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AuthControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @MockBean private AuditLogService auditLogService;

    @Test
    @Order(1)
    void register_validRequest_returns200() throws Exception {
        RegisterRequest req = new RegisterRequest();
        req.setUsername("testuser");
        req.setEmail("test@test.com");
        req.setPassword("pass1234");

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists())
                .andExpect(jsonPath("$.role").value("STUDENT"))
                .andExpect(jsonPath("$.userId").exists());
    }

    @Test
    @Order(2)
    void login_validCredentials_returns200() throws Exception {
        // register first
        RegisterRequest reg = new RegisterRequest();
        reg.setUsername("loginuser"); reg.setEmail("login@test.com"); reg.setPassword("pass1234");
        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reg)));

        LoginRequest req = new LoginRequest();
        req.setUsername("loginuser");
        req.setPassword("pass1234");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists());
    }

    @Test
    @Order(3)
    void login_invalidCredentials_returns401() throws Exception {
        LoginRequest req = new LoginRequest();
        req.setUsername("nonexistent");
        req.setPassword("wrongpass");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @Order(4)
    void register_invalidEmail_returns400() throws Exception {
        RegisterRequest req = new RegisterRequest();
        req.setUsername("baduser");
        req.setEmail("not-an-email");
        req.setPassword("pass1234");

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(5)
    void register_shortPassword_returns400() throws Exception {
        RegisterRequest req = new RegisterRequest();
        req.setUsername("baduser2");
        req.setEmail("bad2@test.com");
        req.setPassword("123");

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(6)
    void refresh_validToken_returns200() throws Exception {
        RegisterRequest reg = new RegisterRequest();
        reg.setUsername("refreshuser"); reg.setEmail("refresh@test.com"); reg.setPassword("pass1234");

        String body = mockMvc.perform(post("/auth/register").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reg)))
                .andReturn().getResponse().getContentAsString();

        String refreshToken = objectMapper.readTree(body).get("refreshToken").asText();

        mockMvc.perform(post("/auth/refresh").with(csrf())
                        .header("Refresh-Token", refreshToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists())
                .andExpect(jsonPath("$.refreshToken").exists());
    }

    @Test
    @Order(7)
    void refresh_invalidToken_returns400() throws Exception {
        mockMvc.perform(post("/auth/refresh").with(csrf())
                        .header("Refresh-Token", "invalid-token-xyz"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(8)
    void logout_validToken_returns204() throws Exception {
        RegisterRequest reg = new RegisterRequest();
        reg.setUsername("logoutuser"); reg.setEmail("logout@test.com"); reg.setPassword("pass1234");

        String body = mockMvc.perform(post("/auth/register").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reg)))
                .andReturn().getResponse().getContentAsString();

        String refreshToken = objectMapper.readTree(body).get("refreshToken").asText();

        mockMvc.perform(post("/auth/logout").with(csrf())
                        .header("Refresh-Token", refreshToken))
                .andExpect(status().isNoContent());
    }

    @Test
    @Order(9)
    void register_duplicateUsername_returns400() throws Exception {
        RegisterRequest reg = new RegisterRequest();
        reg.setUsername("dupuser"); reg.setEmail("dup@test.com"); reg.setPassword("pass1234");

        mockMvc.perform(post("/auth/register").with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reg)));

        RegisterRequest dup = new RegisterRequest();
        dup.setUsername("dupuser"); dup.setEmail("other@test.com"); dup.setPassword("pass1234");

        mockMvc.perform(post("/auth/register").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dup)))
                .andExpect(status().isBadRequest());
    }
}
