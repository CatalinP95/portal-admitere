package com.campus.userservice.dto.dormitory;

import java.util.List;

public class BulkBedAllocationRequest {
    private List<BedAllocationRequest> allocations;

    public BulkBedAllocationRequest() {}

    public BulkBedAllocationRequest(List<BedAllocationRequest> allocations) {
        this.allocations = allocations;
    }

    public List<BedAllocationRequest> getAllocations() { return allocations; }
    public void setAllocations(List<BedAllocationRequest> allocations) { this.allocations = allocations; }
}
