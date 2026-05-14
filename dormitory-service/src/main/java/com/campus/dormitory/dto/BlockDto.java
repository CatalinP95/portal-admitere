package com.campus.dormitory.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class BlockDto {
    private Integer id;

    @NotBlank(message = "Numele caminului este obligatoriu")
    private String name;

    private Integer enabled;
}
