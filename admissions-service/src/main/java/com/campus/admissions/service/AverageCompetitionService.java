package com.campus.admissions.service;

import com.campus.admissions.model.AverageCompetition;
import com.campus.admissions.repository.AverageCompetitionRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class AverageCompetitionService {

    private final AverageCompetitionRepository averageCompetitionRepository;

    public AverageCompetitionService(AverageCompetitionRepository averageCompetitionRepository) {
        this.averageCompetitionRepository = averageCompetitionRepository;
    }

    public Optional<AverageCompetition> getByUserId(Long userId) {
        return Optional.of(averageCompetitionRepository.getAverageCompetitionByUserId(userId));
    }
    public List<AverageCompetition> findAll() { return averageCompetitionRepository.findAll(); }
    public AverageCompetition findOne(Integer id) { return averageCompetitionRepository.findById(id).orElse(null); }
    public AverageCompetition save(AverageCompetition avg) { return averageCompetitionRepository.save(avg); }
}
