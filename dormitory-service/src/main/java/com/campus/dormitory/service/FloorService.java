package com.campus.dormitory.service;

import com.campus.dormitory.exception.ResourceNotFoundException;
import com.campus.dormitory.model.Block;
import com.campus.dormitory.model.Floor;
import com.campus.dormitory.repository.jpa.FloorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class FloorService {

    private final FloorRepository floorRepository;
    private final BlockService blockService;

    public FloorService(FloorRepository floorRepository, BlockService blockService) {
        this.floorRepository = floorRepository;
        this.blockService = blockService;
    }

    public List<Floor> findAll() { return floorRepository.findAll(); }

    public Floor findById(Integer id) {
        return floorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Floor", id));
    }

    public List<Floor> findByBlockId(Integer blockId) {
        Block block = blockService.findById(blockId);
        return floorRepository.findByBlockAndEnabled(block, 1);
    }

    @Transactional
    public Floor create(String name, Integer blockId, Long userId) {
        Block block = blockService.findById(blockId);
        Floor f = new Floor();
        f.setName(name);
        f.setBlock(block);
        f.setEnabled(1);
        f.setCreatedBy(userId);
        f.setCreatedAt(new Date());
        return floorRepository.save(f);
    }

    @Transactional
    public Floor update(Integer id, String name, Integer blockId, Long userId) {
        Floor existing = findById(id);
        existing.setName(name);
        if (blockId != null) existing.setBlock(blockService.findById(blockId));
        existing.setModifiedBy(userId);
        existing.setModifiedAt(new Date());
        return floorRepository.save(existing);
    }

    @Transactional
    public void delete(Integer id) {
        Floor existing = findById(id);
        existing.setEnabled(0);
        floorRepository.save(existing);
    }
}
