package com.campus.dormitory.repository.jpa;

import com.campus.dormitory.model.Block;
import com.campus.dormitory.model.Floor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FloorRepository extends JpaRepository<Floor, Integer> {

    List<Floor> findByBlock(Block block);
    List<Floor> findByBlockAndEnabled(Block block, int enabled);
}
