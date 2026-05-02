package com.campus.admissions.service;

import com.campus.admissions.model.Highschool;
import com.campus.admissions.repository.HighschoolRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class HighschoolService {

    private final HighschoolRepository highschoolRepository;

    public HighschoolService(HighschoolRepository highschoolRepository) {
        this.highschoolRepository = highschoolRepository;
    }

    public List<Highschool> findAll() { return highschoolRepository.findAll(); }
    public Highschool findOne(Integer id) { return highschoolRepository.findById(id).orElse(null); }
    public Highschool save(Highschool highschool) { return highschoolRepository.save(highschool); }
}
