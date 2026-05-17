package com.campus.dormitory.api;

import com.campus.dormitory.dto.BlockDto;
import com.campus.dormitory.model.Block;
import com.campus.dormitory.repository.jpa.BlockRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class BlockControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private BlockRepository blockRepository;
    @Autowired private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        blockRepository.deleteAll();
    }

    @Test
    void getAll_emptyDb_returnsEmptyArray() throws Exception {
        mockMvc.perform(get("/api/dormitory/blocks"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void create_validDto_returns201AndPersists() throws Exception {
        BlockDto dto = new BlockDto();
        dto.setName("A1");

        mockMvc.perform(post("/api/dormitory/blocks")
                        .header("X-User-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("A1"))
                .andExpect(jsonPath("$.enabled").value(1));
    }

    @Test
    void create_blankName_returns400() throws Exception {
        BlockDto dto = new BlockDto();
        dto.setName("");

        mockMvc.perform(post("/api/dormitory/blocks")
                        .header("X-User-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getOne_missing_returns404() throws Exception {
        mockMvc.perform(get("/api/dormitory/blocks/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void update_existing_modifiesAndReturns200() throws Exception {
        Block b = new Block();
        b.setName("A1");
        b.setEnabled(1);
        Block saved = blockRepository.save(b);

        BlockDto dto = new BlockDto();
        dto.setName("A1-renamed");
        dto.setEnabled(1);

        mockMvc.perform(put("/api/dormitory/blocks/" + saved.getId())
                        .header("X-User-Id", "7")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("A1-renamed"));
    }

    @Test
    void delete_existing_returns204AndSoftDeletes() throws Exception {
        Block b = new Block();
        b.setName("B1");
        b.setEnabled(1);
        Block saved = blockRepository.save(b);

        mockMvc.perform(delete("/api/dormitory/blocks/" + saved.getId()))
                .andExpect(status().isNoContent());

        Block reloaded = blockRepository.findById(saved.getId()).orElseThrow();
        assert reloaded.getEnabled() == 0;
    }
}
