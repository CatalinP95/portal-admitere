package com.campus.userservice.api;

import com.campus.userservice.model.Role;
import com.campus.userservice.model.User;
import com.campus.userservice.repository.RefreshTokenRepository;
import com.campus.userservice.repository.UserProfileRepository;
import com.campus.userservice.repository.UserRepository;
import com.campus.userservice.security.JwtUtil;
import com.campus.userservice.service.AuditLogService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class MenuControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private JwtUtil jwtUtil;
    @Autowired private UserRepository userRepository;
    @Autowired private UserProfileRepository userProfileRepository;
    @Autowired private RefreshTokenRepository refreshTokenRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @MockBean private AuditLogService auditLogService;

    private String studentToken;
    private String adminToken;
    private String secretariatToken;

    @BeforeEach
    void setUp() {
        userProfileRepository.deleteAll();
        refreshTokenRepository.deleteAll();
        userRepository.deleteAll();

        User student = new User();
        student.setUsername("student"); student.setEmail("student@test.com");
        student.setPassword(passwordEncoder.encode("pass")); student.setRole(Role.STUDENT); student.setEnabled(true);
        Long sid = userRepository.save(student).getId();
        studentToken = jwtUtil.generateToken(String.valueOf(sid), "STUDENT");

        User admin = new User();
        admin.setUsername("admin"); admin.setEmail("admin@test.com");
        admin.setPassword(passwordEncoder.encode("pass")); admin.setRole(Role.ADMIN); admin.setEnabled(true);
        Long aid = userRepository.save(admin).getId();
        adminToken = jwtUtil.generateToken(String.valueOf(aid), "ADMIN");

        User sec = new User();
        sec.setUsername("sec"); sec.setEmail("sec@test.com");
        sec.setPassword(passwordEncoder.encode("pass")); sec.setRole(Role.SECRETARIAT); sec.setEnabled(true);
        Long secId = userRepository.save(sec).getId();
        secretariatToken = jwtUtil.generateToken(String.valueOf(secId), "SECRETARIAT");
    }

    @Test
    void getMenu_asStudent_returnsDashboardAndProfile() throws Exception {
        mockMvc.perform(get("/api/menu")
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].label").value("Dashboard"))
                .andExpect(jsonPath("$[1].label").value("Profilul Meu"));
    }

    @Test
    void getMenu_asAdmin_returnsUtilizatoriItem() throws Exception {
        mockMvc.perform(get("/api/menu")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[1].label").value("Utilizatori"))
                .andExpect(jsonPath("$[1].route").value("/users"));
    }

    @Test
    void getMenu_asSecretariat_returnsCereriAdmitere() throws Exception {
        mockMvc.perform(get("/api/menu")
                        .header("Authorization", "Bearer " + secretariatToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[1].label").value("Cereri Admitere"));
    }

    @Test
    void getMenu_noToken_returns403() throws Exception {
        mockMvc.perform(get("/api/menu"))
                .andExpect(status().isForbidden());
    }

    @Test
    void getRole_asStudent_returnsStudent() throws Exception {
        mockMvc.perform(get("/api/menu/role")
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.role").value("STUDENT"));
    }

    @Test
    void getRole_asAdmin_returnsAdmin() throws Exception {
        mockMvc.perform(get("/api/menu/role")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.role").value("ADMIN"));
    }

    @Test
    void getRole_noToken_returns403() throws Exception {
        mockMvc.perform(get("/api/menu/role"))
                .andExpect(status().isForbidden());
    }
}
