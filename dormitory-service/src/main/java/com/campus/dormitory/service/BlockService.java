package com.campus.dormitory.service;

import com.campus.dormitory.exception.ResourceNotFoundException;
import com.campus.dormitory.model.Block;
import com.campus.dormitory.repository.jpa.BlockRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class BlockService {

    private final BlockRepository blockRepository;

    public BlockService(BlockRepository blockRepository) {
        this.blockRepository = blockRepository;
    }

    @Cacheable(value = "blocks", key = "'all'")
    public List<Block> findAll() {
        log.info("Cache MISS — loading all blocks from DB");
        return blockRepository.findAll();
    }

    public Page<Block> findAll(Pageable pageable) {
        return blockRepository.findAll(pageable);
    }

    @Cacheable(value = "blocks", key = "#id")
    public Block findById(Integer id) {
        log.info("Cache MISS — loading block {} from DB", id);
        return blockRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Block", id));
    }

    @Cacheable(value = "blocks", key = "'name:' + #name")
    public Block findByName(String name) {
        log.info("Cache MISS — loading block by name '{}' from DB", name);
        return blockRepository.findFirstByNameAndEnabled(name, 1)
                .orElseThrow(() -> new ResourceNotFoundException("Block", name));
    }

    @Caching(evict = {
            @CacheEvict(value = "blocks", key = "'all'"),
            @CacheEvict(value = "blocks", allEntries = false, key = "'name:' + #block.name", condition = "#block.name != null")
    })
    @Transactional
    public Block create(Block block, Long userId) {
        Date now = new Date();
        block.setEnabled(1);
        block.setCreatedBy(userId);
        block.setCreatedAt(now);
        return blockRepository.save(block);
    }

    @Caching(evict = {
            @CacheEvict(value = "blocks", key = "#id"),
            @CacheEvict(value = "blocks", key = "'all'")
    })
    @Transactional
    public Block update(Integer id, Block block, Long userId) {
        Block existing = findByIdInternal(id);
        existing.setName(block.getName());
        if (block.getEnabled() != null) existing.setEnabled(block.getEnabled());
        existing.setModifiedBy(userId);
        existing.setModifiedAt(new Date());
        return blockRepository.save(existing);
    }

    @Caching(evict = {
            @CacheEvict(value = "blocks", key = "#id"),
            @CacheEvict(value = "blocks", key = "'all'")
    })
    @Transactional
    public void delete(Integer id) {
        Block existing = findByIdInternal(id);
        existing.setEnabled(0);
        blockRepository.save(existing);
    }

    private Block findByIdInternal(Integer id) {
        return blockRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Block", id));
    }

    public List<Object[]> countRequestsPerBlock() {
        return blockRepository.countRequestsPerBlock();
    }

    public List<Object[]> countStudentsPerBlock() {
        return blockRepository.countStudentsPerBlock();
    }
}
