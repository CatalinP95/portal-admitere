package com.campus.userservice.api;

import com.campus.userservice.dto.UserProfileRequest;
import com.campus.userservice.model.Role;
import com.campus.userservice.model.User;
import com.campus.userservice.repository.UserProfileRepository;
import com.campus.userservice.repository.UserRepository;
import com.campus.userservice.security.JwtUtil;
import com.campus.userservice.service.AuditLogService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UserProfileControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private JwtUtil jwtUtil;
    @Autowired private UserRepository userRepository;
    @Autowired private UserProfileRepository userProfileRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private ObjectMapper objectMapper;
    @MockBean private AuditLogService auditLogService;

    private String studentToken;
    private String adminToken;
    private Long studentId;
    private Long adminId;

    @BeforeEach
    void setUp() {
        userProfileRepository.deleteAll();
        userRepository.deleteAll();

        User student = new User();
        student.setUsername("student"); student.setEmail("student@test.com");
        student.setPassword(passwordEncoder.encode("student123"));
        student.setRole(Role.STUDENT); student.setEnabled(true);
        studentId = userRepository.save(student).getId();
        studentToken = jwtUtil.generateToken(String.valueOf(studentId), "STUDENT");

        User admin = new User();
        admin.setUsername("admin"); admin.setEmail("admin@test.com");
        admin.setPassword(passwordEncoder.encode("admin123"));
        admin.setRole(Role.ADMIN); admin.setEnabled(true);
        adminId = userRepository.save(admin).getId();
        adminToken = jwtUtil.generateToken(String.valueOf(adminId), "ADMIN");
    }

    @Test
    void saveProfile_asStudent_returns200() throws Exception {
        UserProfileRequest req = new UserProfileRequest();
        req.setFirstName("Ion");
        req.setLastName("Popescu");
        req.setCnp("1990101123456");
        req.setPhone("+40722123456");

        mockMvc.perform(put("/api/profile")
                        .header("Authorization", "Bearer " + studentToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Ion"))
                .andExpect(jsonPath("$.lastName").value("Popescu"))
                .andExpect(jsonPath("$.cnp").value("1990101123456"));
    }

    @Test
    void getOwnProfile_afterSave_returns200() throws Exception {
        UserProfileRequest req = new UserProfileRequest();
        req.setFirstName("Maria");
        req.setLastName("Ionescu");
        req.setCnp("2950202234567");
        req.setPhone("+40733987654");

        mockMvc.perform(put("/api/profile")
                .header("Authorization", "Bearer " + studentToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)));

        mockMvc.perform(get("/api/profile")
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Maria"))
                .andExpect(jsonPath("$.lastName").value("Ionescu"));
    }

    @Test
    void getProfile_noToken_returns403() throws Exception {
        mockMvc.perform(get("/api/profile"))
                .andExpect(status().isForbidden());
    }

    @Test
    void saveProfile_invalidCnp_returns400() throws Exception {
        UserProfileRequest req = new UserProfileRequest();
        req.setFirstName("Ion");
        req.setLastName("Popescu");
        req.setCnp("123");

        mockMvc.perform(put("/api/profile")
                        .header("Authorization", "Bearer " + studentToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getProfileById_asAdmin_returns200() throws Exception {
        UserProfileRequest req = new UserProfileRequest();
        req.setFirstName("Test");
        req.setLastName("User");
        req.setCnp("1990101123456");

        mockMvc.perform(put("/api/profile")
                .header("Authorization", "Bearer " + studentToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)));

        mockMvc.perform(get("/api/profile/" + studentId)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Test"));
    }
}
