package com.campus.userservice.dto.algorithm;

public class AlgorithmResultDto {

    private Long sessionId;
    private int totalProcessed;
    private int approvedBuget;
    private int approvedTaxa;
    private int waitingList;

    public AlgorithmResultDto(Long sessionId, int totalProcessed,
                               int approvedBuget, int approvedTaxa, int waitingList) {
        this.sessionId = sessionId;
        this.totalProcessed = totalProcessed;
        this.approvedBuget = approvedBuget;
        this.approvedTaxa = approvedTaxa;
        this.waitingList = waitingList;
    }

    public Long getSessionId() { return sessionId; }
    public int getTotalProcessed() { return totalProcessed; }
    public int getApprovedBuget() { return approvedBuget; }
    public int getApprovedTaxa() { return approvedTaxa; }
    public int getWaitingList() { return waitingList; }
}
