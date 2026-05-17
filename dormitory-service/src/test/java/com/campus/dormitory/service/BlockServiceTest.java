package com.campus.dormitory.service;

import com.campus.dormitory.exception.ResourceNotFoundException;
import com.campus.dormitory.model.Block;
import com.campus.dormitory.repository.jpa.BlockRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BlockServiceTest {

    @Mock private BlockRepository blockRepository;

    @InjectMocks private BlockService blockService;

    private Block existingBlock;

    @BeforeEach
    void setUp() {
        existingBlock = new Block();
        existingBlock.setId(1);
        existingBlock.setName("A1");
        existingBlock.setEnabled(1);
    }

    @Test
    void findAll_returnsAllBlocks() {
        Block b2 = new Block(); b2.setId(2); b2.setName("A2");
        when(blockRepository.findAll()).thenReturn(Arrays.asList(existingBlock, b2));

        List<Block> result = blockService.findAll();

        assertEquals(2, result.size());
        verify(blockRepository).findAll();
    }

    @Test
    void findById_existing_returnsBlock() {
        when(blockRepository.findById(1)).thenReturn(Optional.of(existingBlock));

        Block result = blockService.findById(1);

        assertEquals("A1", result.getName());
    }

    @Test
    void findById_missing_throwsResourceNotFound() {
        when(blockRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> blockService.findById(99));
    }

    @Test
    void create_setsAuditFieldsAndSaves() {
        Block input = new Block();
        input.setName("B1");
        when(blockRepository.save(any(Block.class))).thenAnswer(inv -> inv.getArgument(0));

        Block saved = blockService.create(input, 42L);

        assertEquals(1, saved.getEnabled());
        assertEquals(42L, saved.getCreatedBy());
        assertNotNull(saved.getCreatedAt());
        verify(blockRepository).save(input);
    }

    @Test
    void update_existing_modifiesAndSaves() {
        Block patch = new Block();
        patch.setName("A1-renamed");
        when(blockRepository.findById(1)).thenReturn(Optional.of(existingBlock));
        when(blockRepository.save(any(Block.class))).thenAnswer(inv -> inv.getArgument(0));

        Block saved = blockService.update(1, patch, 7L);

        assertEquals("A1-renamed", saved.getName());
        assertEquals(7L, saved.getModifiedBy());
        assertNotNull(saved.getModifiedAt());
    }

    @Test
    void delete_setsEnabledZero() {
        when(blockRepository.findById(1)).thenReturn(Optional.of(existingBlock));

        blockService.delete(1);

        assertEquals(0, existingBlock.getEnabled());
        verify(blockRepository).save(existingBlock);
    }
}
