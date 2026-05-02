package com.campus.admissions.repository;

import com.campus.admissions.model.AnotherUniversity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AnotherUniversityRepository extends JpaRepository<AnotherUniversity, Integer> {
    List<AnotherUniversity> findByUserId(Long userId);
}
