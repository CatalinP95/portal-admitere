package com.campus.userservice.dto.algorithm;

public class FacultyRankingResult {

    private Integer facultyId;
    private int totalCandidates;
    private int approvedBuget;
    private int approvedTaxa;
    private int waitingList;
    private double minimumScore;

    public FacultyRankingResult(Integer facultyId, int totalCandidates,
                                 int approvedBuget, int approvedTaxa,
                                 int waitingList, double minimumScore) {
        this.facultyId = facultyId;
        this.totalCandidates = totalCandidates;
        this.approvedBuget = approvedBuget;
        this.approvedTaxa = approvedTaxa;
        this.waitingList = waitingList;
        this.minimumScore = minimumScore;
    }

    public Integer getFacultyId() { return facultyId; }
    public int getTotalCandidates() { return totalCandidates; }
    public int getApprovedBuget() { return approvedBuget; }
    public int getApprovedTaxa() { return approvedTaxa; }
    public int getWaitingList() { return waitingList; }
    public double getMinimumScore() { return minimumScore; }
}
