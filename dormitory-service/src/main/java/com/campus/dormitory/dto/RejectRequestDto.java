package com.campus.dormitory.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class RejectRequestDto {
    @NotBlank(message = "Motivul respingerii este obligatoriu")
    private String reason;

    @NotNull(message = "adminId este obligatoriu")
    private Long adminId;
}
