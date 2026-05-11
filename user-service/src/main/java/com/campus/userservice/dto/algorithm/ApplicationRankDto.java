package com.campus.userservice.dto.algorithm;

public class ApplicationRankDto {

    private Long applicationId;
    private Long userId;
    private Integer facultyId;
    private Float averageBac;
    private Integer formFunding;
    private Float markDif1;
    private Float markDif2;
    private Float markDif3;

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

    public Float getMarkDif1() { return markDif1; }
    public void setMarkDif1(Float markDif1) { this.markDif1 = markDif1; }

    public Float getMarkDif2() { return markDif2; }
    public void setMarkDif2(Float markDif2) { this.markDif2 = markDif2; }

    public Float getMarkDif3() { return markDif3; }
    public void setMarkDif3(Float markDif3) { this.markDif3 = markDif3; }
}
