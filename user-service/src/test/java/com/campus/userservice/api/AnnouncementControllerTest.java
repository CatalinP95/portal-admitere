package com.campus.userservice.api;

import com.campus.userservice.dto.AnnouncementRequest;
import com.campus.userservice.model.Role;
import com.campus.userservice.model.User;
import com.campus.userservice.repository.AnnouncementRepository;
import com.campus.userservice.repository.RefreshTokenRepository;
import com.campus.userservice.repository.TagRepository;
import com.campus.userservice.repository.UserProfileRepository;
import com.campus.userservice.repository.UserRepository;
import com.campus.userservice.security.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import com.campus.userservice.service.AuditLogService;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AnnouncementControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private JwtUtil jwtUtil;
    @Autowired private UserRepository userRepository;
    @Autowired private UserProfileRepository userProfileRepository;
    @Autowired private RefreshTokenRepository refreshTokenRepository;
    @Autowired private AnnouncementRepository announcementRepository;
    @Autowired private TagRepository tagRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private ObjectMapper objectMapper;
    @MockBean private AuditLogService auditLogService;

    private String adminToken;
    private String studentToken;

    @BeforeEach
    void setUp() {
        announcementRepository.deleteAll();
        tagRepository.deleteAll();
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
    void createAnnouncement_asAdmin_returns200() throws Exception {
        AnnouncementRequest req = new AnnouncementRequest();
        req.setTitle("Anunt important");
        req.setContent("Continut anunt");
        req.setTagNames(List.of("IMPORTANT"));

        mockMvc.perform(post("/api/announcements")
                        .with(csrf())
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Anunt important"))
                .andExpect(jsonPath("$.tags[0]").value("IMPORTANT"));
    }

    @Test
    void createAnnouncement_asStudent_returns403() throws Exception {
        AnnouncementRequest req = new AnnouncementRequest();
        req.setTitle("Anunt student");
        req.setContent("Continut");

        mockMvc.perform(post("/api/announcements")
                        .with(csrf())
                        .header("Authorization", "Bearer " + studentToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isForbidden());
    }

    @Test
    void getAnnouncements_asStudent_returns200() throws Exception {
        // creaza un anunt ca admin intai
        AnnouncementRequest req = new AnnouncementRequest();
        req.setTitle("Anunt vizibil");
        req.setContent("Continut vizibil");

        mockMvc.perform(post("/api/announcements")
                        .with(csrf())
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)));

        mockMvc.perform(get("/api/announcements")
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void getAnnouncements_noToken_returns403() throws Exception {
        mockMvc.perform(get("/api/announcements"))
                .andExpect(status().isForbidden());
    }

    @Test
    void getAnnouncementsByTag_returnsFiltered() throws Exception {
        AnnouncementRequest important = new AnnouncementRequest();
        important.setTitle("Anunt IMPORTANT");
        important.setContent("Continut");
        important.setTagNames(List.of("IMPORTANT"));

        AnnouncementRequest academic = new AnnouncementRequest();
        academic.setTitle("Anunt ACADEMIC");
        academic.setContent("Continut");
        academic.setTagNames(List.of("ACADEMIC"));

        mockMvc.perform(post("/api/announcements")
                .with(csrf())
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(important)));

        mockMvc.perform(post("/api/announcements")
                .with(csrf())
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(academic)));

        mockMvc.perform(get("/api/announcements?tag=IMPORTANT")
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.content[0].title").value("Anunt IMPORTANT"));
    }

    @Test
    void deleteAnnouncement_asAdmin_returns204() throws Exception {
        AnnouncementRequest req = new AnnouncementRequest();
        req.setTitle("De sters");
        req.setContent("Continut");

        String response = mockMvc.perform(post("/api/announcements")
                        .with(csrf())
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andReturn().getResponse().getContentAsString();

        Long id = objectMapper.readTree(response).get("id").asLong();

        mockMvc.perform(delete("/api/announcements/" + id)
                        .with(csrf())
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/announcements")
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(jsonPath("$.totalElements").value(0));
    }
}
