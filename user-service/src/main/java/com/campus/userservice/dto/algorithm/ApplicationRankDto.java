package com.campus.userservice.dto.algorithm;

public class ApplicationRankDto {

    private Long applicationId;
    private Long userId;
    private Integer facultyId;
    private Float averageBac;
    private Integer formFunding;

    public Long getApplicationId() { return applicationId; }
    public void setApplicationId(Long applicationId) { this.applicationId = applicationId; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Integer getFacultyId() { return facultyId; }
    public void setFacultyId(Integer facultyId) { this.facultyId = facultyId; }

    public Float getAverageBac() { return averageBac; }
    public void setAverageBac(Float averageBac) { this.averageBac = averageBac; }

    public Integer getFormFunding() { return formFunding; }
    public void setFormFunding(Integer formFunding) { this.formFunding = formFunding; }
}
