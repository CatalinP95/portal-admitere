package com.campus.admissions.repository;

import com.campus.admissions.model.University;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UniversityRepository extends JpaRepository<University, Integer> {
    Page<University> findAll(Pageable pageable);
}
