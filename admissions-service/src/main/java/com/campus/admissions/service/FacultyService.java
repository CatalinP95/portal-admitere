package com.campus.admissions.service;

import com.campus.admissions.model.Faculty;
import com.campus.admissions.model.University;
import com.campus.admissions.repository.FacultyRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FacultyService {

    private final FacultyRepository facultyRepository;

    public FacultyService(FacultyRepository facultyRepository) {
        this.facultyRepository = facultyRepository;
    }

    public List<Faculty> findAll() {
        return facultyRepository.findAll();
    }

    public List<Faculty> findByUniversity(University university) {
        return facultyRepository.findByUniversity(university);
    }

    public Faculty findById(Integer id) {
        return facultyRepository.findById(id).orElse(null);
    }

    public Boolean save(Faculty faculty) {
        facultyRepository.save(faculty);
        return true;
    }

    public void delete(Faculty faculty) {
        facultyRepository.delete(faculty);
    }
}
