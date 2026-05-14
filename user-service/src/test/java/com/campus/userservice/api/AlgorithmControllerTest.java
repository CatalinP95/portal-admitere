package com.campus.userservice.api;

import com.campus.userservice.client.AdmissionsClient;
import com.campus.userservice.service.AuditLogService;
import com.campus.userservice.dto.algorithm.ApplicationRankDto;
import com.campus.userservice.dto.algorithm.FacultySpotsDto;
import com.campus.userservice.model.Role;
import com.campus.userservice.model.User;
import com.campus.userservice.repository.RefreshTokenRepository;
import com.campus.userservice.repository.UserProfileRepository;
import com.campus.userservice.repository.UserRepository;
import com.campus.userservice.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AlgorithmControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private JwtUtil jwtUtil;
    @Autowired private UserRepository userRepository;
    @Autowired private UserProfileRepository userProfileRepository;
    @Autowired private RefreshTokenRepository refreshTokenRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    @MockBean private AdmissionsClient admissionsClient;
    @MockBean private AuditLogService auditLogService;

    private String adminToken;
    private String studentToken;

    @BeforeEach
    void setUp() {
        userProfileRepository.deleteAll();
        refreshTokenRepository.deleteAll();
        userRepository.deleteAll();

        User admin = new User();
        admin.setUsername("admin"); admin.setEmail("admin@test.com");
        admin.setPassword(passwordEncoder.encode("admin123"));
        admin.setRole(Role.ADMIN); admin.setEnabled(true);
        Long adminId = userRepository.save(admin).getId();
        adminToken = jwtUtil.generateToken(String.valueOf(adminId), "ADMIN");

        User student = new User();
        student.setUsername("student"); student.setEmail("student@test.com");
        student.setPassword(passwordEncoder.encode("student123"));
        student.setRole(Role.STUDENT); student.setEnabled(true);
        Long studentId = userRepository.save(student).getId();
        studentToken = jwtUtil.generateToken(String.valueOf(studentId), "STUDENT");
    }

    @Test
    void runAlgorithm_asAdmin_returns200() throws Exception {
        ApplicationRankDto candidate = new ApplicationRankDto();
        candidate.setApplicationId(1L);
        candidate.setUserId(10L);
        candidate.setFacultyId(1);
        candidate.setAverageBac(9.5f);
        candidate.setFormFunding(1);

        FacultySpotsDto spots = new FacultySpotsDto();
        spots.setFacultyId(1);
        spots.setNrBuget(2);
        spots.setNrTaxa(1);

        when(admissionsClient.getPendingApplications(anyInt())).thenReturn(List.of(candidate));
        when(admissionsClient.getFacultySpots(anyInt())).thenReturn(spots);
        doNothing().when(admissionsClient).updateStatuses(any());

        mockMvc.perform(post("/api/algorithm/run/1")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalProcessed").value(1))
                .andExpect(jsonPath("$.approvedBuget").value(1));
    }

    @Test
    void runAlgorithm_asStudent_returns403() throws Exception {
        mockMvc.perform(post("/api/algorithm/run/1")
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void runAlgorithm_noToken_returns403() throws Exception {
        mockMvc.perform(post("/api/algorithm/run/1"))
                .andExpect(status().isForbidden());
    }
}
