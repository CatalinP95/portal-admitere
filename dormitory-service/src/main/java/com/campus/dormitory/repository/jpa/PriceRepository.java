package com.campus.dormitory.repository.jpa;

import com.campus.dormitory.model.Block;
import com.campus.dormitory.model.Price;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PriceRepository extends JpaRepository<Price, Integer> {

    Price findPriceById(Integer id);
    List<Price> findByBlock(Block block);
}
