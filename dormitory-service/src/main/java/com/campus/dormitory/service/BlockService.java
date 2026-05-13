package com.campus.dormitory.service;

import com.campus.dormitory.exception.ResourceNotFoundException;
import com.campus.dormitory.model.Block;
import com.campus.dormitory.repository.jpa.BlockRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class BlockService {

    private final BlockRepository blockRepository;

    public BlockService(BlockRepository blockRepository) {
        this.blockRepository = blockRepository;
    }

    public List<Block> findAll() {
        return blockRepository.findAll();
    }

    public Page<Block> findAll(Pageable pageable) {
        return blockRepository.findAll(pageable);
    }

    public Block findById(Integer id) {
        return blockRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Block", id));
    }

    public Block findByName(String name) {
        return blockRepository.findFirstByNameAndEnabled(name, 1)
                .orElseThrow(() -> new ResourceNotFoundException("Block", name));
    }

    @Transactional
    public Block create(Block block, Long userId) {
        Date now = new Date();
        block.setEnabled(1);
        block.setCreatedBy(userId);
        block.setCreatedAt(now);
        return blockRepository.save(block);
    }

    @Transactional
    public Block update(Integer id, Block block, Long userId) {
        Block existing = findById(id);
        existing.setName(block.getName());
        if (block.getEnabled() != null) existing.setEnabled(block.getEnabled());
        existing.setModifiedBy(userId);
        existing.setModifiedAt(new Date());
        return blockRepository.save(existing);
    }

    @Transactional
    public void delete(Integer id) {
        Block existing = findById(id);
        existing.setEnabled(0);
        blockRepository.save(existing);
    }

    public List<Object[]> countRequestsPerBlock() {
        return blockRepository.countRequestsPerBlock();
    }

    public List<Object[]> countStudentsPerBlock() {
        return blockRepository.countStudentsPerBlock();
    }
}
