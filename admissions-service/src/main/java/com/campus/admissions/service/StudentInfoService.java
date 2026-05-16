package com.campus.admissions.service;

import com.campus.admissions.dto.algorithm.AllocationDataDto;
import com.campus.admissions.model.AverageCompetition;
import com.campus.admissions.model.IdentityCard;
import com.campus.admissions.model.StudentInfo;
import com.campus.admissions.repository.AverageCompetitionRepository;
import com.campus.admissions.repository.IdentityCardRepository;
import com.campus.admissions.repository.StudentInfoRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class StudentInfoService {

    private final IdentityCardRepository identityCardRepository;
    private final StudentInfoRepository studentInfoRepository;
    private final AverageCompetitionRepository averageCompetitionRepository;

    public StudentInfoService(IdentityCardRepository identityCardRepository, StudentInfoRepository studentInfoRepository, AverageCompetitionRepository averageCompetitionRepository) {
        this.identityCardRepository = identityCardRepository;
        this.studentInfoRepository = studentInfoRepository;
        this.averageCompetitionRepository = averageCompetitionRepository;
    }

    public List<StudentInfo> findAll() { return studentInfoRepository.findAll(); }
    public StudentInfo findOne(Integer id) { return studentInfoRepository.findById(id).orElse(null); }
    public Optional<StudentInfo> findByUserId(Long userId) { return studentInfoRepository.findByUserId(userId); }
    public StudentInfo save(StudentInfo info) { return studentInfoRepository.save(info); }

    public AllocationDataDto getAllocationData(Long userId) {

        IdentityCard identityCard = identityCardRepository
                .findByCreatedBy(userId)
                .orElseThrow(() ->
                        new RuntimeException("Identity card not found"));

        StudentInfo studentInfo = studentInfoRepository
                .findByUserId(userId)
                .orElseThrow(() ->
                        new RuntimeException("Student info not found"));

        AverageCompetition averageCompetition =
                averageCompetitionRepository
                        .getAverageCompetitionByUserId(userId);

        if (averageCompetition == null) {
            throw new RuntimeException("Average competition not found");
        }

        return AllocationDataDto.builder()
                .sex(identityCard.getSex())
                .distanceKm(identityCard.getDistance())
                .medicalCondition(studentInfo.getMedicalCondition())
                .averageBac(averageCompetition.getAverageBac())
                .build();
    }
}
