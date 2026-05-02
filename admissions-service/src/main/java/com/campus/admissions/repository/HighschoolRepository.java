package com.campus.admissions.repository;

import com.campus.admissions.model.Highschool;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HighschoolRepository extends JpaRepository<Highschool, Integer> {
}
