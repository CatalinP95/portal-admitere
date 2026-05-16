package com.campus.userservice.api;

import com.campus.userservice.dto.RegisterRequest;
import com.campus.userservice.model.Role;
import com.campus.userservice.model.User;
import com.campus.userservice.repository.RefreshTokenRepository;
import com.campus.userservice.repository.UserProfileRepository;
import com.campus.userservice.repository.UserRepository;
import com.campus.userservice.security.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import com.campus.userservice.service.AuditLogService;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UserControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private JwtUtil jwtUtil;
    @Autowired private UserRepository userRepository;
    @Autowired private UserProfileRepository userProfileRepository;
    @Autowired private RefreshTokenRepository refreshTokenRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private ObjectMapper objectMapper;
    @MockBean private AuditLogService auditLogService;

    private String adminToken;
    private String studentToken;
    private Long adminId;

    @BeforeEach
    void setUp() {
        userProfileRepository.deleteAll();
        refreshTokenRepository.deleteAll();
        userRepository.deleteAll();

        User admin = new User();
        admin.setUsername("admin"); admin.setEmail("admin@test.com");
        admin.setPassword(passwordEncoder.encode("admin123"));
        admin.setRole(Role.ADMIN); admin.setEnabled(true);
        adminId = userRepository.save(admin).getId();
        adminToken = jwtUtil.generateToken(String.valueOf(adminId), "ADMIN");

        User student = new User();
        student.setUsername("student"); student.setEmail("student@test.com");
        student.setPassword(passwordEncoder.encode("student123"));
        student.setRole(Role.STUDENT); student.setEnabled(true);
        Long studentId = userRepository.save(student).getId();
        studentToken = jwtUtil.generateToken(String.valueOf(studentId), "STUDENT");
    }

    @Test
    void getAllUsers_asAdmin_returns200() throws Exception {
        mockMvc.perform(get("/api/users")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.totalElements").value(2));
    }

    @Test
    void getAllUsers_asStudent_returns403() throws Exception {
        mockMvc.perform(get("/api/users")
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void getAllUsers_noToken_returns403() throws Exception {
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isForbidden());
    }

    @Test
    void getUserById_asAdmin_returns200() throws Exception {
        mockMvc.perform(get("/api/users/" + adminId)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("admin"))
                .andExpect(jsonPath("$.password").doesNotExist());
    }

    @Test
    void createUser_asAdmin_returns200() throws Exception {
        RegisterRequest req = new RegisterRequest();
        req.setUsername("newuser"); req.setEmail("new@test.com"); req.setPassword("newpass123");

        mockMvc.perform(post("/api/users")
                        .with(csrf())
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("newuser"))
                .andExpect(jsonPath("$.role").value("STUDENT"));
    }

    @Test
    void createUser_asStudent_returns403() throws Exception {
        RegisterRequest req = new RegisterRequest();
        req.setUsername("xyz"); req.setEmail("x@x.com"); req.setPassword("pass1234");

        mockMvc.perform(post("/api/users")
                        .with(csrf())
                        .header("Authorization", "Bearer " + studentToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isForbidden());
    }

    @Test
    void changeRole_asAdmin_returns200() throws Exception {
        mockMvc.perform(put("/api/users/" + adminId + "/role?role=SECRETARIAT")
                        .with(csrf())
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.role").value("SECRETARIAT"));
    }

    @Test
    void deleteUser_asAdmin_returns204() throws Exception {
        mockMvc.perform(delete("/api/users/" + adminId)
                        .with(csrf())
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNoContent());
    }

    @Test
    void getUserById_asStudent_returns403() throws Exception {
        mockMvc.perform(get("/api/users/" + adminId)
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void getUserById_notFound_returns404() throws Exception {
        mockMvc.perform(get("/api/users/99999")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void createUser_missingEmail_returns400() throws Exception {
        RegisterRequest req = new RegisterRequest();
        req.setUsername("nomail");
        req.setPassword("pass1234");

        mockMvc.perform(post("/api/users")
                        .with(csrf())
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void changeRole_asStudent_returns403() throws Exception {
        mockMvc.perform(put("/api/users/" + adminId + "/role?role=SECRETARIAT")
                        .with(csrf())
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void deleteUser_asStudent_returns403() throws Exception {
        mockMvc.perform(delete("/api/users/" + adminId)
                        .with(csrf())
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isForbidden());
    }
}
