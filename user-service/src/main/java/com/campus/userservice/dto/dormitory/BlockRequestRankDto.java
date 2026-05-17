package com.campus.userservice.dto.dormitory;

public class BlockRequestRankDto {
    private Integer blockRequestId;
    private Long userId;

    public BlockRequestRankDto() {}

    public Integer getBlockRequestId() { return blockRequestId; }
    public void setBlockRequestId(Integer blockRequestId) { this.blockRequestId = blockRequestId; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
}
