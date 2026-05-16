package com.campus.userservice.api;

import com.campus.userservice.service.AuditLogService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class HealthControllerTest {

    @Autowired private MockMvc mockMvc;
    @MockBean private AuditLogService auditLogService;

    @Test
    void discoveryHealth_returns200WithFallback() throws Exception {
        // Eureka not running in test — circuit breaker kicks in, fallback returns 200
        mockMvc.perform(get("/api/health/discovery"))
                .andExpect(status().isOk());
    }
}
