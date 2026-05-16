package com.campus.userservice.client;

import com.campus.userservice.dto.dormitory.BlockRequestRankDto;
import com.campus.userservice.dto.dormitory.BulkBedAllocationRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class DormitoryClientFallback implements FallbackFactory<DormitoryClient> {

    private static final Logger log = LoggerFactory.getLogger(DormitoryClientFallback.class);

    @Override
    public DormitoryClient create(Throwable cause) {
        log.error("dormitory-service unavailable: {}", cause.getMessage());
        return new DormitoryClient() {
            @Override
            public List<BlockRequestRankDto> getPendingRequests(Integer sessionId) {
                throw new RuntimeException("dormitory-service indisponibil — nu se pot prelua cererile PENDING");
            }

            @Override
            public void bulkAllocate(BulkBedAllocationRequest request) {
                throw new RuntimeException("dormitory-service indisponibil — alocarea nu a putut fi salvata");
            }
        };
    }
}
