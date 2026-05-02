package com.campus.admissions.repository;

import com.campus.admissions.model.Faculty;
import com.campus.admissions.model.University;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FacultyRepository extends JpaRepository<Faculty, Integer> {

    Faculty findFacultyById(Integer id);
    Page<Faculty> findAll(Pageable pageable);
    List<Faculty> findByUniversity(University university);
}
