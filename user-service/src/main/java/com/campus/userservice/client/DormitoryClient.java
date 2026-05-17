package com.campus.userservice.client;

import com.campus.userservice.dto.dormitory.BulkBedAllocationRequest;
import com.campus.userservice.dto.dormitory.BlockRequestRankDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "dormitory-service", fallbackFactory = DormitoryClientFallback.class)
public interface DormitoryClient {

    @GetMapping("/internal/dormitory/requests/pending/{sessionId}")
    List<BlockRequestRankDto> getPendingRequests(@PathVariable("sessionId") Integer sessionId);

    @PutMapping("/internal/dormitory/allocate/bulk")
    void bulkAllocate(@RequestBody BulkBedAllocationRequest request);
}
