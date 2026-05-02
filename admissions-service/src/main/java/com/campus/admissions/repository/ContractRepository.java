package com.campus.admissions.repository;

import com.campus.admissions.model.Contract;
import com.campus.admissions.model.Session;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ContractRepository extends JpaRepository<Contract, Integer> {

    List<Contract> findByUserIdAndEnabled(Long userId, Integer enabled);
    List<Contract> findTopByUserIdAndEnabledOrderByDateDesc(Long userId, int enabled);
    List<Contract> findByUserId(Long userId);
    Optional<Contract> findFirstByUserIdAndEnabled(Long userId, int enabled);
    Page<Contract> findAll(Pageable pageable);
}
