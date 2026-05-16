package com.campus.userservice.dto.dormitory;

public class BedAllocationRequest {
    private Integer blockRequestId;
    private Integer roomType;
    private Double allocationScore;

    public BedAllocationRequest() {}

    public BedAllocationRequest(Integer blockRequestId, Integer roomType, Double allocationScore) {
        this.blockRequestId = blockRequestId;
        this.roomType = roomType;
        this.allocationScore = allocationScore;
    }

    public Integer getBlockRequestId() { return blockRequestId; }
    public void setBlockRequestId(Integer blockRequestId) { this.blockRequestId = blockRequestId; }
    public Integer getRoomType() { return roomType; }
    public void setRoomType(Integer roomType) { this.roomType = roomType; }
    public Double getAllocationScore() { return allocationScore; }
    public void setAllocationScore(Double allocationScore) { this.allocationScore = allocationScore; }
}
