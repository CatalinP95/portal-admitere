package com.campus.dormitory.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class CreateBlockRequestDto {
    @NotNull(message = "blockId este obligatoriu")
    private Integer blockId;

    @NotNull(message = "userId este obligatoriu")
    private Long userId;

    private Long contractId;
}
