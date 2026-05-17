package com.campus.userservice.dto.dormitory;

public class StudentAllocationDataDto {
    private String sex;
    private Double distanceKm;
    private String medicalCondition;
    private Float averageBac;

    public StudentAllocationDataDto() {}

    public String getSex() { return sex; }
    public void setSex(String sex) { this.sex = sex; }
    public Double getDistanceKm() { return distanceKm; }
    public void setDistanceKm(Double distanceKm) { this.distanceKm = distanceKm; }
    public String getMedicalCondition() { return medicalCondition; }
    public void setMedicalCondition(String medicalCondition) { this.medicalCondition = medicalCondition; }
    public Float getAverageBac() { return averageBac; }
    public void setAverageBac(Float averageBac) { this.averageBac = averageBac; }
}
