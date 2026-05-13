package com.campus.dormitory.dto;

import javax.validation.constraints.NotNull;

public class CreateBlockRequestDto {
    @NotNull(message = "blockId este obligatoriu")
    private Integer blockId;

    @NotNull(message = "userId este obligatoriu")
    private Long userId;

    private Long contractId;

    public CreateBlockRequestDto() {}

    public Integer getBlockId() { return blockId; }
    public void setBlockId(Integer blockId) { this.blockId = blockId; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Long getContractId() { return contractId; }
    public void setContractId(Long contractId) { this.contractId = contractId; }
}
