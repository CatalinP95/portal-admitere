package com.campus.dormitory.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class ApproveRequestDto {
    @NotNull(message = "bedId este obligatoriu")
    private Integer bedId;

    @NotNull(message = "priceId este obligatoriu")
    private Integer priceId;

    @NotNull(message = "adminId este obligatoriu")
    private Long adminId;
}
