package com.campus.dormitory.service;

import com.campus.dormitory.exception.ResourceNotFoundException;
import com.campus.dormitory.model.Block;
import com.campus.dormitory.model.Price;
import com.campus.dormitory.repository.jpa.PriceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class PriceService {

    private final PriceRepository priceRepository;
    private final BlockService blockService;

    public PriceService(PriceRepository priceRepository, BlockService blockService) {
        this.priceRepository = priceRepository;
        this.blockService = blockService;
    }

    public List<Price> findAll() {
        return priceRepository.findAll();
    }

    public Price findById(Integer id) {
        return priceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Price", id));
    }

    public List<Price> findByBlockId(Integer blockId) {
        Block block = blockService.findById(blockId);
        return priceRepository.findByBlock(block);
    }

    @Transactional
    public Price create(String name, Float value, Integer blockId, Long userId) {
        Block block = blockService.findById(blockId);
        Price p = new Price();
        p.setName(name);
        p.setPrice(value);
        p.setBlock(block);
        p.setEnabled(1);
        p.setCreatedBy(userId);
        p.setCreatedAt(new Date());
        return priceRepository.save(p);
    }

    @Transactional
    public Price update(Integer id, String name, Float value, Integer blockId, Long userId) {
        Price existing = findById(id);
        if (name != null) existing.setName(name);
        if (value != null) existing.setPrice(value);
        if (blockId != null) existing.setBlock(blockService.findById(blockId));
        existing.setModifiedBy(userId);
        existing.setModifiedAt(new Date());
        return priceRepository.save(existing);
    }

    @Transactional
    public void delete(Integer id) {
        Price existing = findById(id);
        priceRepository.delete(existing);
    }
}
