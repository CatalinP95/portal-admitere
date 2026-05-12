package com.campus.admissions.repository;

import com.campus.admissions.model.AverageCompetition;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AverageCompetitionRepository extends JpaRepository<AverageCompetition, Integer> {
    AverageCompetition getAverageCompetitionByUserId(Long userId);
}
