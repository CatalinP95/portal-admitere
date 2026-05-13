package com.campus.dormitory.repository.jpa;

import com.campus.dormitory.model.Block;
import com.campus.dormitory.model.BlockRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BlockRequestRepository extends JpaRepository<BlockRequest, Integer> {

    List<BlockRequest> findByUserIdAndEnabled(Long userId, int enabled);
    List<BlockRequest> findByStatus(String status);
    Page<BlockRequest> findByStatus(String status, Pageable pageable);
    List<BlockRequest> findByUserIdAndStatus(Long userId, String status);
    List<BlockRequest> findByBlockAndStatus(Block block, String status);
    BlockRequest findBlockRequestById(int id);
}
