package com.campus.admissions.repository;

import com.campus.admissions.model.StudentInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StudentInfoRepository extends JpaRepository<StudentInfo, Integer> {
    Optional<StudentInfo> findByUserId(Long userId);

}
