package com.campus.userservice.dto.dormitory;

public class DormitoryAllocationResultDto {
    private Integer sessionId;
    private int totalProcessed;
    private int allocated;
    private int rejected;

    public DormitoryAllocationResultDto() {}

    public DormitoryAllocationResultDto(Integer sessionId, int totalProcessed, int allocated, int rejected) {
        this.sessionId = sessionId;
        this.totalProcessed = totalProcessed;
        this.allocated = allocated;
        this.rejected = rejected;
    }

    public Integer getSessionId() { return sessionId; }
    public void setSessionId(Integer sessionId) { this.sessionId = sessionId; }
    public int getTotalProcessed() { return totalProcessed; }
    public void setTotalProcessed(int totalProcessed) { this.totalProcessed = totalProcessed; }
    public int getAllocated() { return allocated; }
    public void setAllocated(int allocated) { this.allocated = allocated; }
    public int getRejected() { return rejected; }
    public void setRejected(int rejected) { this.rejected = rejected; }
}
