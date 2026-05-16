package com.campus.userservice.dto.dormitory;

public class BlockRequestRankDto {
    private Integer blockRequestId;
    private Long userId;
    private String sex;
    private Double distanceKm;

    public BlockRequestRankDto() {}

    public Integer getBlockRequestId() { return blockRequestId; }
    public void setBlockRequestId(Integer blockRequestId) { this.blockRequestId = blockRequestId; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getSex() { return sex; }
    public void setSex(String sex) { this.sex = sex; }
    public Double getDistanceKm() { return distanceKm; }
    public void setDistanceKm(Double distanceKm) { this.distanceKm = distanceKm; }
}
