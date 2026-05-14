package com.campus.dormitory.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class FloorDto {
    private Integer id;

    @NotBlank(message = "Numele etajului este obligatoriu")
    private String name;

    @NotNull(message = "blockId este obligatoriu")
    private Integer blockId;

    private Integer enabled;
}
