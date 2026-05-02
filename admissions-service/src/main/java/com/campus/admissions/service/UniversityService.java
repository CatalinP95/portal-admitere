package com.campus.admissions.service;

import com.campus.admissions.model.University;
import com.campus.admissions.repository.UniversityRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UniversityService {

    private final UniversityRepository universityRepository;

    public UniversityService(UniversityRepository universityRepository) {
        this.universityRepository = universityRepository;
    }

    public List<University> findAll() {
        return universityRepository.findAll();
    }

    public University findById(Integer id) {
        return universityRepository.findById(id).orElse(null);
    }

    public Boolean save(University university) {
        universityRepository.save(university);
        return true;
    }

    public void delete(University university) {
        universityRepository.delete(university);
    }
}
