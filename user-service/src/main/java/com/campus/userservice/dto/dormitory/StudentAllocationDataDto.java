package com.campus.userservice.dto.dormitory;

public class StudentAllocationDataDto {
    private Long userId;
    private Float averageBac;
    private String medicalCondition;

    public StudentAllocationDataDto() {}

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Float getAverageBac() { return averageBac; }
    public void setAverageBac(Float averageBac) { this.averageBac = averageBac; }
    public String getMedicalCondition() { return medicalCondition; }
    public void setMedicalCondition(String medicalCondition) { this.medicalCondition = medicalCondition; }
}
