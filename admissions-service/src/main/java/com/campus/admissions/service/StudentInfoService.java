package com.campus.admissions.service;

import com.campus.admissions.model.StudentInfo;
import com.campus.admissions.repository.StudentInfoRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class StudentInfoService {

    private final StudentInfoRepository studentInfoRepository;

    public StudentInfoService(StudentInfoRepository studentInfoRepository) {
        this.studentInfoRepository = studentInfoRepository;
    }

    public List<StudentInfo> findAll() { return studentInfoRepository.findAll(); }
    public StudentInfo findOne(Integer id) { return studentInfoRepository.findById(id).orElse(null); }
    public Optional<StudentInfo> findByUserId(Long userId) { return studentInfoRepository.findByUserId(userId); }
    public StudentInfo save(StudentInfo info) { return studentInfoRepository.save(info); }
}
